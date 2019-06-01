package raivio.kaappo.calculator.algo.expression;

import lexer.token.SymbolToken;

import math.fraction.approxfraction.ApproxFraction;
import math.fraction.fraction.Fraction;
import math.fraction.fraction.Fractionable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SymbolTable {
    public static final SymbolTable defaultTable;

    static {
        Map<SymbolToken, Fractionable> tempMap = new HashMap<>();

        tempMap.put(new SymbolToken("%"),  new Fraction(1, 100));
        tempMap.put(new SymbolToken("π"),  new ApproxFraction(new BigDecimal("3.14159 26535 89793 23846 26433 83279 50288".replace(" ", ""))));
        tempMap.put(new SymbolToken("p"),  new ApproxFraction(new BigDecimal("3.14159 26535 89793 23846 26433 83279 50288".replace(" ", ""))));
        tempMap.put(new SymbolToken("e"),  new ApproxFraction(new BigDecimal("2.71828 18284 59045 23536 02874 71352 66249".replace(" ", ""))));


        defaultTable = new SymbolTable(tempMap);
    }

    private Map<SymbolToken, Fractionable> symbolTable;

    public SymbolTable (Map<SymbolToken, Fractionable> symbolTable) {
        this.symbolTable = symbolTable;
    }

    public Fractionable getValue (SymbolToken token) {
        return Optional.ofNullable(symbolTable.get(token)).orElseThrow(() -> new RuntimeException("Unknown variable '" + token.getValue() + "'!"));
    }

}
