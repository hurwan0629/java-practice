package com.example.demo.controller;

import com.example.demo.domain.Member;
import com.example.demo.dto.MemberAuthInfo;
import com.example.demo.dto.MemberInfoResponse;
import com.example.demo.dto.MemberLoginRequest;
import com.example.demo.dto.MemberRegisterRequest;
import com.example.demo.exception.LoginFailedException;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.service.JwtService;
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
    private JwtService jwtService;

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
        Long memberCount = memberMapper.checkMemberUniqueWithId(request.getMemberId());
        System.out.println(memberCount);
        if(memberCount == 0) {
            throw new LoginFailedException("아이디가 잘못되었습니다.");
        }
        else if(memberCount == 1) {
            MemberAuthInfo memberAuthInfo = memberMapper.getMemberNameAndPasswordHashById(request.getMemberId());
            System.out.println(memberAuthInfo);
            if(!passwordEncoder.matches(request.getMemberPassword(), memberAuthInfo.getMemberPasswordHash())) {
                throw new LoginFailedException("비밀번호가 잘못되었습니다.");
            }
            // API 토큰 생성 후 Cookie로 빌드
            String token = jwtService.createToken(memberAuthInfo.getMemberPk());
            ResponseCookie jwtCookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .sameSite(this.cookieSameSite)
                    .secure(this.cookieSecure)
                    .path("/")
                    .maxAge(60*60)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(Map.of(
                            "memberName", memberAuthInfo.getMemberName(),
                            "memberPk", memberAuthInfo.getMemberPk()));
        }
        else {
            throw new LoginFailedException("아이디가 2개 이상 존재합니다.");
        }
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
            @RequestBody MemberRegisterRequest request,
            Member member) {
//        System.out.println(request);
        String encodedPassword = passwordEncoder.encode(request.getMemberPassword());

        member.setMemberName(request.getMemberName());
        member.setMemberId(request.getMemberId());
        member.setMemberPasswordHash(encodedPassword);
        member.setMemberEmail(request.getMemberEmail());

//        System.out.println(member);

        return ResponseEntity.ok(Map.of("memberPk", memberMapper.insert(member)));
    }

    @GetMapping("/me")
    public ResponseEntity<MemberInfoResponse> memberInfo(
            @RequestAttribute("memberPk") Long memberPk
    ) {

        return ResponseEntity.ok(memberMapper.getMemberInfoByPk(memberPk));
    }
}
