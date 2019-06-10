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
    <form action="${pageContext.request.contextPath}/user/register3.do" method="post">
        用户名:<input type="text" name="user.username"><br>
        密码:<input type="text" name="user.password"><br>
        年龄:<input type="text" name="user.age"><br>
        性别:<input type="text" name="user.gender"><br>
        生日:<input type="text" name="user.birthday"><br>
        爱好:<input type="checkbox" name="user.hobbyIds" value="1">打球
        <input type="checkbox" name="user.hobbyIds" value="2">打假
        <input type="checkbox" name="user.hobbyIds" value="3">打架<br>
        <input type="submit">
    </form>

    接收集合类型数据：<br>
    <form action="${pageContext.request.contextPath}/user/register3.do" method="post">
        用户名1:<input type="text" name="userList[0].username"><br>
        密码1:<input type="text" name="userList[0].password"><br>
        用户名2:<input type="text" name="userList[1].username"><br>
        密码2:<input type="text" name="userList[1].password"><br>

        <input type="submit">
    </form>

    接收Map类型数据：<br>
    <form action="${pageContext.request.contextPath}/user/register4.do" method="post">
        用户名:<input type="text" name="mapInfo['username']"><br>
        密码:<input type="text" name="mapInfo['password']"><br>
        年龄:<input type="text" name="mapInfo['age']"><br>
        性别:<input type="text" name="mapInfo['gender']"><br>

        <input type="submit">
    </form>
</body>
</html>
