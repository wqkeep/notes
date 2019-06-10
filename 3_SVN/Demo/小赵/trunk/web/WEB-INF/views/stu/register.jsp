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
    <title>学生注册</title>
    <script src="${pageContext.request.contextPath}/js/jquery-1.8.3.js"></script>
    <script>
        function register() {

/*            var username = document.getElementById("username").value;
            var gender = document.getElementById("gender").value;*/
           var username = $('#username').val();
           var gender = $('#gender').val();

           //表单提交
            var url = '${pageContext.request.contextPath}/stu/save.do';

            var jsonObj = {username:username,gender:gender};
            var parameter = JSON.stringify(jsonObj);

            console.log(jsonObj);
            console.log(parameter);

            $.ajax({
                type:'post',
                url:url,
                contentType: 'application/json;charset=utf-8',
                data:parameter,
                success:function (resqData) {
                    console.log(resqData);
                }
            });

        }
    </script>
</head>
<body>
    <form action="${pageContext.request.contextPath}/stu/save1.do" method="post">
        用户名:<input type="text" id="username" name="username"><br>
        性别:<input type="text" id="gender" name="gender"><br>

        <input type="button" value="提交json数据" onclick="register()">
        <input type="submit">
    </form>
</body>
</html>
