<%--
  Created by IntelliJ IDEA.
  User: fijaz
  Date: Jun 27, 2008
  Time: 7:29:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Welcome</title></head>
<body>
<form action="#">
    Select Search Engine: <select id="lstSearchEngine" name="searchEngine"></select><br/>
    Search Text: <input name="searchText" type="text"/>
    <input id="btnSearch" type="button" value="Search"/>
</form>
<iframe id="frm1" frameborder="1" width="100%" height="600"></iframe>
</body>
</html>