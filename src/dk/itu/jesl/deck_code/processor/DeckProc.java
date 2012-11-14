package dk.itu.jesl.deck_code.processor;

import java.util.*;

public abstract class DeckProc {
    public class Ex extends RuntimeException {
        Ex(String msg) { super(msg); }
        public int lineNo() { return DeckProc.this.lineNo(); }
    }

    abstract int lineNo();

    class SafeMap<T> {
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
}