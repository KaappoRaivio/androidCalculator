package raivio.kaappo.calculator.algo.operator;

import raivio.kaappo.calculator.algo.lexer.token.Token;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;
import raivio.kaappo.calculator.algo.operator.genericoperator.OperatorType;
import raivio.kaappo.calculator.algo.operator.unaryoperator.UnaryOperator;
import raivio.kaappo.calculator.algo.operator.unaryoperator.UnaryOperatorType;

public class BoundingOperator extends UnaryOperator {
    private final Token rightToken;
    private final String representation;
    private final Token leftToken;

    public Token getRightToken () {
        return rightToken;
    }

    public Token getLeftToken () {
        return leftToken;
    }

    public BoundingOperator(Token leftToken, Token rightToken, java.util.function.UnaryOperator<Fractionable> function, String representation) {
        super(rightToken, function, UnaryOperatorType.BOUNDARY);
        this.leftToken = leftToken;
        this.rightToken = rightToken;
        this.representation = representation;
    }

    @Override
    public String toString () {
        return representation;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.BOUNDARY;
    }

    @Override
    public Token getTokenType() {
        return leftToken;
    }

    @Override
    public int getArity () {
        return 1;
    }

//    @Override
//    public Fractionable invoke (List<Fractionable> operands) {
//        if (operands.size() != getArity()) {
//            throw new RuntimeException("Bounding operator " + toString() + " cannot be applied to " + operands + "!");
//        }
//
//        return
//    }

    @Override
    public boolean isOperator() {
        return true;
    }

    @Override
    public boolean isFraction () {
        return false;
    }
}
