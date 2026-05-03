package com.example.demo.controller;

import com.example.demo.domain.Member;
import com.example.demo.dto.MemberAuthInfo;
import com.example.demo.dto.MemberLoginRequest;
import com.example.demo.dto.MemberLoginResponse;
import com.example.demo.dto.MemberRegisterRequest;
import com.example.demo.exception.LoginFailedException;
import com.example.demo.mapper.MemberMapper;
import com.example.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<MemberLoginResponse> login(
            @RequestBody MemberLoginRequest request,
            MemberLoginResponse response
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

            response.setAccessToken(JwtService.createToken(memberAuthInfo.getMemberPk()));
            response.setMemberName(memberAuthInfo.getMemberName());

            return ResponseEntity.ok(response);
        }
        else {
            throw new LoginFailedException("아이디가 2개 이상 존재합니다.");
        }
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
}
