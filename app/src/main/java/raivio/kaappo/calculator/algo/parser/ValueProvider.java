package raivio.kaappo.calculator.algo.parser;

;

import math.fraction.fraction.Fractionable;


public interface ValueProvider<T extends Fractionable> {
    T valueOf(String string);
}
