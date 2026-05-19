package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// HTTP 요청을 받는 클래스인 HttpServlet를 상속받은 서블릿을 반들겠다는 선언
public class HelloServlet extends HttpServlet {
    
    // GET요청을 받는 메서드 (보통 GET청을 doGet로 받는다.)
    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        
        // HTTP Response Header 설정
        response.setContentType("text/plain; charset=UTF-8");
        
        // HTTP Response Body에 문자열 작성
        response.getWriter().println("Hello from classic Servlet");
        response.getWriter().println("Request URI: " + request.getRequestURI());
        response.getWriter().println("Method: " + request.getMethod());
        
        /**
         * [결과 예시]
         * HTTP/1.1 200 OK
         * Content-Type: text/plain; charset=UTF-8
         * Content-Length: 76
         * Date: Tue, 19 May 2026 00:30:00 GMT
         *
         * Hello from classic Servlet
         * Request URI: /simple-servlet-app/hello
         * Method: GET
         *
         * ---
         *
         * [Ethernet Frame]
         * └─ [IP Packet]
         *    └─ [TCP Segment]
         *       └─ [HTTP Response]
         *          ├─ HTTP/1.1 200 OK
         *          ├─ Content-Type: text/plain; charset=UTF-8
         *          └─ Body: Hello from classic Servlet ...
         */
    }
}