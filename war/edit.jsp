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
<%@ page import="com.google.appengine.api.datastore.Text" %>
<%@ page import="dk.itu.jesl.deck_code.HtmlWriter" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String scriptName = request.getParameter("name");
    if (scriptName == null || scriptName.length() == 0) {
        throw new NullPointerException("No script name given");
    }
    String scriptNameC = HtmlWriter.quotedContent(scriptName);
    String scriptNameS = HtmlWriter.quotedString(scriptName);
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
        response.sendRedirect("/");
    } else {
        String nickC = HtmlWriter.quotedContent(user.getNickname());
%>
<html>
  <head>
    <title>Edit <%= scriptNameC %> (Deck Code)</title>
  </head>

  <body>
    <p>User: <%= nickC %> (<a href="<%= userService.createLogoutURL("/") %>">sign out</a>)</p>
<%
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userKey = KeyFactory.createKey("User", user.getUserId());
        Query query = new Query("Script", userKey).setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, scriptName));
        Entity script = datastore.prepare(query).asSingleEntity();
        if (script == null) {
%>
    <script type="text/javascript">
        alert("<%= scriptNameS %> not found");
        window.location.href = "/";
    </script>
<%
        } else {
            Object scriptTextEntity = script.getProperty("text");
            String scriptText = scriptTextEntity instanceof Text ?
                ((Text) scriptTextEntity).getValue() :
                scriptTextEntity.toString();
            String scriptTextC = HtmlWriter.quotedContent(scriptText);
%>
    <p>Script: <%= scriptNameC %></p>

    <form action="/submit.jsp" method="post">
      <input type="hidden" name="name" value="<%= scriptNameS %>" />
      <div><textarea name="text" rows="30" cols="80"><%= scriptTextC %></textarea></div>
      <div><input type="submit" value="Submit" /></div>
    </form>
<%
        }
%>
  <p><a href="/">Deck code home</a></p>
  </body>
</html>
<%
    }
%>
