package raivio.kaappo.calculator.algo.operator.genericoperator;

import raivio.kaappo.calculator.algo.lexer.token.FoundToken;


import java.util.Arrays;
import java.util.List;


public class GenericOperatorStack {
    private List<GenericOperatorGroup> operatorGroups;
    private GenericOperatorGroup suffixOperators;
    private GenericOperatorGroup boundaryOperators;

    public GenericOperatorStack(GenericOperatorGroup suffixOperators, GenericOperatorGroup boundaryOperators, GenericOperatorGroup... operators) {
        this(suffixOperators, boundaryOperators, Arrays.asList(operators));
    }

    @Override
    public String toString() {
        return operatorGroups.toString();
    }

    public GenericOperatorGroup getSuffixOperators () {
        return suffixOperators;
    }

    public GenericOperatorGroup getBoundaryOperators () {

        return boundaryOperators;
    }

    public GenericOperatorStack(GenericOperatorGroup suffixOperators, GenericOperatorGroup boundaryOperators, List<GenericOperatorGroup> operatorGroups) {
        this.suffixOperators = suffixOperators;
        this.boundaryOperators = boundaryOperators;
        this.operatorGroups = operatorGroups;
    }

    public GenericOperatorGroup getGroup (int index) {
        return operatorGroups.get(index);
    }

    public int size () {
        return operatorGroups.size();
    }

    public boolean isOperatorRecognized(FoundToken token) {
        return operatorGroups.stream().anyMatch(genericOperatorGroup -> genericOperatorGroup.isOperator(token));
    }
}
