package raivio.kaappo.calculator.algo.operator.binaryoperator;

import raivio.kaappo.calculator.algo.lexer.token.Token;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;
import raivio.kaappo.calculator.algo.operator.genericoperator.Operator;
import raivio.kaappo.calculator.algo.operator.genericoperator.OperatorType;
import raivio.kaappo.calculator.algo.expression.Payload;

import java.util.List;

public class BinaryOperator implements Operator, Payload {
    private java.util.function.BinaryOperator<Fractionable> function;
    private EvaluatingOrder evaluatingOrder;
    private Token token;

    public BinaryOperator(Token token, java.util.function.BinaryOperator<Fractionable> function, EvaluatingOrder evaluatingOrder) {
        this.function = function;
        this.evaluatingOrder = evaluatingOrder;
        this.token = token;
    }

    public EvaluatingOrder getEvaluatingOrder() {
        return evaluatingOrder;
    }

    public Token getToken() {
        return token;
    }

    public Fractionable invoke (List<Fractionable> operands) {
        if (operands.size() != getArity()) {
            throw new RuntimeException("Binary operator " + toString() + " cannot be applied to " + operands + "!");
        }
        return function.apply(operands.get(0), operands.get(1));
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.BINARY;
    }

    @Override
    public String toString() {
//        return "BinaryOperator{" +
//                ", evaluatingOrder=" + evaluatingOrder +
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
        return 2;
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
