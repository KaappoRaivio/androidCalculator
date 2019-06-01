package raivio.kaappo.calculator.algo.parser;

import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;

public class MyValueProvider<T extends Fractionable> implements ValueProvider<T> {


    @Override
    public T valueOf (String string) {
        return (T) T.valueOf(string);
    }
}
