/*
 *  Copyright 2017 Nord Pool.
 *  This library is intended to aid integration with Nord Pool’s Intraday API and comes without any warranty. Users of this library are responsible for separately testing and ensuring that it works according to their own standards.
 *  Please send feedback to idapi@nordpoolgroup.com.
 */

package com.nordpool.intraday.publicapi.example.service.security;

import com.nordpool.intraday.publicapi.example.service.connection.PropertyValidator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SSOService {
    private static final Logger LOGGER = LogManager.getLogger(SSOService.class);

    // SSO properties
    @Value("${sso.protocol}")
    private String protocol;
    @Value("${sso.host}")
    private String host;
    @Value("${sso.token.uri}")
    private String tokenUri;
    @Value("${sso.user}")
    private String user;
    @Value("${sso.password}")
    private String password;

    @Value("${sso.clientId}")
    private String clientId;
    @Value("${sso.clientSecret}")
    private String clientSecret;

    private String token = null;

    @Autowired
    private PropertyValidator validator;

    @PostConstruct
    public void validateProperties() {
        validator.validate(protocol, "sso.protocol");
        validator.validate(host, "sso.host");
        validator.validate(tokenUri, "sso.token.uri");
        validator.validate(user, "sso.user");
        validator.validate(password, "sso.password");
    }


    public String getToken() {
        return token;
    }

    public String getNewToken() throws IOException {

        String uri = protocol + "://" + host + tokenUri;
        LOGGER.info("Getting SSO token from " + uri + " for user '" + user + "' with password '"+ password+"'");
        HttpPost httppost = new HttpPost(uri);

        // if (StringUtils.isEmpty(token)) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                httppost.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                String auth = clientId + ":" + clientSecret;
                byte[] encodedAuth = Base64.encodeBase64(
                        auth.getBytes(Charset.forName("ISO-8859-1")));
                String authHeader = "Basic " + new String(encodedAuth);
                httppost.addHeader(HttpHeaders.AUTHORIZATION, authHeader);
                httppost.setEntity(new ByteArrayEntity(("grant_type=password&scope=intraday_api&username=" + user + "&password=" + password).getBytes()));
                CloseableHttpResponse response = httpClient.execute(httppost);

                Pattern p = Pattern.compile(Pattern.quote("\"access_token\":\"") + "(?<token>.*?)" + Pattern.quote("\",\""));
                Matcher m = p.matcher(EntityUtils.toString(response.getEntity()));
                while (m.find()) {
                    token = m.group("token");
                }

            }
        //}
        if (StringUtils.isEmpty(token)) {
            LOGGER.error("Haven't got correct token from SSO, empty reply");
        } else {
            LOGGER.info("Obtained SSO token successfully.");
        }

        return token;
    }


    public String getUser() {
        return user;
    }
}
