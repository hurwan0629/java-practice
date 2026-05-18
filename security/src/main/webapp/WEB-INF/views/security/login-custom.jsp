<%@ page contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>로그인을 해보거라</title>
</head>
<body>
<h2>로그인 폼</h2>
<form action="/login-execute" method="POST">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
    <input type="text" name="id" placeholder="id">
    <input type="password" name="pw" placeholder="password">
    <input type="submit" value="제출하셈">
</form>
</body>
</html>