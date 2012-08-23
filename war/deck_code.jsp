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
%>
    <p>User: ${fn:escapeXml(user.nickname)} (<a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>)</p>
    <table>
<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String userId = user.getUserId();
	pageContext.setAttribute("userid", userId + " length " + userId.length());
%>
    <p>User ID: ${userid}</p>
<%
	Key userKey = KeyFactory.createKey("User", userId);
	Query query = new Query("Script", userKey);
	for (Entity script : datastore.prepare(query).asIterable()) {
	    pageContext.setAttribute("script", URLEncoder.encode((String) script.getProperty("name"), "UTF-8"));
%>
<tr><td><a href="edit.jsp?script=${script}">${script}</td></tr>
<%
        }
%>
    </table>
    <script type="text/javascript">
    function newScript() {
	window.location.href = "/create.jsp?script=" + escape(prompt("Script name?"));
    }
    </script>

    <p><a href="javascript:newScript()">New script</a></p>
<%
    }
%>
  </body>
</html>