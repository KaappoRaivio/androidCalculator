package raivio.kaappo.calculator.algo.math.utils;

import math.fraction.fraction.Fraction;
import math.fraction.fraction.Fractionable;

import java.math.BigInteger;

public class BigIntegerUtils {
    public static Fractionable power (Fractionable base, BigInteger exponent) {
        Fraction result = new Fraction(1, 1);

        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            result = result.multiply(base.fractionValue());
        }

        return result;
    }

//    public static Fractionable power (Fractionable base, Fractionable exponent) {
//        return power(base, exponent.fractionValue().getNumerator()).fractionValue().root()
//    }
}
