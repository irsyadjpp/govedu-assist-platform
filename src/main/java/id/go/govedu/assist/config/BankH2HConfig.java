package id.go.govedu.assist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BankH2HConfig {

    @Value("${bank.himbara.base-url}")
    private String baseUrl;

    @Value("${bank.himbara.client-id}")
    private String clientId;

    @Value("${bank.himbara.client-secret}")
    private String clientSecret;

    @Value("${bank.himbara.partner-id}")
    private String partnerId;

    @Value("${bank.himbara.channel-id}")
    private String channelId;

    @Bean
    public RestClient bankHimbaraClient(HimbaraSecurityInterceptor securityInterceptor) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-PARTNER-ID", partnerId)
                .defaultHeader("CHANNEL-ID", channelId)
                .requestInterceptor(securityInterceptor)
                .build();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public String getChannelId() {
        return channelId;
    }
}
