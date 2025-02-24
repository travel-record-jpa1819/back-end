package io.travel.map.service;

import io.travel.map.entity.User;
import io.travel.map.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CustomOAuth2UserService {

    private final UserRepository userRepository;

    // 생성자 주입
    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional // 구글 로그인을 처리하는 로직 서비스
    public User processOAuth2User(OAuth2AuthenticationToken token){
        OAuth2User oAuth2User = token.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String  email = (String)attributes.get("email");
        String  name = (String)attributes.get("name");
        String  photoUrl = (String)attributes.get("picture");


        //이메일이 DB에 존재하는지 확인
        // 이미 존재하는 유저라면 기존 데이터를 반환
        // 존재하지 않는다면(orElseGet) 새 유저를 생성하여 데이터베이스에 저장
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setPhotoUrl(photoUrl);
                    return userRepository.save(newUser);
                });
    }


}

