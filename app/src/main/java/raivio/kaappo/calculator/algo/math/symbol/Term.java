package raivio.kaappo.calculator.algo.math.symbol;

public class Term {
    private int a;
    private int n;

    public int getA() {
        return a;
    }

    public int getN() {
        return n;
    }

    public Term(int a, int n) {
        this.a = a;
        this.n = n;
    }

    public Term plus (Term other) {
        if (!isLike(other)) {
            throw new RuntimeException("Terms " + this + " and " + other + " are not like!");
        } else {
            return new Term(a + other.getA(), getN());
        }
    }

    public boolean isLikeIn (Polynomial other) {
        return other.getTerms().stream().anyMatch(this::isLike);
    }

    public Term findLike (Polynomial other) {
        try {
            return other
                    .getTerms()
                    .stream()
                    .filter(this::isLike)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Didn't find a match for term " + this + " from polynomial " + other));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public boolean isLike (Term other) {
        return n == other.getN();
    }

    @Override
    public String toString() {
        String s;
        switch (n) {
            case 0:
                s = a + "";
                break;
            case 1:
                s = Math.abs(a) + "x";
                break;
            default:
                s = Math.abs(a) + "x^" + n;
                break;
        }
        if (a == 0) {
            return "";
        } else if (a > 0) {
            return "+ " + s;
        } else {
            return "- " + s;
        }
    }
}
