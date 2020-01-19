package raivio.kaappo.calculator.algo.math.fraction.fraction;


import raivio.kaappo.calculator.algo.expression.Expression;
import raivio.kaappo.calculator.algo.expression.SymbolTable;
import raivio.kaappo.calculator.algo.lexer.token.SymbolToken;
import raivio.kaappo.calculator.algo.math.error.MathError;
import raivio.kaappo.calculator.algo.math.fraction.approxfraction.ApproxFraction;
import raivio.kaappo.calculator.algo.math.utils.RootCalculus;
import raivio.kaappo.calculator.algo.math.utils.TrigonometryUtils;
import raivio.kaappo.calculator.algo.misc.BigFunctions;
import raivio.kaappo.calculator.algo.misc.Pair;
import raivio.kaappo.calculator.algo.misc.StringUtil;


import ch.obermuhlner.math.big.BigDecimalMath;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.stream.Stream;

public class Fraction implements Fractionable {
    public static boolean USE_DEGREES = true;

    public static final int PRECISION = 34;


    protected String originalRepresentation;
    private BigInteger numerator;
    private BigInteger denominator;

    public Fraction (BigInteger numerator, BigInteger denominator) {
        this(numerator, denominator, true);
    }

    private Fraction (BigInteger numerator, BigInteger denominator, boolean compact) {
        if (denominator.equals(BigInteger.ZERO)) {
//            throw new ArithmeticException("Denominator cannot be zero!");
            throw new MathError("Cannot divide by zero!", "Not a number");
        }


        this.numerator = numerator;
        this.denominator = denominator;

        if (compact) {
            compactInPlace();
        }
    }

    private void compactInPlace () {
        BigInteger gcd = gcd(this.numerator, this.denominator);
        this.numerator = this.numerator.divide(gcd);
        this.denominator = this.denominator.divide(gcd);
    }

    public BigInteger safeIntValue () {
        if (isInteger()) {
            return numerator;
        } else {
            throw new RuntimeException("safeIntValue() isn't safe for " + this);
        }
    }

    public Fraction (String numerator, String denominator) {
        this(new BigInteger(numerator), new BigInteger(denominator));
    }

    public Fraction (int numerator, int denominator) {
        this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    private boolean compareDecimal (Fraction other) {
        String a = toDecimal().toString();
        String b = other.toDecimal().toString();

        for (int i = 0; i < b.length(); i++) {
            try {
                if (a.charAt(i) != b.charAt(i)) {
                    return  false;
                }
            } catch (StringIndexOutOfBoundsException e) {
                break;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toDecimal());
    }

    @Override
    public boolean equals (Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (o instanceof Integer) {
            return numerator.equals(new BigInteger(String.valueOf((int) o))) && denominator.equals(BigInteger.ONE);
        } else if (o instanceof Fraction) {
            Fraction other = (Fraction) o;

            Fraction a = expand(other.denominator);
            Fraction b = other.expand(denominator);

            return a.numerator.equals(b.numerator) && a.denominator.equals(b.denominator);
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        if (isInteger()) {
            return numerator.toString();
        } else {
            return "(" + numerator + " / " + denominator + ")";
        }
    }

    public Fraction add (int other) {
        return add(new Fraction(BigInteger.valueOf(other), BigInteger.ONE));
    }

    public Fraction add (Fraction other) {
        if (other instanceof ApproxFraction) {
            return other.add(this);
        }
        Fraction a = this.expand(other.denominator);
        Fraction b = other.expand(this.denominator);


        return new Fraction(a.numerator.add(b.numerator), a.denominator);
    }

    Fraction expand (BigInteger amount) {
        return new Fraction(numerator.multiply(amount), denominator.multiply(amount), false);
    }

    public Fraction subtract (int other) {
        return add(-other);
    }

    protected Fraction compact () {
        BigInteger gcd = gcd(numerator, denominator);
        return reduce(gcd);
    }

    public Fraction subtract (Fraction other) {
        return add(other.negate());
    }

    private Fraction reduce (BigInteger amount) {
        return new Fraction(numerator.divide(amount), denominator.divide(amount));
    }

    public Fraction negate () {
        return new Fraction(numerator.negate(), denominator);
    }

    public Fraction power (Fraction exponent) {
        if (exponent instanceof ApproxFraction) {
            return ((ApproxFraction) exponent).inversePower(this);
        } else if (this.equals(0)) {
            if (exponent.equals(0)) {
                throw new MathError("0 to the power of 0 is not defined!", "Undefined");
            } else {
                return this;
            }
        } else if (this.equals(1)) {
            return this;
        } else {
            return power(exponent.numerator).root(exponent.denominator);
        }

    }

    public Fraction root (BigInteger n) {
        if (isNegative() && n.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            throw new MathError("Even roots are only defined for positive numbers!", "i");
        }

        String a = RootCalculus.nthRoot(n, toDecimal()).toString();

        if (a.contains(".")) {
            return fromDecimal(a.substring(0, a.length() - 1), true);
        } else {
            return fromDecimal(a, false);
        }
    }

    public Fraction multiply (int other) {
        return multiply(new Fraction(other, 1));
    }

    public Fraction multiply (Fraction other) {
        if (other instanceof ApproxFraction) {
            return other.multiply(this);
        }
        return new Fraction(numerator.multiply(other.numerator), denominator.multiply(other.denominator)).compact();
    }

    public Fraction divide (Fraction other) {
//        return new Fraction(numerator.multiply(other.denominator), denominator.multiply(other.numerator)).compact();
        return multiply(other.inverse());
    }

    public Fraction divide (int other) {
        return divide(new Fraction(other, 1));
    }

    private Fraction power (BigInteger exponent) {
        if(exponent.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
            throw new RuntimeException("Overflow!");
        }

        int integerExponent = exponent.intValue();

        if (integerExponent < 0) {
            return new Fraction(numerator.pow(-integerExponent), denominator.pow(-integerExponent)).inverse();
        } else {
            return new Fraction(numerator.pow(integerExponent), denominator.pow(integerExponent));
        }
    }

    public Fraction sin () {
        if (USE_DEGREES) {
            return TrigonometryUtils.sinMapDegrees.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.sin(toRadians().toDecimal(), Expression.CONTEXT)).toDegrees());
        } else {
            return TrigonometryUtils.sinMapRadians.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.sin(toDecimal(), Expression.CONTEXT)));
        }
    }

    public Fraction cos () {
        if (USE_DEGREES) {
            return TrigonometryUtils.cosMapDegrees.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.cos(toRadians().toDecimal(), Expression.CONTEXT)).toDegrees());
        } else {
            return TrigonometryUtils.cosMapRadians.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.cos(toDecimal(), Expression.CONTEXT)));
        }
    }

    public Fraction tan () {
        return sin().divide(cos());
    }

    public Fraction asin () {
        if (USE_DEGREES) {
            return TrigonometryUtils.asinMapDegrees.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.asin(toRadians().toDecimal(), Expression.CONTEXT)).toDegrees());
        } else {
            return TrigonometryUtils.asinMapRadians.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.asin(toDecimal(), Expression.CONTEXT)));
        }
    }

    public Fraction acos () {
        if (USE_DEGREES) {
            return TrigonometryUtils.acosMapDegrees.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.asin(toRadians().toDecimal(), Expression.CONTEXT)).toDegrees());
        } else {
            return TrigonometryUtils.acosMapRadians.getOrDefault(this.normalize(), new ApproxFraction(BigDecimalMath.asin(toDecimal(), Expression.CONTEXT)));
        }
    }

    public Fraction atan () {
        if (USE_DEGREES) {
            return new ApproxFraction(BigDecimalMath.atan(toRadians().toDecimal(), Expression.CONTEXT)).toDegrees();
        } else {
            return new ApproxFraction(BigDecimalMath.atan(toDecimal(), Expression.CONTEXT));
        }
    }


    public Fraction log10 () {
        return new ApproxFraction(BigDecimalMath.log10(toDecimal(), Expression.CONTEXT));
    }

    public Fraction log2 () {
        return new ApproxFraction(BigDecimalMath.log2(toDecimal(), Expression.CONTEXT));
    }

    public Fraction ln () {
        return new ApproxFraction(BigFunctions.ln(toDecimal(), Expression.CONTEXT.getPrecision()));
    }

    public Fraction toDegrees () {
        return divide(SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue().multiply(2)).multiply(360);
    }

    private Fraction toRadians () {
        return divide(360).fractionValue().multiply(SymbolTable.defaultTable.getValue(new SymbolToken("p")).fractionValue().multiply(2));
    }

    public BigDecimal toDecimal () {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), Expression.CONTEXT);
    }

    public static Fraction fromDecimal (String representation, boolean endless) {
        if (endless) {
            return fromEndlessDecimal(representation);
        } else {
            return fromEndingDecimal(representation);
        }
    }

    private static Fraction fromEndlessDecimal (String representation) {
        if (!representation.contains(".") && !representation.contains(",")) {
//            throw new NumberFormatException("No comma or decimal point found in number " + representation + "!");
            return fromEndingDecimal(representation);
        }

        boolean repeating = !StringUtil.findPattern(representation).getV().equals("");

        if (repeating) {
            return fromRepeatingDecimal(representation);
        } else {
            String sliced;
            try {
                sliced = representation.substring(0, PRECISION);
            } catch (IndexOutOfBoundsException e) {
                sliced = representation;
            }

            // non-repeating, endless
            return ApproxFraction.fromEndingDecimal(sliced);
        }
    }

    private static Fraction fromEndingDecimal(String x) {
        String original = x;


        int commaPlace = StringUtil.getCurrentCommaPlace(x);
        x = x.replaceAll("[.,]", "");
        Fraction fraction = new Fraction(x, StringUtil.moveComma("1.0", x.length() - commaPlace));
        fraction.originalRepresentation = original;
        return fraction;
    }

    private static Fraction fromRepeatingDecimal(String x) {
        String original = x;
        x = x.replaceAll("^0+", "");

        if (x.startsWith(".") || x.startsWith(",")) {
            x = "0" + x;
        }

        Pair<String, String> pattern = StringUtil.findPattern(x);

        String base = pattern.getK();
        String cycle = pattern.getV();


        int commaPlace = StringUtil.getCurrentCommaPlace(x);
        int exponent = base.length() - commaPlace;

        Fraction baseFraction;
        Fraction cycleFraction;

        try {
            baseFraction = new Fraction(StringUtil.moveComma(base, -exponent), "1");
            cycleFraction = new Fraction(new BigInteger(cycle), new BigInteger(StringUtil.moveComma("1.0", cycle.length())).subtract(BigInteger.ONE)).multiply(tenExp(-exponent));
        } catch (NumberFormatException e) {
            return ApproxFraction.fromEndingDecimal(x);
//            throw new RuntimeException("Fraction.java only supports exact values, and '" + x + "' is not such.");
        }

        Fraction fraction = baseFraction.add(cycleFraction);
        fraction.originalRepresentation = original;
        return fraction;
    }


    private static Fraction tenExp (int exponent) {
        if (exponent < 0) {
            return new Fraction(1, (int) Math.pow(10, -exponent));
        } else if (exponent > 0) {
            return new Fraction((int) Math.pow(10, exponent), 1);
        } else {
            return new Fraction(1, 1);
        }
    }

    public Fraction toEndless () {
        return Fraction.fromDecimal(getOriginalRepresentation(), true);
    }

    public String getOriginalRepresentation () {
        if (originalRepresentation == null) {
            return toDecimal().toString();
        } else {
            return originalRepresentation;
        }
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public Fraction inverse () {
        return new Fraction(denominator, numerator);
    }

    public Fraction abs () {
        return new Fraction(numerator.abs(), denominator.abs());
    }

    public static Fraction valueOf (int integer, boolean endless) {
        return valueOf(String.valueOf(integer), endless);
    }

    public static Fraction valueOf (String string, boolean endless) {
        return fromDecimal(string, endless);
    }

    public static Fraction valueOf (double integer, boolean endless) {
        return valueOf(String.valueOf(integer), endless);
    }

    public static Fraction valueOf (float integer, boolean endless) {
        return valueOf(String.valueOf(integer), endless);
    }




    public boolean isInteger () {
        return denominator.abs().equals(BigInteger.ONE);
    }

    public boolean isNegative () {
        return toDecimal().compareTo(BigDecimal.ZERO) < 0;
    }

    public Fraction factorial () {
        if (!isInteger() || isNegative()) {
            throw new RuntimeException("Factorial is only defined for positive integer fractions, not \"" + toString() + "\"!");
        }

        Fraction result = new Fraction(numerator, denominator);
        for (Fraction i = this.subtract(1); i.getNumerator().compareTo(BigInteger.ZERO) > 0 ; i = i.subtract(1)) {
            result = result.multiply(i);
        }

        return result;
    }

    public Fraction normalize () {
        boolean tooSmall = isNegative();
        if (USE_DEGREES) {
            Fraction result = this;
            if (tooSmall) {
                while (result.toDecimal().signum() < 0) {
                    result = result.add(new Fraction(360, 1));
                }

                return result;
            } else {
                while (result.toDecimal().compareTo(new Fraction(360, 1).toDecimal()) > 0) {
                    result = result.subtract(new Fraction(360, 1));
                }

                return result;
            }
        } else {
            return this;
        }
    }

    @Override
    public Fraction fractionValue() {
        return this;
    }

    @Override
    public boolean isOperator () {
        return false;
    }

    @Override
    public boolean isFraction () {
        return true;
    }



    private static BigInteger lcm (BigInteger num, BigInteger den) {
        return Stream.of(num, den).reduce(new BigInteger("1"), (x, y) -> x.multiply(y.divide(gcd(x, y))));
    }

    private static BigInteger gcd (BigInteger num, BigInteger den) {
        return num.gcd(den);
    }

}
