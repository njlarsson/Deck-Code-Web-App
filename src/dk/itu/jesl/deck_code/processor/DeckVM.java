package dk.itu.jesl.deck_code.processor;

public class DeckVM {
    public static final int MOVETOP = 32 + 1;
    public static final int MOVEALL = 32 + 2;
    public static final int JUMP_EMPTY = 64 + 16 + 3;
    public static final int JUMP_NOT_EMPTY = 64 + 16 + 4;
    public static final int JUMP_LESS = 64 + 32 + 5;
    public static final int JUMP_EQUAL = 64 + 32 + 6;
    public static final int JUMP = 64 + 7;
    public static final int OUTPUT = 16 + 8;
    public static final int READ = 16 + 9;
    public static final int READ_FILE = 32 + 16 + 10;
    public static final int STOP = 11;
}