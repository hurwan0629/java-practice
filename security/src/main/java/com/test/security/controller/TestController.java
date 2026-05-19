package com.test.security.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class TestController {

    @GetMapping("/")
    public String home(Principal principal, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isUserLoggedIn =
                authentication != null &&
                        authentication.isAuthenticated() &&
                        !(authentication instanceof AnonymousAuthenticationToken);

        model.addAttribute("isUserLoggedIn", isUserLoggedIn);
        model.addAttribute("securityContext", SecurityContextHolder.getContext().toString());
        return "main";
    }
    
    @ResponseBody
    @GetMapping("/public")
    public String publicPage() {
        return "public - 누구나 접근 가능";
    }
    
    @ResponseBody
    @GetMapping("/private")
    public String privatePage() {
        return "private - 로그인 필요";
    }
    
    @ResponseBody
    @GetMapping("/principal")
    public String principalForm(Principal principal) {
        if(principal == null) {
            return "";
        }
        return principal.toString();
    }
    
    @ResponseBody
    @GetMapping("/security-context")
    public String showSecurityContextForm() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if(authentication == null) {
            return "no Authentication";
        }

        return """
                SecurityContext class = %s
                Authentication class = %s
                name = %s
                principal class = %s
                principal = %s
                authorities = %s
                authenticated = %s
                """.formatted(
                context.getClass().getName(),
                authentication.getClass().getName(),
                authentication.getName(),
                authentication.getPrincipal().getClass().getName(),
                authentication.getPrincipal(),
                authentication.getAuthorities(),
                authentication.isAuthenticated()
        );
    }

}
