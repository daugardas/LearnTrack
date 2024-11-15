package com.learntrack.resourceserver.annotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserIdResolver implements HandlerMethodArgumentResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static Long getCurrentUserId(Authentication authentication) {
        CurrentUserIdResolver resolver = new CurrentUserIdResolver();
        try {
            return resolver.resolveArgument(null, null, null, null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasMethodAnnotation(CurrentUserId.class);
    }

    @Override
    public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            logger.info("No authenticated user found");
            return null;
        }

        Object principal = authentication.getPrincipal();

//        if (authentication instanceof BearerTokenAuthentication bearerAuth) {
//            try {
//                Map<String, Object> attributes = bearerAuth.getTokenAttributes();
//                Object userIdObject = attributes.get("user_id");
//
//                if (userIdObject instanceof Number) {
//                    return ((Number) userIdObject).longValue();
//                } else if (userIdObject instanceof String) {
//                    return Long.parseLong((String) userIdObject);
//                }
//
//                logger.warn("user_id claim is not in expected format: {}", userIdObject);
//            } catch (Exception e) {
//                logger.error("Error extracting user_id from token attributes: {}", e.getMessage());
//            }
//        }

        if (principal instanceof Jwt jwt) {
            try {
                logger.info("Checking principal '{}'", jwt);
                Object userIdObject = jwt.getClaims().get("user_id");

                if (userIdObject instanceof Number) {
                    return ((Number) userIdObject).longValue();
                } else if (userIdObject instanceof String) {
                    return Long.parseLong((String) userIdObject);
                }

                logger.error("User id is not a number or string");
                return null;
            } catch (Exception e) {
                logger.error("Error getting user id from JWT", e);
                return null;
            }
        }

        logger.debug("Unable to extract user_id. Authentication type: {}", authentication.getClass().getName());
        return null;
    }
}
