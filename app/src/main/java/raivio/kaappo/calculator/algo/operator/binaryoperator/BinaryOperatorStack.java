package raivio.kaappo.calculator.algo.operator.binaryoperator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BinaryOperatorStack {
    private List<BinaryOperatorGroup> operatorGroups;

    public BinaryOperatorStack(BinaryOperatorGroup... operators) {
        this(Arrays.stream(operators).collect(Collectors.toList()));
    }

    public BinaryOperatorStack(List<BinaryOperatorGroup> operatorGroups) {
        this.operatorGroups = operatorGroups;
    }

    public BinaryOperatorGroup getGroup (int index) {
        return operatorGroups.get(index);
    }

    public int size () {
        return operatorGroups.size();
    }
}
