package nps.id.publicapi.java.client.security.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenResponse {
    @JsonProperty("access_token")
    public String AccessToken;
    @JsonProperty("token_type")
    public String TokenType;
    @JsonProperty("expires_in")
    public int ExpiresIn;
}
