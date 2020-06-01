package expression.parser;

public class StringSource implements Source {
    String string;
    int pos;

    public StringSource(String string) {
        this.string = string;
        pos = 0;
    }

    public boolean hasNext() {
        return pos < string.length();
    }

    public char nextChar() {
        return string.charAt(pos++);
    }

    public int getPos() {
        return pos;
    }
}
