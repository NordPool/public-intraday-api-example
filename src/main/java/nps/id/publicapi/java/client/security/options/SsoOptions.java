package nps.id.publicapi.java.client.security.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SsoOptions {
    @Value("${sso.uri}")
    private String uri;
    @Value("${sso.clientId}")
    private String clientId;
    @Value("${sso.clientSecret}")
    private String clientSecret;
}
