package dk.itu.jesl.deck_code.processor;

import java.io.*;
import java.util.ArrayList;

public class DeckComp extends DeckProc {
    private int lineNo, instrNo;
    
    private SafeMap<Integer> decks = new SafeMap<Integer>("deck");
    private SafeMap<Integer> labels = new SafeMap<Integer>("label");

    int lineNo() { return lineNo; }

    public void run(String[] lines, Writer primitiveWriter) {
        final PrintWriter w = new PrintWriter(primitiveWriter);
        final ArrayList<String> deckList = new ArrayList<String>();
        final ArrayList<Integer> inputs = new ArrayList<Integer>();
        lineNo = 0;
        instrNo = 0;
            
        // Scan for labels and decks.
        DeckLineParser.LineProc labelParser =
            new DeckLineParser.LineProc() {
                public void stop() { instrNo += 1; }
                public void label(String label) { labels.create(label, instrNo); }
                public void makeDeck(String deckName) {
                    deckList.add(deckName);
                    decks.create(deckName, deckList.size());
                }
                public void moveTop(String left, String right) { instrNo += 3; }
                public void moveAll(String left, String right) { instrNo += 3; }
                public void jump(String label) { instrNo += 2; }
                public void jumpEmpty(String deckName, String label) { instrNo += 3; }
                public void jumpNotEmpty(String deckName, String label) { instrNo +=3; }
                public void jumpLess(String left, String right, String label) { instrNo += 4; }
                public void jumpGreater(String left, String right, String label) { instrNo += 4; }
                public void jumpEqual(String left, String right, String label) { instrNo += 4; }
                public void output(String deckName) { instrNo += 2; }
                public void read(String deckName) {
                    deckList.add(deckName);
                    inputs.add(deckList.size());
                    decks.create(deckName, deckList.size());
                }
                public void parseException(String message) { throw new Ex(message); }
            };

        while (lineNo < lines.length) {
            String line = lines[lineNo++].trim();
            DeckLineParser.parseLine(line, labelParser);
        }
        w.print("Decks:      ");
        w.println(deckList.size());
        for (int i = 0; i < deckList.size(); i++) {
            String name = deckList.get(i);
            w.print(i+1);
            w.print(":");
            w.print(pad(10, i+1));
            w.print(name.length());
            w.print(pad(3, name.length()));
            for (int j = 0; j < name.length(); j++) {
                int c = name.charAt(j);
                w.print(" ");
                w.print(c);
            }
            w.println();
        }
        w.println();
        w.print("Inputs:     ");
        w.print(inputs.size());
        w.print(pad(3, inputs.size()));
        for (int deckNo : inputs) {
            w.print(" ");
            w.print(deckNo);
        }
        w.println();
        w.println();
        w.print("Program:    ");
        w.println(instrNo);

        DeckLineParser.LineProc codeGenerator =
            new DeckLineParser.LineProc() {
                public void stop() { outCode(w, DeckVM.STOP); }
                public void label(String label) { }
                public void makeDeck(String deckName) { }
                public void moveTop(String left, String right) { outCode(w, DeckVM.MOVETOP, decks.get(left), decks.get(right)); }
                public void moveAll(String left, String right) { outCode(w, DeckVM.MOVEALL, decks.get(left), decks.get(right)); }
                public void jump(String label) { outCode(w, DeckVM.JUMP, labels.get(label)); }
                public void jumpEmpty(String deckName, String label) { outCode(w, DeckVM.JUMP_EMPTY, decks.get(deckName), labels.get(label)); } 
                public void jumpNotEmpty(String deckName, String label) { outCode(w, DeckVM.JUMP_NOT_EMPTY, decks.get(deckName), labels.get(label)); } 
                public void jumpLess(String left, String right, String label) { outCode(w, DeckVM.JUMP_LESS, decks.get(left), decks.get(right), labels.get(label)); }
                public void jumpGreater(String left, String right, String label) { outCode(w, DeckVM.JUMP_LESS, decks.get(right), decks.get(left), labels.get(label)); }
                public void jumpEqual(String left, String right, String label) { outCode(w, DeckVM.JUMP_EQUAL, decks.get(left), decks.get(right), labels.get(label)); }
                public void output(String deckName) { outCode(w, DeckVM.OUTPUT, decks.get(deckName)); }
                public void read(String deckName) { }
                public void parseException(String message) { throw new Ex(message); }
            };
        lineNo = 0;             // reset for code generation
        instrNo = 0;
        while (lineNo < lines.length) {
            String line = lines[lineNo++].trim();
            DeckLineParser.parseLine(line, codeGenerator);
        }
        w.flush();
    }

    private void outCode(PrintWriter w, int... code) {
        w.print(instrNo);
        w.print(":");
        w.print(pad(9, instrNo));
        for (int i : code) {
            w.print(" ");
            w.print(i);
            instrNo++;
        }
        w.println();
    }

    private static String pad(int f, int i) {
        StringBuilder b = new StringBuilder();
        while (f > 1 && i > 9) {
            i /= 10;
            f--;
        }
        while (f > 0) {
            b.append(" ");
            f--;
        }
        return b.toString();
    }
}