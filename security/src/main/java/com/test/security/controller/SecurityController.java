package com.test.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("/login-custom")
    public String loginCustom(){
        return "security/login-custom";
    }
    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication, Model model){
        System.out.println("/login success");
        if(authentication != null) {
            System.out.println("authentication is no null");
            model.addAttribute("loginUserName", authentication.getName());
        }
        return "security/login-success";
    }

    @GetMapping("/login-dashboard")
    public String loginDashboard(Model model){
        List<String> targetUsers = List.of("admin", "basic", "advanced", "pro", "ultimate");

        Set<String> loggedInUsers = this.sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof User)
                .map(principal -> ((User) principal).getUsername())
                .collect(Collectors.toSet());
        String usersStatus = targetUsers.stream()
                .map(username -> username + " : " + (loggedInUsers.contains(username) ? "LOGIN" : "LOGOUT"))
                .collect(Collectors.joining("\n"));
        model.addAttribute("usersStatus", usersStatus);
        System.out.println(usersStatus);
        return "security/login-dashboard";
    }

    @GetMapping("/basic")
    public String basicPage(){
        return "security/basic";
    }

    @GetMapping("/advanced")
    public String advancedPage(){
        return "security/advanced";
    }

    @GetMapping("/pro")
    public String proPage(){
        return  "security/pro";
    }
    @GetMapping("/ultimate")
    public String ultimatePage(){
        return "security/ultimate";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Authentication authentication, Model model) {
        if(authentication != null) {
            model.addAttribute("userAuthList",authentication.getAuthorities());
        }
        return "security/access-denied";
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
