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
<%@ page import="dk.itu.jesl.deck_code.ProcessDeckCode" %>
<%@ page import="dk.itu.jesl.deck_code.processor.DeckInterException" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    String scriptName = request.getParameter("name");
    if (scriptName == null || scriptName.length() == 0) {
        throw new NullPointerException("No script name given");
    }
    String scriptNameU = URLEncoder.encode(scriptName, "UTF-8");
    String scriptNameC = HtmlWriter.quotedContent(scriptName);
    String scriptNameS = HtmlWriter.quotedString(scriptName);

    String text = request.getParameter("text");

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
        response.sendRedirect("/");
    } else {
        String nickC = HtmlWriter.quotedContent(user.getNickname());
%>
<html>
  <head>
    <title>Prepare <%= scriptNameC %> (Deck Code)</title>
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
        alert("<%=  scriptNameS %> not found");
        window.location.href = "/";
    </script>
<%
        } else {
            script.setProperty("text", new Text(text));
            datastore.put(script);
            String[] lines = text.split("[\\r\\n]+");
            Iterable<String> inDecks = null;
            String errorText = null;
            try {
                inDecks = ProcessDeckCode.inputDecks(lines);
            } catch (DeckInterException e) {
                errorText = ProcessDeckCode.errorText(lines, e);
            }
            if (errorText != null) {
%>
    <h1>Error in parsing <%= scriptNameC %></h1>
    <%= errorText %>
<%
            } else {
%>
    <form action="/run.jsp" method="post">
      <input type="hidden" name="name" value="<%= scriptNameS %>" />
<%
                for (String deck : inDecks) {
%>
      <div><%= deck %>: <input type="text" size="120" name="d_<%= deck %>" /></div>
<%
                }
%>
      <div><input type="submit" value="Run" /></div>
    </form>
<%
            }
        }
%>
  <p><a href="/edit.jsp?name=<%= scriptNameU %>">Edit <%= scriptNameC %></a>
  <p><a href="/">Deck code home</a>
  </body>
</html>
<%
    }
%>
