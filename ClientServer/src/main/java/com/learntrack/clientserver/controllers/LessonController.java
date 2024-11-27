package com.learntrack.clientserver.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.learntrack.clientserver.models.LessonDTO;
import com.learntrack.clientserver.models.ReviewResponseDTO;

import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@Controller
@RequestMapping("/courses/{courseId}/lessons")
public class LessonController {
    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(LessonController.class);

    public LessonController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private boolean hasRequiredRole(OidcUser user) {
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) user.getClaims().get("roles");
        return roles != null && (roles.contains("ROLE_LECTURER") || roles.contains("ROLE_ADMIN"));
    }

    @GetMapping
    public Mono<String> getLessons(
            @PathVariable Long courseId,
            Model model,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user) {
        logger.info("Fetching lessons for course with ID: {}", courseId);
        return webClient
                .get()
                .uri("/api/v1/courses/{courseId}/lessons", courseId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(Object[].class)
                .map(lessons -> {
                    model.addAttribute("courseId", courseId);
                    model.addAttribute("lessons", lessons);
                    model.addAttribute("canManageLessons", hasRequiredRole(user));
                    return "lessons";
                });
    }

    @PostMapping
    public Mono<String> createLesson(
            @PathVariable Long courseId,
            @RequestBody LessonDTO lessonDTO,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user) {
        if (!hasRequiredRole(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only lecturers and admins can create lessons");
        }

        logger.info("Creating lesson for course with ID: {}", courseId);
        return webClient
                .post()
                .uri("/api/v1/courses/{courseId}/lessons", courseId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .bodyValue(lessonDTO)
                .retrieve()
                .bodyToMono(Object.class)
                .thenReturn("redirect:/courses/" + courseId);
    }

    @DeleteMapping("/{lessonId}")
    public Mono<String> deleteLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user) {
        if (!hasRequiredRole(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only lecturers and admins can delete lessons");
        }

        logger.info("Deleting lesson with ID: {} for course with ID: {}", lessonId, courseId);
        return webClient
                .delete()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}", courseId, lessonId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .toBodilessEntity()
                .thenReturn("redirect:/courses/" + courseId);
    }

    @PutMapping("/{lessonId}")
    public Mono<String> updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody LessonDTO lessonDTO,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user) {
        if (!hasRequiredRole(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only lecturers and admins can update lessons");
        }

        logger.info("Updating lesson with ID: {} for course with ID: {}", lessonId, courseId);
        return webClient
                .put()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}", courseId, lessonId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .bodyValue(lessonDTO)
                .retrieve()
                .bodyToMono(Object.class)
                .thenReturn("redirect:/courses/" + courseId);
    }

    @GetMapping("/{lessonId}")
    public Mono<String> getLessonDetail(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            Model model,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser user) {
        logger.info("Fetching lesson detail for lesson ID: {} in course ID: {}", lessonId, courseId);
        
        Mono<Object> lessonMono = webClient
                .get()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}", courseId, lessonId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(Object.class);
                
        Mono<ReviewResponseDTO[]> reviewsMono = webClient
                .get()
                .uri("/api/v1/courses/{courseId}/lessons/{lessonId}/reviews", courseId, lessonId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(ReviewResponseDTO[].class);
                
        return Mono.zip(lessonMono, reviewsMono)
                .map(tuple -> {
                    Object lesson = tuple.getT1();
                    ReviewResponseDTO[] reviews = tuple.getT2();
                    
                    model.addAttribute("courseId", courseId);
                    model.addAttribute("lesson", lesson);
                    model.addAttribute("reviews", reviews);
                    model.addAttribute("canManageLessons", hasRequiredRole(user));
                    model.addAttribute("currentUserId", Long.parseLong(user.getAttributes().get("user_id").toString()));
                    return "lesson-detail";
                });
    }
}
