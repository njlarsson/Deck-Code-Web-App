package dk.itu.jesl.deck_code.processor;

import java.util.ArrayList;

public class Deck {
    static class OpException extends RuntimeException {
        OpException(String msg) { super(msg); }
    }
    
    private final String name;
    private final ArrayList<Integer> cards = new ArrayList<Integer>();

    public Deck(String name) { this.name = name; }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(name);
        b.append("(");
        int i = cards.size();
        while (i-- > 0) {
            b.append(cards.get(i).toString());
            if (i > 0) { b.append(", "); }
        }
        b.append(")");
        return b.toString();
    }

    String name() { return name; }

    public void parse(String s) {
        if (s.trim().length() != 0) {
            String[] a = s.split(",");
            for (int i = 0; i < a.length; i++) {
                cards.add(Integer.parseInt(a[i].trim()));
            }
        }
    }
  
    void moveTopTo(Deck other) {
        assertNotEmpty();
        other.cards.add(cards.remove(cards.size() - 1));
    }

    void moveAllTo(Deck other) {
        other.cards.addAll(cards);
        cards.clear();
    }

    int compareTop(Deck other) {
        assertNotEmpty();
        other.assertNotEmpty();
        return top() - other.top();
    }

    private int top() {
        return cards.get(cards.size() - 1);
    }

    private void assertNotEmpty() {
        if (cards.isEmpty()) {
            throw new OpException("Deck " + name + " is empty");
        }
    }

    boolean isEmpty() { return cards.isEmpty(); }
}
