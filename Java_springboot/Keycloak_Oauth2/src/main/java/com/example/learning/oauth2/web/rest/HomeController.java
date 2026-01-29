package com.example.learning.oauth2.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
public class HomeController {


    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @GetMapping("")
    public String home(OAuth2AuthenticationToken auth2AccessToken)
    {
        logger.debug("First logs");
        String email = auth2AccessToken.getPrincipal().getAttribute("email");
        return "home: " + email;
    }
}
