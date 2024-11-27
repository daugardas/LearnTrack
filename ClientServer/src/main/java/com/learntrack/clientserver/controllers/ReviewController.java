package com.learntrack.clientserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.learntrack.clientserver.models.ReviewDTO;

import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/courses/{courseId}/lessons/{lessonId}/reviews")
public class ReviewController {
    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    public ReviewController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private boolean hasRequiredRole(OidcUser user) {
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) user.getClaims().get("roles");
        return roles != null && (roles.contains("ROLE_LECTURER") || roles.contains("ROLE_ADMIN"));
    }

    @PostMapping
    public Mono<String> createReview(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody ReviewDTO reviewDTO,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient) {
        logger.info("Creating review for lesson ID: {} in course ID: {}", lessonId, courseId);
        return webClient
                .post()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews", courseId, lessonId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .bodyValue(reviewDTO)
                .retrieve()
                .bodyToMono(Object.class)
                .thenReturn("redirect:/courses/" + courseId + "/lessons/" + lessonId);
    }

    @PutMapping("/{reviewId}")
    public Mono<String> updateReview(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @PathVariable Long reviewId,
            @RequestBody ReviewDTO reviewDTO,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient) {

        logger.info("Updating review ID: {} for lesson ID: {} in course ID: {}", reviewId, lessonId, courseId);
        return webClient
                .put()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews/{reviewId}", courseId, lessonId, reviewId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .bodyValue(reviewDTO)
                .retrieve()
                .bodyToMono(Object.class)
                .thenReturn("redirect:/courses/" + courseId + "/lessons/" + lessonId);
    }

    @DeleteMapping("/{reviewId}")
    public Mono<String> deleteReview(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @PathVariable Long reviewId,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user) {
        logger.info("Deleting review ID: {} for lesson ID: {} in course ID: {}", reviewId, lessonId, courseId);
        return webClient
                .delete()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews/{reviewId}", courseId, lessonId, reviewId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .toBodilessEntity()
                .thenReturn("redirect:/courses/" + courseId + "/lessons/" + lessonId);
    }
}
