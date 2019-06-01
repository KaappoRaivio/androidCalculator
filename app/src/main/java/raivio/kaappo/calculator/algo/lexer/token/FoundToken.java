package raivio.kaappo.calculator.algo.lexer.token;


import java.util.List;
import java.util.Objects;

public class FoundToken {
    private Token tokenType;

    public FoundToken (Token tokenType) {
        this.tokenType = tokenType;
    }

    public Token getTokenType() {
        return tokenType;
    }

    @Override
    public boolean equals (Object o) {
        if (o == null) return false;

        if (getClass() != o.getClass()) return false;
        if (this == o) return true;

        FoundToken that = (FoundToken) o;
        return tokenType == that.tokenType;
    }

    public boolean is (Object o) {
        if (o == null) return false;
        else if (o.getClass() == Token.class) return tokenType == o;
        else return equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType);
    }

    @Override
    public String toString() {
        return tokenType.toString();
    }

    public boolean isIn (List<Token> tokenList) {
        boolean found = false;

        for (Token a : tokenList) {
            if (is(a)) {
                found = true;
                break;
            }
        }

        return found;
    }

}
