package raivio.kaappo.calculator.algo.math.utils;


import raivio.kaappo.calculator.algo.math.fraction.fraction.Fraction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class RootCalculus {

    private static final int SCALE = Fraction.PRECISION;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static BigDecimal nthRoot (BigInteger n, final BigDecimal base) {
        return nthRoot(n, base, BigDecimal.valueOf(.1).movePointLeft(SCALE));
//        return base;
    }

    private static BigDecimal nthRoot (final BigInteger n, final BigDecimal a, final BigDecimal precision) {
        if (a.compareTo(BigDecimal.ZERO) < 0 && n.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("Roots are only defined for positive numbers");
        }
        if (a.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        BigDecimal xPrev = a;
        BigDecimal x = a.divide(new BigDecimal(n), SCALE, ROUNDING_MODE);  // starting "guessed" value...
        while (x.subtract(xPrev).abs().compareTo(precision) > 0) {
            xPrev = x;
            x = new BigDecimal(n.subtract(BigInteger.ONE))
                    .multiply(x)
                    .add(a
                            .divide(x
                                    .pow(n
                                            .subtract(BigInteger.ONE)
                                            .intValue()), SCALE, ROUNDING_MODE))
                    .divide(new BigDecimal(n), SCALE, ROUNDING_MODE);
        }
        return x;
    }

    private RootCalculus() {
    }
}