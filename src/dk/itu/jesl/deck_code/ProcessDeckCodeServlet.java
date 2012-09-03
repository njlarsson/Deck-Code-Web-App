package dk.itu.jesl.deck_code;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import dk.itu.jesl.deck_code.processor.DeckInter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ProcessDeckCodeServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ProcessDeckCodeServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String name = req.getParameter("name");
        String text = req.getParameter("text");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key userKey = KeyFactory.createKey("User", user.getUserId());
        Query query = new Query("Script", userKey).
            setFilter(new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, name));
        Entity script = datastore.prepare(query).asSingleEntity();
        script.setProperty("text", text);
        datastore.put(script);
        log.info("User " + user + " posted script " + name);

        // HtmlWriter w = new HtmlWriter(resp.getWriter());  
        // resp.setContentType("text/html;charset=UTF-8");
        // w.write("<html>\n<head><title>" + name +"</title></head>\n<body>\n");
        // w.println("<h1>Processing " + name + "</h1>\n");

        // String lines = script.split("[\\r\\n]+");
        // DeckInter inter = new DeckInter();

        // try {
        //     Iterable<String> decks = inter.inputDecks(lines);
        // } catch (DeckInter.DeckInterException e) {
        //     error(w, lines, name, e);
        //     return;
        // }
        // w.write("<table>\n<tr><th>Input deck</th><th>Value</th></tr>\n");
        // //        for (String deck : decks) {
            
            


        // String deck = "in1";
        // String deckContent = "in1-before";
        // w.println("<p>Deck " + deck + "<div id='" + deck + "'>" + deckContent + "</div></p>");
        // w.println("<script type=\"text/javascript\">");  
        // w.println("document.getElementById('" + deck + "').innerHTML = prompt('Content of " + deck + "');");
        // w.println("</script>");
    }

            // w.write("<p><a href='/edit.jsp?script="); w.quoteString(name); w.write("'>Back to edit</a></p>");
}
