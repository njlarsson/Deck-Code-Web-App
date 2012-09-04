package dk.itu.jesl.deck_code;

public class IllegalDeckException extends RuntimeException {
    IllegalDeckException(String deckName, String problem, String spec) {
        super(deckName + ": " + problem + " in " + spec);
    }
}