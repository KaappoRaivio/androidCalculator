package raivio.kaappo.calculator.algo.math.fraction.approxfraction;

import raivio.kaappo.calculator.algo.expression.Expression;
import raivio.kaappo.calculator.algo.expression.SymbolTable;
import raivio.kaappo.calculator.algo.lexer.token.SymbolToken;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fraction;
import raivio.kaappo.calculator.algo.math.utils.TrigonometryUtils;
import raivio.kaappo.calculator.algo.misc.BigFunctions;
import raivio.kaappo.calculator.algo.misc.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ApproxFraction extends Fraction {
    private BigDecimal actualValue;

    public ApproxFraction (BigInteger numerator, BigInteger denominator) {
        super(numerator, denominator);
        actualValue = super.toDecimal();
    }

    public ApproxFraction (BigDecimal value) {
        super(1, 1);
        actualValue = value;
    }

    public ApproxFraction (String numerator, String denominator) {
        this(new BigInteger(numerator), new BigInteger(denominator));
    }

    private ApproxFraction (Fraction fraction) {
        this(fraction.toDecimal());
    }

    public static Fraction fromEndingDecimal (String s) {
        String original = s;


        int commaPlace = StringUtil.getCurrentCommaPlace(s);
        s = s.replaceAll("[.,]", "");

        ApproxFraction fraction = new ApproxFraction(s, StringUtil.moveComma("1.0", s.length() - commaPlace));
        fraction.originalRepresentation = original;
        return  fraction;
    }

    @Override
    public Fraction inverse () {
        return new ApproxFraction(BigDecimal.ONE.divide(actualValue, Expression.CONTEXT));
    }

    @Override
    public String toString () {
        return actualValue.toString();
    }

    @Override
    public Fraction add (Fraction other) {
        return new ApproxFraction(actualValue.add(other.toDecimal()));
    }

    @Override
    public Fraction factorial () {
        throw new RuntimeException("Fractions are not defined for approximates such as " + toString() +"!");
    }

    @Override
    public BigInteger getNumerator () {
        throw new RuntimeException("The numerator is not exact!");
    }

    @Override
    public BigInteger getDenominator () {
        throw new RuntimeException("The denominator is not exact!");
    }

    @Override
    protected ApproxFraction compact () {
        return this;
    }

    @Override
    public Fraction multiply (Fraction other) {
        return new ApproxFraction(actualValue.multiply(other.toDecimal()));
    }

    @Override
    public Fraction negate () {
        return new ApproxFraction(actualValue.negate());
    }

    @Override
    public Fraction power (Fraction exponent) {
        return new ApproxFraction(BigFunctions.exp(BigFunctions.ln(actualValue, Fraction.PRECISION).multiply(exponent.toDecimal(), Expression.CONTEXT), Fraction.PRECISION));
    }

    @Override
    public BigDecimal toDecimal () {
        return actualValue.stripTrailingZeros();
    }

    @Override
    public boolean isNegative () {
        return actualValue.signum() == -1;
    }

    @Override
    public boolean equals (Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof ApproxFraction) {
            return actualValue.compareTo(((ApproxFraction) o).toDecimal()) == 0;
        } else {
            return false;
        }
//        return super.equals(o);
//        throw new RuntimeException("Not implemented yet!");
    }

    public Fraction inversePower (Fraction mantissa) {
        return new ApproxFraction(BigFunctions.exp(BigFunctions.ln(mantissa.toDecimal(), Fraction.PRECISION).multiply(actualValue, Expression.CONTEXT), Fraction.PRECISION));
    }

    private static ApproxFraction getPi () {
        if (USE_DEGREES) {
            return new ApproxFraction(BigDecimal.valueOf(180));
        } else {
            return (ApproxFraction) SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue();
        }
    }

    private static ApproxFraction getMinusPi () {
        return new ApproxFraction(BigDecimal.valueOf(0));
    }

    private static ApproxFraction getHalfPi () {
        if (USE_DEGREES) {
            return new ApproxFraction(BigDecimal.valueOf(90));
        } else {
            return (ApproxFraction) SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue().divide(2);
        }
    }

    @Override
    public Fraction sin () {
        if (USE_DEGREES) {
            return TrigonometryUtils.sinMapDegrees.getOrDefault(this.normalize(), super.sin());
        } else {
            return TrigonometryUtils.sinMapRadians.getOrDefault(this.normalize(), super.sin());
        }
    }

    @Override
    public Fraction cos () {
        if (USE_DEGREES) {
            return TrigonometryUtils.cosMapDegrees.getOrDefault(this.normalize(), super.cos());
        } else {
            return TrigonometryUtils.cosMapRadians.getOrDefault(this.normalize(), super.cos());
        }
    }

    @Override
    public Fraction normalize () {
        boolean tooSmall = isNegative();
        if (USE_DEGREES) {
//            Fraction result = this;
//            if (tooSmall) {
//                while (result.toDecimal().signum() < 0) {
//                    result = result.add(new Fraction(360, 1));
//                }
//
//                return result;
//            } else {
//                while (result.toDecimal().compareTo(new Fraction(360, 1).toDecimal()) > 0) {
//                    result = result.subtract(new Fraction(360, 1));
//                }
//
//                return result;
//            }
            return this;
        } else {
            ApproxFraction result = this;
            if (tooSmall) {
                while (result.toDecimal().signum() < 0) {
                    result = (ApproxFraction) result.add(SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue().multiply(2));
                }

                return new ApproxFraction(result.toDecimal().stripTrailingZeros());
            } else {
                while (result.toDecimal().compareTo(SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue().multiply(2).toDecimal()) > 0) {
                    result = (ApproxFraction) result.subtract(SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue().multiply(2));
                }

                return new ApproxFraction(result.toDecimal().stripTrailingZeros());
            }
        }
    }
}
