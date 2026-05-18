package com.test.security.controller;

import com.test.security.repository.MemberRepository;

public class MemberController {

    private final MemberRepository memberRepository;

    public MemberController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

}
