package id.go.govedu.assist.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

@Configuration
@RequiredArgsConstructor
public class SftpConfig {

    @Bean
    public DefaultSftpSessionFactory sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost("sftp.himbara.co.id");
        factory.setPort(22);
        factory.setUser("govedu_user");
        factory.setPassword("change-in-production");
        factory.setAllowUnknownKeys(true);
        return factory;
    }
}
