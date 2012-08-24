package dk.itu.jesl.deck_code.processor;

import java.util.HashMap;

public class DeckInter {
    private class DeckInterException extends RuntimeException {
        DeckInterException(String msg) { super(msg); }
        public String toString() { return lineNo + ": " + getMessage(); }
    }

    private class SafeMap<T> {
        private HashMap<String, T> map = new HashMap<String, T>();
        private final String what;

        SafeMap(String what) { this.what = what; }

        T get(String name) {
            T value = map.get(name);
            if (value == null) { throw new DeckInterException("No such " + what + ": " + name); }
            return value;
        }

        void create(String name, T value) {
            if (map.put(name, value) != null) {
                throw new DeckInterException("Already defined " + what + ": " + name);
            }
        }
    }

    // Base class in non-static context.
    private abstract class LocalLineProc implements DeckLineParser.LineProc { }

    private int lineNo = 0;
    private boolean stop = false;
    
    private SafeMap<Deck> decks = new SafeMap<Deck>("deck");
    private SafeMap<Integer> labels = new SafeMap<Integer>("label");
    
    public void run(String[] lines, boolean trace) {
        try {
            // Scan for labels.
            DeckLineParser.LineProc labelParser =
                new DeckLineParser.LineProc() {
                    public void stop() { }
                    public void label(String label) { labels.create(label, lineNo); }
                    public void makeDeck(String deckName) { }
                    public void moveTop(String left, String right) { }
                    public void moveAll(String left, String right) { }
                    public void jump(String label) { }
                    public void jumpEmpty(String deckName, String label) { }
                    public void jumpNotEmpty(String deckName, String label) { }
                    public void jumpLess(String left, String right, String label) { }
                    public void jumpGreater(String left, String right, String label) { }
                    public void jumpEqual(String left, String right, String label) { }
                    public void output(String deckName) { }
                    public void read(String deckName) { }
                    public void parseException(String message) { throw new DeckInterException(message); }
                };

            while (lineNo < lines.length) {
                String line = lines[lineNo++].trim();
                DeckLineParser.parseLine(line, labelParser);
            }
            lineNo = 0;             // reset for execution

            DeckLineParser.LineProc executor =
                new DeckLineParser.LineProc() {
                    public void stop() { stop = true; }
                    public void label(String label) { }
                    public void makeDeck(String deckName) { decks.create(deckName, new Deck(deckName)); }
                    public void moveTop(String left, String right) { decks.get(left).moveTopTo(decks.get(right)); }
                    public void moveAll(String left, String right) { decks.get(left).moveAllTo(decks.get(right)); }
                    public void jump(String label) { lineNo = labels.get(label); }
                    public void jumpEmpty(String deckName, String label) { 
                        if (decks.get(deckName).isEmpty()) { lineNo = labels.get(label); } 
                    }
                    public void jumpNotEmpty(String deckName, String label) {
                        if (!decks.get(deckName).isEmpty()) { lineNo = labels.get(label); }
                    }
                    public void jumpLess(String left, String right, String label) {
                        if (decks.get(left).compareTop(decks.get(right)) < 0) { lineNo = labels.get(label); }
                    }
                    public void jumpGreater(String left, String right, String label) {
                        if (decks.get(left).compareTop(decks.get(right)) > 0) { lineNo = labels.get(label); }
                    }
                    public void jumpEqual(String left, String right, String label) {
                        if (decks.get(left).compareTop(decks.get(right)) == 0) { lineNo = labels.get(label); }
                    }
                    public void output(String deckName) { throw new UnsupportedOperationException("Not implemented"); }
                    public void read(String deckName) { throw new UnsupportedOperationException("Not implemented"); }
                    public void parseException(String message) { throw new DeckInterException(message); }
                };

            while (!stop) {
                if (lineNo == lines.length) { throw new DeckInterException("Unexpected end of file"); }
                String line = lines[lineNo++].trim();
                if (trace) { throw new UnsupportedOperationException("Not implemented"); /* println("Executing " + lineNo + ": " + line) */ }
                DeckLineParser.parseLine(line, executor);
            }
        } catch (DeckInterException e) {
            throw new UnsupportedOperationException("Not implemented"); //println(e);
        } catch (Deck.OpException e) {
            throw new UnsupportedOperationException("Not implemented"); //println(lineNo + ": " + e.getMessage());
        }
    }
}