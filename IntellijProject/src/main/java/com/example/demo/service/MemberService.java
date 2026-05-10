package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.dto.MemberAuthInfo;
import com.example.demo.dto.MemberLoginResponse;
import com.example.demo.dto.MemberRegisterRequest;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MemberController중에서 좀 더 유지보수, 등을 할만한 로직들을 이곳으로 옮기겠습니다.
 */
@Service
public class MemberService {

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;
    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    // findByPk는 Member 객체를 주며 현재 사용중이지 앟기때문에 버리기.
    // findAll은 하는게 거의 없기때문에 그냥 그대로
    // checkDuplicate도 코드가 어렵지 않아서 그대로

    // login은 복잡하기 때문에 인자로 받아서 처리
    // login의 순서는
    // 0. AuthInterceptor에서 인증 절차 거치기
    // 1. 아이디가 존재하는지 확인 (memberCount 를 통해 확인)
    // 1-2. 동일 아이디가 2개 이상 있으면 에러 발생 (401)
    // 2. 비밀번호 확인 (bcrypt.matches 사용)
    // 3-1. jwt토큰 생성 -> Reseponse에 추가
    // 3-2. 응답 데이터 추가
    
    public MemberLoginResponse login(String memberId, String memberPassword) {
        MemberLoginResponse result = new MemberLoginResponse();

        Long memberCount = memberMapper.checkMemberUniqueWithId(memberId);
        if(memberCount == 0) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED, Map.of(
                    "resource", "memberId",
                    "action", "select",
                    "reason", "아이디가 잘못되었습니다."
            ));
        }
        else if(memberCount == 1) {
            MemberAuthInfo memberAuthInfo = memberMapper.getMemberNameAndPasswordHashById(memberId);
            result.setMemberPk(memberAuthInfo.getMemberPk());
            result.setMemberName(memberAuthInfo.getMemberName());

            System.out.println(memberAuthInfo);
            if(!passwordEncoder.matches(memberPassword, memberAuthInfo.getMemberPasswordHash())) {
                throw new BusinessException(ErrorCode.LOGIN_FAILED, Map.of(
                        "resource", "memberPassword",
                        "action", "select",
                        "reason", "비밀번호가 잘못되었습니다."
                ));
            }
            // API 토큰 생성 후 Cookie로 빌드
            String token = jwtService.createToken(memberAuthInfo.getMemberPk());
            result.setJwtToken(token);

            return result;
        }
        else {
            throw new BusinessException(ErrorCode.ID_CONFLICT, Map.of(
                    "resource", "memberId",
                    "action", "select",
                    "reason", "아이디가 중복 되었습니다."
            ));
        }
    }
    
    // logout도 그냥 빈 쿠키 주는것이기 때문에 그대로

    // register
    // 비밀번호 인코딩
    //
    public int register(MemberRegisterRequest request) {
        Long memberCount = memberMapper.checkMemberUniqueWithId(request.getMemberId());
        if(memberCount == 0) {
            Member member = new Member();
            member.setMemberName(request.getMemberName());
            member.setMemberId(request.getMemberId());
            member.setMemberPasswordHash(this.passwordEncoder.encode(request.getMemberPassword()));
            member.setMemberEmail(request.getMemberEmail());

            return memberMapper.insert(member);

        } else {
            throw new BusinessException(ErrorCode.ID_CONFLICT, Map.of(
                    "resource", "memberId",
                    "action", "select",
                    "reason", "이미 존재하는 아이디입니다. 다른 아이디를 시도해주세요"
            ));
        }
    }
}
