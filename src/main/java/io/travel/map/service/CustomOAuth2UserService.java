package io.travel.map.service;

import io.travel.map.document.User;
import io.travel.map.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    // 생성자 주입
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * 실제 OAuth2 로그인 과정에서 호출되는 메서드
     * - userRequest: Provider(구글, 깃헙 등)로부터 받은 정보 + Access Token 등
     * - 반환값: OAuth2User (로그인 성공 후 SecurityContext에 저장될 사용자 정보)
     */
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Spring이 제공하는 기본 UserService를 통해 먼저 사용자 정보를 가져옴
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2. Provider(구글, 깃헙 등)에 따라 attributes에서 꺼낼 필드명이 다를 수 있음
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2 Provider에서 받은 attributes: {}", attributes);


        // 4. 구글이라면 "email", "name", "picture" 등 키를 사용
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String photoUrl = (String) attributes.get("picture");

        if (email == null) {
            throw new OAuth2AuthenticationException("이메일 정보가 없습니다. 다른 OAuth2 프로바이더를 확인하세요.");
        }


        log.info("추출된 email: {}, name: {}, picture: {}", email, name, photoUrl);

        // 5. DB에 해당 email이 이미 있는지 확인, 없으면 새로 생성
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUserName(name);
                    newUser.setPhotoUrl(photoUrl);
                    return userRepository.save(newUser);
                });


        // 6. SecurityContext에 저장할 수 있는 OAuth2User 객체를 반환
        //    - DefaultOAuth2User(기본 구현체)를 사용하거나, 직접 CustomOAuth2User를 구현 가능
        return new DefaultOAuth2User(
                Collections.emptyList(),  // 권한 목록(필요하다면 ROLE_USER 등 부여)
                attributes,              // OAuth2User의 주요 정보 (email, name, etc)
                "email"                    // attributes에서 '키'를 지정)
        );
    }


}

