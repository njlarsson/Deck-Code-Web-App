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

<%
    String scriptName = request.getParameter("name");
    if (scriptName == null || scriptName.length() == 0) {
        throw new NullPointerException("No script name given");
    }
    String scriptNameU = URLEncoder.encode(scriptName, "UTF-8");
    String scriptNameC = HtmlWriter.quotedContent(scriptName);
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user == null) {
        response.sendRedirect("/");
    } else {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userKey = KeyFactory.createKey("User", user.getUserId());
        Query query = new Query("Script", userKey).setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, scriptName));
        Entity script = datastore.prepare(query).asSingleEntity();
        if (script == null) {
            script = new Entity("Script", userKey);
            script.setProperty("name", scriptName);
            script.setProperty("text", "");
            datastore.put(script);
            response.sendRedirect("/edit.jsp?name=" + scriptNameU);
            return;
        } else {
%>
<html>
  <head>
    <title>Create script <%= scriptNameC %></title>
  </head>
  <body>
    <script type="text/javascript">
        alert("<%= HtmlWriter.quotedString(scriptName) %> already exists");
        window.location.href = "/";
    </script>
  </body>
</html>
<%
        }
    }
%>
