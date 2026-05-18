<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>메인 페이지</title>
</head>
<body>
    <h1>메인 페이지입니다.</h1>
    <p>로그인 상태: ${isUserLoggedIn==null ? false : isUserLoggedIn}</p>
    <p>${securityContext}</p>
    <form id="logoutForm" action="/logout-custom" method="POST" style="display:none;">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    </form>
    <button type="button" onclick="document.getElementById('logoutForm').submit()">
        로그아웃
    </button>
</body>
</html>

