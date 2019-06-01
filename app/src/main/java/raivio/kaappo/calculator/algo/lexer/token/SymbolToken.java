package raivio.kaappo.calculator.algo.lexer.token;

import lexer.token.FoundToken;
import lexer.token.Token;

import java.rmi.server.ExportException;

public class SymbolToken extends FoundToken {

    private String value;

    public String getValue() {
        return value;
    }


    @Override
    public boolean equals (Object o) {
        try {
            return super.equals(o) && value.equals(((SymbolToken) o).value);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "{" + value + "}";
    }

    public SymbolToken (String value) {
        super(Token.SYMBOL);

        this.value = value;
    }
}

