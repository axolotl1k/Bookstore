package com.example.bookstore.service.impl;

import com.example.bookstore.model.Role;
import com.example.bookstore.model.User;
import com.example.bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final RestTemplate restTemplate;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        var oauth2User = new DefaultOAuth2UserService().loadUser(request);

        String email = oauth2User.<String>getAttribute("email");
        if (email == null) {
            email = fetchPrimaryEmail(request);
        }

        String finalEmail = email;
        User user = userRepo.findByEmail(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername(oauth2User.getAttribute("login"));
                    u.setEmail(finalEmail);
                    u.setPassword(encoder.encode(UUID.randomUUID().toString()));
                    u.setRole(Role.USER);
                    return userRepo.save(u);
                });

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        Map<String,Object> mappedAttrs = new HashMap<>(oauth2User.getAttributes());
        mappedAttrs.put("email", email);

        return new DefaultOAuth2User(
                authorities,
                mappedAttrs,
                "email"
        );
    }

    private String fetchPrimaryEmail(OAuth2UserRequest req) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(req.getAccessToken().getTokenValue());
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String,Object>>> resp = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return resp.getBody().stream()
                .filter(m -> Boolean.TRUE.equals(m.get("primary")))
                .map(m -> (String) m.get("email"))
                .findFirst()
                .orElseThrow(() ->
                        new OAuth2AuthenticationException("Не отримано основної електронної адреси з GitHub"));
    }
}
