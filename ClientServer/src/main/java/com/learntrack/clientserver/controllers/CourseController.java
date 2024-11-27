package com.learntrack.clientserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/courses")
public class CourseController {
    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);

    public CourseController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }

    private boolean hasRequiredRole(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) oidcUser.getClaims().get("roles");
            return roles != null && (roles.contains("ROLE_LECTURER") || roles.contains("ROLE_ADMIN"));
        }
        return false;
    }

    @GetMapping
    public Mono<String> courses(
            Model model,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        return webClient
                .get()
                .uri("/api/v1/courses")
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(Object[].class)
                .map(courses -> {
                    model.addAttribute("courses", courses);
                    model.addAttribute("canManageCourses", hasRequiredRole(authentication));
                    return "courses";
                });
    }

    @PostMapping
    public Mono<String> createCourse(
            @RequestBody Map<String, String> courseData,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        if (!hasRequiredRole(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only lecturers and admins can create courses");
        }

        return webClient
                .post()
                .uri("/api/v1/courses")
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .bodyValue(courseData)
                .retrieve()
                .bodyToMono(Object.class)
                .thenReturn("redirect:/courses");
    }

    @DeleteMapping("/{courseId}")
    public Mono<String> deleteCourse(
            @PathVariable Long courseId,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        if (!hasRequiredRole(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only lecturers and admins can delete courses");
        }

        return webClient
                .delete()
                .uri("/api/v1/courses/{courseId}", courseId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .toBodilessEntity()
                .thenReturn("redirect:/courses");
    }

    @PutMapping("/{courseId}")
    public Mono<String> updateCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, String> courseData,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        if (!hasRequiredRole(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only lecturers and admins can update courses");
        }

        return webClient
                .put()
                .uri("/api/v1/courses/{courseId}", courseId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .bodyValue(courseData)
                .retrieve()
                .bodyToMono(Object.class)
                .thenReturn("redirect:/courses");
    }

    @GetMapping("/{courseId}")
    public Mono<String> getCourseDetail(
            @PathVariable Long courseId,
            Model model,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @CurrentSecurityContext(expression = "authentication") Authentication authentication) {
        logger.info("Fetching course detail for course ID: {}", courseId);
        
        Mono<Object> courseMono = webClient
                .get()
                .uri("/api/v1/courses/{courseId}", courseId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(Object.class);
                
        Mono<Object[]> lessonsMono = webClient
                .get()
                .uri("/api/v1/courses/{courseId}/lessons", courseId)
                .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .bodyToMono(Object[].class);
                
        return Mono.zip(courseMono, lessonsMono)
                .map(tuple -> {
                    Object course = tuple.getT1();
                    Object[] lessons = tuple.getT2();
                    
                    model.addAttribute("course", course);
                    model.addAttribute("lessons", lessons);
                    model.addAttribute("canManageCourses", hasRequiredRole(authentication));
                    return "course-detail";
                });
    }
}
