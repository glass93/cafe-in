package com.cafein.serviceImpl.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cafein.dao.UserRepository;
import com.cafein.dto.user.signin.OauthOutput;
import com.cafein.dto.user.signin.SignInOutput;
import com.cafein.dto.user.signin.SocialLoginType;
import com.cafein.entity.User;
import com.cafein.response.Response;
import com.cafein.service.JwtService;
import com.cafein.service.social.SocialOauth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cafein.response.ResponseStatus.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOauth implements SocialOauth {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final String KAKAO_OAUTH_BASE_URL = "https://kauth.kakao.com/oauth/authorize";
    private final String KAKAO_OAUTH_TOKEN_BASE_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_OAUTH_RESOURCE_BASE_URL = "https://kapi.kakao.com/v2/user/me";
    private final String KAKAO_OAUTH_CALLBACK_URL = "http://i5b307.p.ssafy.io/auth/kakao/callback";

    @Value("${custom.oauth2.kakao.client.id}")
    private String KAKAO_OAUTH_CLIENT_ID;

    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", "code");
        params.put("client_id", KAKAO_OAUTH_CLIENT_ID);
        params.put("redirect_uri", KAKAO_OAUTH_CALLBACK_URL);

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
        return KAKAO_OAUTH_BASE_URL + "?" + parameterString;
    }

    @Override
    public Response<SignInOutput> requestLogin(String code) {
        // 1. code 로 Access token 정보 요청
        String kakaoAccessToken = this.requestAccessToken(code);
        if(kakaoAccessToken == null) {
            log.error("[auth/kakao/callback/get] UNAUTHORIZED ACCESS TOKEN error");
            return new Response<>(UNAUTHORIZED_TOKEN);
        }
        // 2. Access Token 으로 사용자 정보 반환
        JsonNode userInfo = this.requestUserInfo(kakaoAccessToken);
        if(userInfo == null) {
            log.error("[auth/kakao/callback/get] UNAUTHORIZED ACCESS TOKEN error");
            return new Response<>(UNAUTHORIZED_TOKEN);
        }
        String kakaoId = userInfo.get("id").asText();
        String nickname = userInfo.get("kakao_account").get("profile").get("nickname").asText();
        String image = null;
        if (userInfo.get("kakao_account").get("profile").has("profile_image_url")) {
            image = userInfo.get("kakao_account").get("profile").get("profile_image_url").toString();
            image = image.substring(1, image.length() - 1);
            String temp = image.substring(0, 4);
            String temp2 = image.substring(4);
            image = temp + "s" + temp2; // https 작업
        }
        String email = null;
        if (userInfo.get("kakao_account").has("email")) {
            email = userInfo.get("kakao_account").get("email").asText();
        }
        // 3. user 정보 가져오기
        User userDB;
        try {
            User user = userRepository.findByEmailAndStatus(email, "ACTIVATE").orElse(null);
            if (user == null) { // 최초 로그인
                SignInOutput oauthOutput =
                        SignInOutput.builder()
                                .oauth(
                                        OauthOutput.builder()
                                                .type(SocialLoginType.KAKAO)
                                                .id(kakaoId)
                                                .email(email)
                                                .nickname(nickname)
                                                .image(image)
                                                .build()).build();
                return new Response<>(oauthOutput, NEED_SIGNUP);
            }
            if (!StringUtils.isNotEmpty(user.getOauthId())) { // 기존에 이메일로 가입했을 경우
                user.setOauth(SocialLoginType.KAKAO);
                user.setOauthId(kakaoId);
                userDB = userRepository.save(user);
            } else {
                userDB = user;
            }
        } catch (Exception e) {
            log.error("[auth/kakao/callback/get] database error", e);
            return new Response<>(DATABASE_ERROR);
        }
        // 4. access token 생성
        String accessToken;
        try {
            accessToken = jwtService.createAccessToken(userDB.getId());
            if (accessToken.isEmpty()) {
                return new Response<>(FAILED_TO_CREATE_TOKEN);
            }
        } catch (Exception e) {
            return new Response<>(FAILED_TO_CREATE_TOKEN);
        }
        // 5. 결과 return
        SignInOutput signInOutput = SignInOutput.builder().
                userId(userDB.getId())
                .accessToken(accessToken)
                .oauth(
                        OauthOutput.builder()
                                .type(SocialLoginType.KAKAO)
                                .id(kakaoId)
                                .email(email)
                                .nickname(nickname)
                                .image(image)
                                .build()).build();
        return new Response<>(signInOutput, SUCCESS_SIGN_IN);
    }

    @Override
    public String requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8"); //헤더지정

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        params.set("code", code);
        params.set("client_id", KAKAO_OAUTH_CLIENT_ID);
        params.set("redirect_uri", KAKAO_OAUTH_CALLBACK_URL);
        params.set("grant_type", "authorization_code");

        String accessToken = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(KAKAO_OAUTH_TOKEN_BASE_URL, restRequest, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                accessToken = mapper.readTree(responseEntity.getBody()).get("access_token").toString();
            }
        } catch (IOException e) {
            log.error("[auth/kakao] database error", e);
        }
        return accessToken;
    }

    @Override
    public JsonNode requestUserInfo(String accessToken) {
        // 카카오 서버에 요청
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(KAKAO_OAUTH_RESOURCE_BASE_URL);

        post.addHeader("Authorization", "Bearer " + accessToken); // add header

        JsonNode returnNode = null;
        try {
            final HttpResponse response = client.execute(post);
            final int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode == 200) {
                ObjectMapper mapper = new ObjectMapper();
                returnNode = mapper.readTree(response.getEntity().getContent());
            }
        } catch (IOException e) {
            log.error("[auth/kakao] database error", e);
        }
        return returnNode;
    }
}
