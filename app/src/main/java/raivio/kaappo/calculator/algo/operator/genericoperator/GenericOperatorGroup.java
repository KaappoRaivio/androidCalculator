package raivio.kaappo.calculator.algo.operator.genericoperator;

import raivio.kaappo.calculator.algo.lexer.token.FoundToken;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class GenericOperatorGroup {
    private List<Operator> operators;

    @Override
    public String toString() {
        return groupType + ", " + operators.toString();
    }

    public OperatorType getGroupType () {
        return groupType;
    }

    private OperatorType groupType;

    public GenericOperatorGroup(OperatorType groupType, Operator... operators) {
        this(groupType, Arrays.stream(operators).collect(Collectors.toList()));
    }

    public GenericOperatorGroup (OperatorType groupType, List<Operator> operators) {
        this.groupType = groupType;
        this.operators = operators;
    }

    public boolean isOperator (FoundToken token) {
        return operators
                .stream()
                .anyMatch(operator -> token.is(operator.getTokenType()));
    }

    public Operator getOperator (FoundToken token) {
        try {
            return operators
                    .stream()
                    .filter(operator -> token.is(operator.getTokenType()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Token " + token + " is not defined as an operator!"));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
