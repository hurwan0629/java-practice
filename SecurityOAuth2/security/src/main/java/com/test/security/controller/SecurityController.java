package com.test.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/*
loginPage("/login-custom")
                    .loginProcessingUrl("/login-execute")
                    .defaultSuccessUrl("/login-success")
                    .failureUrl("/login-fail")
                    .usernameParameter("id")
                    .passwordParameter("pw")
                    .permitAll() // 폼 로그인에 대해서는 모든 여청 허가
            )
            .logout(logout -> logout
                    .logoutUrl("/logout-custom")
                    .logoutSuccessUrl("/logout-success")
                    .permitAll()
 */
@Controller
public class SecurityController {
    @GetMapping("/login-custom")
    public String loginCustom(){
        return "security/login-custom";
    }
    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication, Model model){
        System.out.println("/login success");
        model.addAttribute("loginUserName", authentication.getName());

        return "security/login-success";
    }
    @GetMapping("/login-fail")
    public String loginFail(){
        System.out.println("/login-fail");
        return "security/login-fail";
    }
//    @GetMapping("/logout-custom")
//    public String logoutCustom(){
//        return "security/logout-custom";
//    }
    @GetMapping("/logout-success")
    public String logoutSuccess(){
        return "security/logout-success";
    }
}
