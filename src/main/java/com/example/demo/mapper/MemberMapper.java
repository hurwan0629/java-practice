package com.example.demo.mapper;

import com.example.demo.domain.Member;
import com.example.demo.dto.MemberAuthInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

// 이 어노테이션을 통해 spring의 mybatis-spring-boot-starter이 알아서 잡아준다.
@Mapper
public interface MemberMapper {

    // @Select("SELECT member_pk, member_name, member_id, member_password_hash, member_email FROM member")
    List<Member> findAll();

    // @Select("SELECT member_pk, member_name, member_id, member_password_hash, member_email FROM member WHERE member_id=#{member_id}")
    Member findByPk(Long memberPk);

    Long findById(String memberId);

    Long checkMemberUniqueWithId(String memberId);

    MemberAuthInfo getMemberNameAndPasswordHashById(String memberId);

    int insert(Member member);
}
