package raivio.kaappo.calculator.algo.operator.unaryoperator;

import raivio.kaappo.calculator.algo.lexer.token.Token;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;
import raivio.kaappo.calculator.algo.operator.genericoperator.Operator;
import raivio.kaappo.calculator.algo.operator.genericoperator.OperatorType;
import raivio.kaappo.calculator.algo.expression.Payload;

import java.util.List;

public class UnaryOperator implements Operator, Payload {
    private java.util.function.UnaryOperator<Fractionable> function;
    private UnaryOperatorType unaryOperatorType;
    private Token token;



    public UnaryOperator(Token token, java.util.function.UnaryOperator<Fractionable> function, UnaryOperatorType unaryOperatorType) {
        this.token = token;
        this.function = function;
        this.unaryOperatorType = unaryOperatorType;
    }

    public Fractionable invoke (List<Fractionable> operands) {
        if (operands.size() != getArity()) {
            throw new RuntimeException("Unary operator " + toString() + " cannot be applied to " + operands + "!");
        } else {
            return function.apply(operands.get(0));
        }
    }

    public UnaryOperatorType getUnaryOperatorType() {
        return unaryOperatorType;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.UNARY;
    }

    @Override
    public String toString() {
//        return "UnaryOperator{" +
//                "unaryOperatorType=" + unaryOperatorType +
//                ", token=" + token +
//                '}';
        return token.toString();
    }

    @Override
    public Token getTokenType() {
        return token;
    }

    @Override
    public int getArity () {
        return 1;
    }

    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public boolean isFraction () {
        return false;
    }
}
