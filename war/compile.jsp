<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.io.StringWriter" %>
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
<%@ page import="dk.itu.jesl.deck_code.ProcessDeckCode" %>
<%@ page import="dk.itu.jesl.deck_code.processor.DeckProc" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String scriptName = request.getParameter("name");
    if (scriptName == null || scriptName.length() == 0) {
        throw new NullPointerException("No script name given");
    }
    String scriptNameU = URLEncoder.encode(scriptName, "UTF-8");
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
    <title>Compile <%= scriptNameC %> (Deck Code)</title>
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
            String[] lines = scriptText.split("[\\r\\n]+");
            StringWriter output = new StringWriter();
            String errorText = null;
            try {
                ProcessDeckCode.compile(lines, output);
            } catch (DeckProc.Ex e) {
                errorText = ProcessDeckCode.errorText(lines, e);
            }
            output.flush();
            if (errorText != null) {
%>
    <h1>Error in compiling <%= scriptNameC %></h1>
    <%= errorText %>
    <h2>Output generated:</h2>
    <hr />
    <pre><%= output %></pre>
    <hr />
<%
            } else {
%>
    <h1><%= scriptNameC %> finished with the following output:</h1>
    <hr />
    <pre><%= output %></pre>
    <hr />
<%
            }
        }
%>
  <p><a href="/edit.jsp?name=<%= scriptNameU %>">Edit <%= scriptNameC %></a></p>
  <p><a href="/">Deck code home</a></p>
  </body>
</html>
<%
    }
%>
