package com.example.demo.service;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/*
현재 jwt를 사용하여 일단 name정보를 토큰에 저장을 해보았습니다.
io.jsonwebtoken 0.13.0 버전을 이용하였으며 아래에 추가적인 주석을 달겠습니다.
 */
@Service
public class JwtService {
    // 특정 길이 이상의 시크릿 키만을 허용해주는 것을 확인하였습니다.
    @Autowired
    private SecretKey key;
    @Value("${app.jwt.expiration-millis}")
    private long expirationMillis;

    // 비밀키를 HMAC 서명에 사용할 수 있는 SecretKey 객체로 반환하는 과정을 가졌습니다.
    // JWT를 서명할 때 직접 문자열을 쓰지 않고, jjwt가 요구하는 안전한 키 타입으로 바꿔주게 됩니다.
//    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(Long pk) {
        long now = System.currentTimeMillis();

        // Jwts.builder()을 통해 JWT를 만들기 위한 빌더를 생성합니다.
        return Jwts.builder()
                // JWT의 sub claim을 설정합니다.
                .subject(String.valueOf(pk))
                // JWT가 발급된 시간을 iat claim으로 설정합니다.
                .issuedAt(new Date(now))
                // JWT 만료시간을 exp claim으로 설정합니다. (1시간)
                .expiration(new Date(now + 1000*60*60))
                 // 앞에서 만든 SecretKey로 JWT에 서명합니다.
                // 이 서명이 있어야 나중에 토큰이 위조되지 않았는지 검증할 수 있습니다.
                .signWith(key)
                // 설정한 claim과 서명을 바탕으로 최종 JWT 문자열을 생성합니다.
                .compact();

    }

    public String getMemberPk(String token) {
        // (javax.SecretKey) KEY를 통해 JWT문자열을 해석하고 검증하기 위한 parser을 생성해줍니다.
        return Jwts.parser()
                .verifyWith(key)
                .build()
                // 서명된 JWT를 파싱하고 서명을 검증합니다.
                .parseSignedClaims(token)
                // JWT의 payload (claim이 들어있는 부분)을 가져옵니다.
                .getPayload()
                // payload 안의 sub claim 값을 가져옵니다.
                .getSubject();
    }
}
