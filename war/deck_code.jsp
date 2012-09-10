<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="dk.itu.jesl.deck_code.HtmlWriter" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
  <head>
    <title>Deck Code</title>
  </head>

  <body>

<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
%>
    <p>Welcome to Deck Code! Please
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">sign in</a>.</p>
<%
    } else {
        String nickC = HtmlWriter.quotedContent(user.getNickname());
%>
    <p>User: <%= nickC %> (<a href="<%= userService.createLogoutURL("/") %>">sign out</a>)</p>
    <table>
<%
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String userId = user.getUserId();
        Key userKey = KeyFactory.createKey("User", userId);
        Query query = new Query("Script", userKey);
        for (Entity script : datastore.prepare(query).asIterable()) {
            String scriptName = (String) script.getProperty("name");
            String scriptNameU = URLEncoder.encode(scriptName, "UTF-8");
            String scriptNameS = HtmlWriter.quotedString(scriptName);
            String scriptNameC = HtmlWriter.quotedContent(scriptName);
%>
    <tr><td><a href="edit.jsp?name=<%= scriptNameU %>"><%= scriptNameC %></a></td><td> [<a href="javascript:deleteScript('<%= scriptNameS %>')">Delete</a>]</td></tr>
<%
        }
%>
    </table>
    <script type="text/javascript">
    function newScript() {
        window.location.href = "/create.jsp?name=" + encodeURI(prompt("Script name?"));
    }
    function deleteScript(name) {
        if (confirm("Are you sure you want to delete " + name + "?")) {
            window.location.href = "/delete.jsp?name=" + name;
        }
    }
    </script>

    <p><a href="javascript:newScript()">New script</a></p>
<%
    }
%>
  </body>
</html>