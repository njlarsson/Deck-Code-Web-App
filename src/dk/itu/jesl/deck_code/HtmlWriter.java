package dk.itu.jesl.deck_code;

import java.io.*;

public class HtmlWriter extends Writer {
    private final Writer w;

    public HtmlWriter(Writer w) {
        this.w = w;
    }

    public void write(int c) throws IOException { w.write(c); } 
    public void write(char cbuf[], int off, int len) throws IOException { w.write(cbuf, off, len); }
    public void write(String str, int off, int len) throws IOException { w.write(str, off, len); }
    public void flush() throws IOException { w.flush(); }

    public void close() throws IOException {
        w.close();
    }
    
    public void quoteContent(int c) throws IOException {
        if      (c == '&') w.write("&amp;");
        else if (c == '<') w.write("&lt;");
        else               w.write(c);
    }

    public void quoteContent(CharSequence s) throws IOException {
        for (int i = 0, l = s.length(); i < l; i++) {
            quoteContent(s.charAt(i));
        }
    }

    public void quoteString(int c) throws IOException {
        if (c == '\'') w.write("&#39;");
        else if (c == '"') w.write("&quot;");
        else               w.write(c);
    }

    public void quoteString(CharSequence s) throws IOException {
        for (int i = 0, l = s.length(); i < l; i++) {
            quoteString(s.charAt(i));
        }
    }

    public static String quotedContent(CharSequence s) throws IOException {
        StringWriter sw = new StringWriter();
        HtmlWriter hw = new HtmlWriter(sw);
        hw.quoteContent(s);
        hw.flush();
        return sw.toString();
    }

    public static String quotedString(CharSequence s) throws IOException {
        StringWriter sw = new StringWriter();
        HtmlWriter hw = new HtmlWriter(sw);
        hw.quoteString(s);
        hw.flush();
        return sw.toString();
    }
}
