package nps.id.publicapi.java.client.security.options;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CredentialsOptions {
    @Value("${credentials.userName}")
    private String userName;
    @Value("${credentials.password}")
    private String password;
}
