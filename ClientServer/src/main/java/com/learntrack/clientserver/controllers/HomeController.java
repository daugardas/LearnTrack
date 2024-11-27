package com.learntrack.clientserver.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class HomeController {

    private final WebClient webClient;

    public HomeController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/")
    public Mono<String> home(
            Model model,
            @RegisteredOAuth2AuthorizedClient("learntrack") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser oidcUser) {
        
        Mono<Long> courseCountMono = webClient
            .get()
            .uri("/api/v1/courses/count")
            .headers(headers -> headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
            .retrieve()
            .bodyToMono(Long.class);

        return courseCountMono.map(courseCount -> {
            model.addAttribute("userName", oidcUser.getFullName());
            model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientId());
            model.addAttribute("userAttributes", oidcUser.getClaims());
            model.addAttribute("courseCount", courseCount);
            model.addAttribute("enrolledCount", 0L);
            model.addAttribute("certificateCount", 0);
            return "home";
        });
    }
}
