package raivio.kaappo.calculator.algo.operator.binaryoperator;

import lexer.token.FoundToken;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryOperatorGroup {
    private List<BinaryOperator> operators;

    public BinaryOperatorGroup (BinaryOperator... operators) {
        this(Arrays.stream(operators).collect(Collectors.toList()));
    }

    public BinaryOperatorGroup(List<BinaryOperator> operators) {
        this.operators = operators;
    }

    public boolean isOperator(FoundToken token) {
        return operators
                .stream()
                .anyMatch(operator -> token.is(operator.getTokenType()));
    }

    public BinaryOperator getOperator(FoundToken token) {
        return operators
                .stream()
                .filter(operator -> token.is(operator.getTokenType()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Token " + token + " is not defined as a suffix operator!"));
    }
}
