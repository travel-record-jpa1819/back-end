package io.travel.map.controller;

import io.travel.map.entity.User;
import io.travel.map.service.CustomOAuth2UserService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    private final CustomOAuth2UserService userService;

    public ViewController(CustomOAuth2UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(OAuth2AuthenticationToken token, Model model){
        User user = userService.processOAuth2User(token); // DB에 저장

        //
        model.addAttribute("name", user.getName());
        model.addAttribute("email",user.getEmail() );
        model.addAttribute("picture", user.getPhotoUrl());
        return "user-profile";
    }
}