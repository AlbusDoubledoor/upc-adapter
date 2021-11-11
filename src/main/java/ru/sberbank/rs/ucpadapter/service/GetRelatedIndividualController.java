package ru.sberbank.rs.ucpadapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.sberbank.rs.ucpadapter.config.Replacements;
import ru.sberbank.rs.ucpadapter.model.synapse.header.SynapseHeaders;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GetRelatedIndividualController {

    @Value("#{@extServices.getRelatedIndividual.name}")
    private String serviceName;

    @Value("#{@extServices.getRelatedIndividual.url}")
    private String endPoint;

    @Value("#{@extServices.getRelatedIndividual.transformResponse}")
    private boolean transformResponse;

    @Value("#{@extServices.getRelatedIndividual.timeout}")
    private long timeout;

    private final Replacements replacementList;

    @RequestMapping(value = "/getRelatedIndividual",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRelatedIndividual(HttpEntity<String> httpEntity) {
        String rquid = httpEntity.getHeaders().getFirst(SynapseHeaders.RQUID);
        log.info("[{}] HTTP Initial Request is received", rquid);

        RestTemplate restTemplate = new RestTemplateBuilder().setReadTimeout(Duration.ofSeconds(timeout)).build();

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(endPoint, HttpMethod.POST, httpEntity, String.class);
        } catch (RestClientResponseException clientex) {
            // Пробрасываем все ошибки от конечного сервиса в ответ
            log.error("[{}] HTTP Outbound Request failed with code {}", rquid, clientex.getRawStatusCode());
            return ResponseEntity.status(clientex.getRawStatusCode()).body(clientex.getResponseBodyAsString());
        } catch (ResourceAccessException raex) {
            // Если получили таймаут по чтению, то отвечаем таймаутом
            log.error("[{}] HTTP Outbound Request failed with timeout", rquid);
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        }

        if (response.getBody() == null || response.getBody().length() == 0) {
            log.error("[{}] HTTP Response returned no content", rquid);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        String rsBody = response.getBody();

        if (transformResponse) {
            for (Replacements.Replacement rpc : replacementList.getReplacementList()) {
                if (rpc.getService().equals(serviceName)) {
                    rsBody = rsBody.replaceAll(rpc.getSource(), rpc.getTarget());
                }
            }
        }

        log.info("[{}] HTTP Response returns OK", rquid);
        return ResponseEntity.ok().body(rsBody);
    }
}
