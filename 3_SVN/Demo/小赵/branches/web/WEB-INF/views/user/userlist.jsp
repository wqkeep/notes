<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/3/18 0018
  Time: 18:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
用户列表界面
<table border="1px">
    <tr>
        <td>姓名</td>
        <td>年龄</td>
        <td>性别</td>
        <td>生日</td>
        <td>设置</td>
        <td>设置2</td>
    </tr>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td>${user.username}</td>
            <td>${user.age}</td>
            <td>${user.gender}</td>
            <td>${user.birthday}</td>
            <td><a href="${pageContext.request.contextPath}/user/userEdit.do?id=${user.id}">修改</a></td>
            <td><a href="${pageContext.request.contextPath}/user/userEdit2/${user.id}.do">修改2</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
