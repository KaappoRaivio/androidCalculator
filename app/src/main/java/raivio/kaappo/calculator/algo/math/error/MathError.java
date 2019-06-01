package raivio.kaappo.calculator.algo.math.error;

public class MathError extends RuntimeException {
    private final String result;

    public MathError (String cause, String result) {
        super(cause);
        this.result = result;
    }

    public String getResult () {
        return result;
    }
}
