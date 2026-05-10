package com.example.demo.controller;

import com.example.demo.domain.Member;
import com.example.demo.dto.*;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
public class MemberController {
    //    private final MemberMapper memberMapper;
//
//    public MemberController(MemberMapper memberMapper) {
//        this.memberMapper = memberMapper;
//    }

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MemberService memberService;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;
    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @GetMapping
    public List<Member> findAll() {
        return memberMapper.findAll();
    }

    @GetMapping("/{pk}")
    public Member findByPk(@PathVariable Long pk) {
        return memberMapper.findByPk(pk);
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam("memberId") String memberId) {
        return ResponseEntity.ok(Map.of("duplicated", memberMapper.findById(memberId)>0));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody MemberLoginRequest request,
            HttpServletResponse servletResponse
        ) {
        System.out.println(request);
        MemberLoginResponse result
                = memberService.login(
                        request.getMemberId(),
                        request.getMemberPassword());

        ResponseCookie cookie = ResponseCookie
                .from("token", result.getJwtToken())
                .httpOnly(true)
                .sameSite(this.cookieSameSite)
                .secure(this.cookieSecure)
                .path("/")
                .maxAge(60*60)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "memberName", result.getMemberName(),
                        "memberPk", result.getMemberPk())
                );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie deleteJwtCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteJwtCookie.toString())
                .body(Map.of("message", "로그아웃 되었습니다"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Integer>> memberRegister(
            @RequestBody MemberRegisterRequest request) {
//        System.out.println(request);
//        System.out.println(member);

        return ResponseEntity.ok(Map.of("memberPk", this.memberService.register(request
        )));
    }

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> memberInfo(
            @RequestAttribute("memberPk") Long memberPk
    ) {

        return ResponseEntity.ok(memberMapper.getMemberInfoByPk(memberPk));
    }
}
