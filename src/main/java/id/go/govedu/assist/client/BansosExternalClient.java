package id.go.govedu.assist.client;

import id.go.govedu.assist.dto.external.BansosRequest;
import id.go.govedu.assist.dto.external.BansosResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BansosExternalClient {

    private final RestClient restClient;

    @Value("${bansos.api.base-url}")
    private String baseUrl;

    @Value("${bansos.api.api-key}")
    private String apiKey;

    public BansosResponse checkStatus(BansosRequest request) {
        return restClient.post()
                .uri(baseUrl + "/bansos/check-status")
                .header("X-Api-Key", apiKey)
                .body(request)
                .retrieve()
                .body(BansosResponse.class);
    }
}
