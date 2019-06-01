package raivio.kaappo.calculator.algo.operator.genericoperator;

import lexer.token.Token;
import expression.Payload;
import math.fraction.fraction.Fractionable;

import java.util.List;

public interface Operator extends Payload {
    OperatorType getOperatorType();
    Token getTokenType();
    int getArity();
    Fractionable invoke(List<Fractionable> operands);
}
