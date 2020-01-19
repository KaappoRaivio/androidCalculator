package raivio.kaappo.calculator.algo.lexer.token;

import raivio.kaappo.calculator.algo.lexer.UnknownTokenException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Token {
    ELLIPSIS ("\\.\\.\\.", "Ellipsis", -2),
    SYMBOL ("[a-z\\%π]", "Symbol", -1),

    SIN ("sin", "Sin", 0),
    COS ("cos", "Cos", 0),
    TAN ("tan", "Tan", 0),
    ARCSIN("asin", "Arcsin", 0),
    ARCCOS("acos", "Arccos", 0),
    ARCTAN("atan", "Arctan", 0),

    LOG10 ("log10", "Log10", 0),
    LOG2 ("log2", "Log2", 0),
    LN ("ln", "Ln", 0),

    ADD ("\\+", "Plus", 1),
    SUBTRACT ("\\-|–", "Minus", 1),
    MULTIPLY ("\\*|×", "Asterisk", 1),
    DIVIDE ("\\/|÷", "Slash", 1),
    LPAREN ("\\(", "Left parenthesis", 1),
    RPAREN ("\\)", "Right parenthesis", 1),
    END ("$", "End", 1),
    ABS ("\\|", "Pipe", 1),
    SQRT ("(\\√|sqrt)", "Square root", 1),
    ROOT ("root", "nth root", 1),
    EXCLAMATION ("\\!", "Factorial", 1),
    POWER ("(\\*\\*|\\^)", "Power", 2),
    INVPOW("(\\*\\*|\\^)( )?-", "Inverse Power", 3),
    NEG_SQRT ("(\\√|sqrt)( )?-", "negative square root", 3),
    NEG_ROOT ("root( )?-", "negative nth root", 3),
    NUMBER ("([0123456789])+([.,]([0123456789])+)?", "Number", 4);


//    FACTORIAL ("\\!", "Factorial", 0);


    private final Pattern regex;
    private final String repr;
    private final int precedence;

    Token(final String regex, final String repr, final int precedence) {
        this.regex = Pattern.compile("^" + regex);
        this.repr = repr;
        this.precedence = precedence;
    }

    public boolean matches (String another) {
        return regex.matcher(another).lookingAt();
    }

    public static Token getToken (String pattern) {
        try {
            return sortedValues()
                    .stream()
                    .filter(token -> token.matches(pattern))
                    .findAny()
                    .orElseThrow(() -> new UnknownTokenException("Unknown token " + pattern + "!"));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public Pattern getRemoverRegex () {
        return Pattern.compile(regex.pattern() + "(.*?)");
    }

    @Override
    public String toString() {
        return repr;
    }

    public static List<Token> sortedValues () {
        return Arrays.stream(Token.values()).sorted((item1, item2) -> item2.precedence - item1.precedence).collect(Collectors.toList());
    }

    public Pattern getRegex() {
        return regex;
    }
}
