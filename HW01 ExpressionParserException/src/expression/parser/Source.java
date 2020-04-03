package expression.parser;

public interface Source {
    boolean hasNext();
    char nextChar();
    int getPos();
}
