<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/3/19 0019
  Time: 13:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/user/update.do" method="post">
        用户名:<input type="text" name="username" value="${user.username}"><br>
        年龄:<input type="text" name="age" value="${user.age}"><br>
        性别:<input type="text" name="gender" value="${user.gender}"><br>
        生日:<input type="text" name="birthday" value="${user.birthday}"><br>

        <input type="submit" value="保存">
    </form>
</body>
</html>
