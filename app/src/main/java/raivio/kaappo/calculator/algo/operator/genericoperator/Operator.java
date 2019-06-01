package raivio.kaappo.calculator.algo.operator.genericoperator;

import raivio.kaappo.calculator.algo.lexer.token.Token;
import raivio.kaappo.calculator.algo.expression.Payload;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;

import java.util.List;

public interface Operator extends Payload {
    OperatorType getOperatorType();
    Token getTokenType();
    int getArity();
    Fractionable invoke(List<Fractionable> operands);
}
