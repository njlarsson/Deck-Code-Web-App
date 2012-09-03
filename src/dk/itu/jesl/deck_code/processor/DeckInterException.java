package dk.itu.jesl.deck_code.processor;

public abstract class DeckInterException extends RuntimeException {
    DeckInterException(String msg) { super(msg); }
    public abstract int lineNo();
}