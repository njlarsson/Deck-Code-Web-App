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
<%@ page import="dk.itu.jesl.deck_code.IllegalDeckException" %>
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
            String text = (String) script.getProperty("text");
            String[] lines = text.split("[\\r\\n]+");
            Iterable<String> inDecks = null;
            String errorText = null;
            try {
                inDecks = ProcessDeckCode.inputDecks(lines);
            } catch (DeckInterException e) {
                errorText = ProcessDeckCode.errorText(lines, scriptName, e);
            }
            if (errorText != null) {
%>
    <%= errorText %>
<%
            } else {
                StringBuilder inSpec = new StringBuilder();
                for (String deckName : inDecks) {
                    String deckValue = request.getParameter("d_" + deckName);
                    inSpec.append(deckName + ":" + deckValue + "\n");
                }
                String output = "";
                try {
                    output = ProcessDeckCode.run(lines, inSpec.toString());
                } catch (IllegalDeckException e) {
                    errorText = "<p>Invalid input for deck " + HtmlWriter.quotedContent(e.getMessage()) + "</p>";
                } catch (DeckInterException e) {
                    errorText = ProcessDeckCode.errorText(lines, scriptName, e);
                }
                if (errorText != null) {
%>
    <%= errorText %>
<%
                } else {
%>
    <p><%= scriptNameC %> finished with the following output:</p>
    <pre><%= output %></pre>
<%
                }
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
