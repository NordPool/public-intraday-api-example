package nps.id.publicapi.java.client.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nps.id.publicapi.java.client.connection.messages.Headers;
import lombok.Getter;
import nps.id.publicapi.java.client.security.exceptions.TokenRequestFailedException;
import nps.id.publicapi.java.client.security.options.CredentialsOptions;
import org.springframework.stereotype.Service;
import nps.id.publicapi.java.client.security.options.SsoOptions;
import nps.id.publicapi.java.client.security.responses.AccessTokenResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SsoService {
    private final String grantType;
    private final String scope;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final SsoOptions ssoOptions;

    private final CredentialsOptions credentialsOptions;

    public SsoService(HttpClient httpClient, ObjectMapper objectMapper, SsoOptions ssoOptions, CredentialsOptions credentialsOptions)
    {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.ssoOptions = ssoOptions;
        this.credentialsOptions = credentialsOptions;
        grantType = "password";
        scope = "global";
    }

    public String getAuthToken() throws TokenRequestFailedException
    {
        var uri = URI.create(ssoOptions.getUri());

        try
        {
            var auth = ssoOptions.getClientId() + ":" + ssoOptions.getClientSecret();
            var encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));

            var payload = constructPayloadForTokenRequest(credentialsOptions.getUserName(), credentialsOptions.getPassword());
            var httpRequest = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .uri(uri)
                    .setHeader(Headers.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .setHeader(Headers.AUTHORIZATION, "Basic " + new String(encodedAuth))
                    .build();

            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), AccessTokenResponse.class).getAccessToken();
        } catch (JsonProcessingException e) {
            throw new TokenRequestFailedException("Failed to read auth token response body!", e);
        } catch(InterruptedException | IOException e) {
            throw new TokenRequestFailedException("Failed to retrieve auth token! Check username and password!", e);
        }
    }

    private String constructPayloadForTokenRequest(String userName, String password) {
        return "grant_type=" + grantType + "&scope=" + scope + "&username=" + userName + "&password=" + URLEncoder.encode(password, StandardCharsets.UTF_8);
    }
}