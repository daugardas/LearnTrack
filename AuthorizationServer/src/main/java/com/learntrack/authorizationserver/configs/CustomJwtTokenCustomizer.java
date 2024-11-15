package com.learntrack.authorizationserver.configs;

import com.learntrack.authorizationserver.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    private final Logger logger = LoggerFactory.getLogger(CustomJwtTokenCustomizer.class);

    @Override
    public void customize(JwtEncodingContext context) {
        if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
            Authentication principal = context.getPrincipal();

            var identity = principal.getPrincipal();
            if (identity instanceof User user) {
                context.getClaims().claim("user_id", user.getId());
                context.getClaims().claim("roles", user.getRoleNames());
            } else {
                logger.error("Principal is not an instance of User: {}", identity);
            }
        }
    }

}
