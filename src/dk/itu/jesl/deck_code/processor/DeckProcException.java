package dk.itu.jesl.deck_code.processor;

public abstract class DeckProcException extends RuntimeException {
    DeckProcException(String msg) { super(msg); }
    public abstract int lineNo();
}