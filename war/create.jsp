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

<%
    String scriptName = request.getParameter("script");
    if (scriptName == null || scriptName.length() == 0) {
	throw new NullPointerException("No script name given");
    }
    String scriptUrl = URLEncoder.encode(scriptName, "UTF-8");
    pageContext.setAttribute("script", scriptUrl);
%>

<html>
  <head>
    <title>Create script ${script}</title>
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
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Key userKey = KeyFactory.createKey("User", user.getUserId());
	Query query = new Query("Script", userKey).
	    setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, scriptName));
        Entity script = datastore.prepare(query).asSingleEntity();
        if (script != null) {
%>
    <script type="text/javascript">
        alert("${script} already exists");
        window.location.href = "/";
    </script>
<%
        } else {
	    script = new Entity("Script", userKey);
	    script.setProperty("name", scriptName);
	    script.setProperty("text", "");
	    datastore.put(script);
            response.sendRedirect("/edit.jsp?script=" + scriptUrl);
	}
    }
%>
    <p>Go <a href="/edit.jsp?script=${script}">edit ${script}</a>.</p>
  </body>
</html>