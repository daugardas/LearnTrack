package com.learntrack.authorizationserver.web.controllers;

import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class JwkSetController {

    private final JWKSet jwkSet;

    public JwkSetController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }


    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Map<String, Object>> getKeys() {
        return ResponseEntity.ok(this.jwkSet.toJSONObject());
    }
}
