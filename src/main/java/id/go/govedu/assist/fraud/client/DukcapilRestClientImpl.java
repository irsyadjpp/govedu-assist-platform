package id.go.govedu.assist.fraud.client;

import id.go.govedu.assist.dto.ekyc.DukcapilRequest;
import id.go.govedu.assist.dto.ekyc.DukcapilResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Profile("prod")
public class DukcapilRestClientImpl implements DukcapilClient {

    private final RestTemplate restTemplate;
    private final String dukcapilBaseUrl;
    private final String clientId;
    private final String clientSecret;

    public DukcapilRestClientImpl(
            RestTemplate restTemplate,
            @Value("${dukcapil.api.base-url}") String dukcapilBaseUrl,
            @Value("${dukcapil.api.client-id}") String clientId,
            @Value("${dukcapil.api.client-secret}") String clientSecret
    ) {
        this.restTemplate = restTemplate;
        this.dukcapilBaseUrl = dukcapilBaseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public DukcapilResponse verifyIdentity(DukcapilRequest request) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Client-Id", clientId);
        headers.set("X-Client-Secret", clientSecret);

        var entity = new HttpEntity<>(request, headers);
        var url = dukcapilBaseUrl + "/penduduk/verifikasi";

        return restTemplate.postForObject(url, entity, DukcapilResponse.class);
    }
}
