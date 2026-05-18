package com.test.security.repository;

import com.test.security.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public class MemberRepository extends JpaRepository<Member, Long> {

}
