package dk.itu.jesl.deck_code;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;
import java.util.regex.*;
import dk.itu.jesl.deck_code.processor.Deck;
import dk.itu.jesl.deck_code.processor.DeckInter;
import dk.itu.jesl.deck_code.processor.DeckInterException;

public class ProcessDeckCode {
    private static final Logger log = Logger.getLogger(ProcessDeckCode.class.getName());

    public static Iterable<String> inputDecks(String[] lines) {
        return new DeckInter().inputDecks(lines);
    }

    private static Pattern deckP = Pattern.compile("(\\w+):(.*)");
            
    public static String run(String[] lines, String inputs) {
        DeckInter inter = new DeckInter();
        for (String deckSpec : inputs.split("\n")) {
            if (deckSpec.length() == 0) { continue; }
            Matcher deckM = deckP.matcher(deckSpec);
            if (!deckM.matches()) {
                throw new IllegalArgumentException("Invalid deck specification: " + deckSpec);
            }
            String name = deckM.group(1);
            String value = deckM.group(2);
            Deck deck = new Deck(name);
            try {
                deck.parse(value);
            } catch (Exception e) {
                String problem = e.getMessage();
                if (problem == null) { problem = "syntax error"; }
                throw new IllegalDeckException(name, problem, value);
            }
            inter.inputDeck(deck);
        }
        StringWriter w = new StringWriter();
        inter.run(lines, w);
        return w.toString();
    }
    
    public static void errorText(String[] lines, String scriptName, DeckInterException e, HtmlWriter hw) throws IOException {
        hw.write("<p>"); hw.quoteContent(scriptName); hw.write(":" + e.lineNo());
        hw.write(": "); hw.quoteContent(e.getMessage()); hw.write("</p>");
        hw.write("\n<table border='0'>\n");
        int off = e.lineNo() - 1;
        for (int i = Math.max(0, off-2), s = Math.min(lines.length-1, off+2); i <= s; i++) {
            log.info("Debug data " + off + ", " + i + ", " + lines.length);
            String begin = "", end = "";
            if (i == off) { begin = "<strong>"; end = "</strong>"; }
            hw.write("<tr><td>"); hw.write(begin); hw.write("" + (i+1)); hw.write(end); hw.write("</td>");
            hw.write("<td>"); hw.write(begin); hw.quoteContent(lines[i]); hw.write(end); hw.write("</td></tr>");
        }
        hw.write("</table>");
    }

    public static String errorText(String[] lines, String scriptName, DeckInterException e) throws IOException {
        StringWriter sw = new StringWriter();
        HtmlWriter hw = new HtmlWriter(sw);
        errorText(lines, scriptName, e, hw);
        hw.flush();
        return sw.toString();
    }
}