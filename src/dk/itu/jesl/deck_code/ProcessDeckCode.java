package dk.itu.jesl.deck_code;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;

import dk.itu.jesl.deck_code.processor.DeckInter;
import dk.itu.jesl.deck_code.processor.DeckInterException;

public class ProcessDeckCode {
    private static final Logger log = Logger.getLogger(ProcessDeckCode.class.getName());

    public static Iterable<String> inputDecks(String[] lines) {
        return new DeckInter().inputDecks(lines);
    }
            
    public static void run(String[] lines) {
        new DeckInter().run(lines);
    }
    
    public static void errorText(String[] lines, String scriptName, DeckInterException e, HtmlWriter hw) throws IOException {
        hw.write("<p>"); hw.quoteContent(scriptName); hw.write(":" + e.lineNo());
        hw.write(": "); hw.quoteContent(e.getMessage()); hw.write("</p>");
        hw.write("\n<table border='0'>\n");
        int off = e.lineNo() - 1;
        for (int i = Math.max(0, off-2), s = Math.min(lines.length-1, off+2); i < s; i++) {
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