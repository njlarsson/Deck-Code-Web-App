package dk.itu.jesl.deck_code.processor;

import java.util.*;
import java.io.IOException;
import java.io.Writer;

public class DeckInter {
    public class Ex extends DeckProcException {
        Ex(String msg) { super(msg); }
        public int lineNo() { return lineNo; }
    }

    private class SafeMap<T> {
        private HashMap<String, T> map = new HashMap<String, T>();
        private final String what;

        SafeMap(String what) { this.what = what; }

        T get(String name) {
            T value = map.get(name);
            if (value == null) { throw new Ex("No such " + what + ": " + name); }
            return value;
        }

        void create(String name, T value) {
            if (map.put(name, value) != null) {
                throw new Ex("Already defined " + what + ": " + name);
            }
        }
    }

    // Base class in non-static context.
    private abstract class LocalLineProc implements DeckLineParser.LineProc { }

    private int lineNo, outputs;
    private boolean stop = false;
    
    private SafeMap<Deck> decks = new SafeMap<Deck>("deck");
    private SafeMap<Integer> labels = new SafeMap<Integer>("label");

    public Iterable<String> inputDecks(String[] lines) {
        final ArrayList<String> inputs = new ArrayList<String>();
        lineNo = 0;
            
        // Scan for inputs.
        DeckLineParser.LineProc labelParser =
            new DeckLineParser.LineProc() {
                public void stop() { }
                public void label(String label) { }
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
                public void read(String deckName) { inputs.add(deckName); }
                public void parseException(String message) { throw new Ex(message); }
            };

        while (lineNo < lines.length) {
            String line = lines[lineNo++].trim();
            DeckLineParser.parseLine(line, labelParser);
        }
        return inputs;
    }

    public void inputDeck(Deck deck) {
        decks.create(deck.name(), deck);
    }

    public void run(String[] lines, final Writer w, final int maxOutputs, final long timeout) {
        try {
            lineNo = 0;
            
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
                    public void parseException(String message) { throw new Ex(message); }
                };

            while (lineNo < lines.length) {
                String line = lines[lineNo++].trim();
                DeckLineParser.parseLine(line, labelParser);
            }

            lineNo = 0;             // reset for execution
            outputs = 0;
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
                    public void output(String deckName) {
                        if (outputs++ >= maxOutputs) {
                            throw new Ex("Reached maximum number of outputs (" + maxOutputs + ")");
                        }
                        try {
                            w.append(decks.get(deckName).toString()).append("\n");
                        } catch (IOException e) {
                            throw new Ex("Output problem: " + e);
                        }
                    }
                    public void read(String deckName) { }
                    public void parseException(String message) { throw new Ex(message); }
                };

            final long deadline = System.currentTimeMillis() + timeout;
            while (!stop) {
                if (System.currentTimeMillis() >= deadline) { throw new DeckInterTimeoutException("Reached time limit (" + timeout/1000.0 + " seconds)"); }
                if (lineNo == lines.length) { throw new Ex("Unexpected end of file"); }
                String line = lines[lineNo++].trim();
                DeckLineParser.parseLine(line, executor);
            }
        } catch (Deck.OpException e) {
            throw new Ex(e.getMessage());
        }
    }
}