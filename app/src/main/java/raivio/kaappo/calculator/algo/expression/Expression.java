package raivio.kaappo.calculator.algo.expression;

import raivio.kaappo.calculator.algo.lexer.token.Token;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fraction;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;
import raivio.kaappo.calculator.algo.operator.BoundingOperator;
import raivio.kaappo.calculator.algo.operator.binaryoperator.BinaryOperator;
import raivio.kaappo.calculator.algo.operator.binaryoperator.EvaluatingOrder;
import raivio.kaappo.calculator.algo.operator.genericoperator.Operator;
import raivio.kaappo.calculator.algo.operator.genericoperator.OperatorType;
import raivio.kaappo.calculator.algo.operator.unaryoperator.UnaryOperator;
import raivio.kaappo.calculator.algo.operator.unaryoperator.UnaryOperatorType;
import raivio.kaappo.calculator.algo.parser.MyValueProvider;

import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Expression {
    public static final MathContext CONTEXT = MathContext.DECIMAL64;

    public final static Operator operatorAdd = new BinaryOperator(Token.ADD,        (fractionable, fractionable2) -> fractionable.fractionValue().add(fractionable2.fractionValue()), EvaluatingOrder.LEFT_TO_RIGHT);
    public final static Operator operatorSub = new BinaryOperator(Token.SUBTRACT,   (fractionable, fractionable2) -> fractionable.fractionValue().subtract(fractionable2.fractionValue()), EvaluatingOrder.LEFT_TO_RIGHT);
    public final static Operator operatorMul = new BinaryOperator(Token.MULTIPLY,   (fractionable, fractionable2) -> fractionable.fractionValue().multiply(fractionable2.fractionValue()), EvaluatingOrder.LEFT_TO_RIGHT);
    public final static Operator operatorDiv = new BinaryOperator(Token.DIVIDE,     (fractionable, fractionable2) -> fractionable.fractionValue().divide(fractionable2.fractionValue()), EvaluatingOrder.LEFT_TO_RIGHT);
    public final static Operator operatorPow = new BinaryOperator(Token.POWER,      (fractionable, fractionable2) -> fractionable.fractionValue().power(fractionable2.fractionValue()), EvaluatingOrder.RIGHT_TO_LEFT);
    public final static Operator operatorRot = new BinaryOperator(Token.ROOT,       (fractionable, fractionable2) -> fractionable2.fractionValue().root(fractionable.fractionValue().safeIntValue()), EvaluatingOrder.LEFT_TO_RIGHT);
    public final static Operator operatorIPo = new BinaryOperator(Token.INVPOW,     (fractionable, fractionable2) -> fractionable.fractionValue().power(fractionable2.fractionValue().negate()), EvaluatingOrder.RIGHT_TO_LEFT);
    public final static Operator operatorIRo = new BinaryOperator(Token.NEG_ROOT,   (fractionable, fractionable2) -> fractionable2.fractionValue().negate().root(fractionable.fractionValue().safeIntValue()), EvaluatingOrder.LEFT_TO_RIGHT);

    public final static Operator operatorSqr = new UnaryOperator(Token.SQRT,                       (fractionable) -> fractionable.fractionValue().root(BigInteger.valueOf(2)), UnaryOperatorType.PREFIX);
    public final static Operator operatorNeg = new UnaryOperator(Token.SUBTRACT,                   (fractionable) -> fractionable.fractionValue().negate(), UnaryOperatorType.PREFIX);
    public final static Operator operatorPos = new UnaryOperator(Token.ADD,                        (fractionable) -> fractionable, UnaryOperatorType.PREFIX);
    public final static Operator operatorEll = new UnaryOperator(Token.ELLIPSIS,                   (fractionable) -> fractionable.fractionValue().toEndless(), UnaryOperatorType.BOUNDARY);
    public final static Operator operatorFac = new UnaryOperator(Token.EXCLAMATION,                (fractionable) -> fractionable.fractionValue().factorial(), UnaryOperatorType.SUFFIX);
    public final static Operator operatorISq = new UnaryOperator(Token.NEG_SQRT,                   (fractionable) -> fractionable.fractionValue().negate().root(BigInteger.valueOf(2)), UnaryOperatorType.PREFIX);

    public final static Operator operatorSin = new UnaryOperator(Token.SIN,                        (fractionable) -> fractionable.fractionValue().sin(), UnaryOperatorType.PREFIX);
    public final static Operator operatorCos = new UnaryOperator(Token.COS,                        (fractionable) -> fractionable.fractionValue().cos(), UnaryOperatorType.PREFIX);
    public final static Operator operatorTan = new UnaryOperator(Token.TAN,                        (fractionable) -> fractionable.fractionValue().tan(), UnaryOperatorType.PREFIX);
    public final static Operator operatorL10 = new UnaryOperator(Token.LOG10,                      (fractionable) -> fractionable.fractionValue().log10(), UnaryOperatorType.PREFIX);
    public final static Operator operatorLo2 = new UnaryOperator(Token.LOG2,                       (fractionable) -> fractionable.fractionValue().log2(), UnaryOperatorType.PREFIX);
    public final static Operator operatorLon = new UnaryOperator(Token.LN,                         (fractionable) -> fractionable.fractionValue().ln(), UnaryOperatorType.PREFIX);

    public final static Operator operatorAbs = new BoundingOperator(Token.ABS, Token.ABS,          (fractionable) -> fractionable.fractionValue().abs(), "Absolute value");
    public final static Operator operatorParen = new BoundingOperator(Token.LPAREN, Token.RPAREN,  (fractionable) -> fractionable, "Parenthesis");


    public Tree<Payload> getTree () {
        return tree;
    }

    private Tree<Payload> tree;

    public Expression () {
        tree = new Tree<>(new Node<>(new MyValueProvider().valueOf("0")));
    }

    public Expression (Fractionable initialValue) {
        this(new Tree<>(new Node<>(initialValue)));
    }

    public Expression (Tree<Payload> expressionTree) {
        this.tree = expressionTree;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Expression makeBinaryOperation (Operator operator, Expression otherOperand) {
        if (operator.getOperatorType() != OperatorType.BINARY) {
            throw new RuntimeException(operator.toString() + " is not a binary operator!");
        }

        Node<Payload> newParent = new Node<Payload>(operator);
        newParent.addChild(tree.getParentNode(), otherOperand.tree.getParentNode());
        tree = new Tree<>(newParent);

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Expression makeBinaryOperation (BinaryOperator operator, Fractionable otherOperand) {
        if (operator.getOperatorType() != OperatorType.BINARY) {
            throw new RuntimeException(operator.toString() + " is not a binary operator!");
        }

        Node<Payload> newParent = new Node<Payload>(operator);
        newParent.addChild(tree.getParentNode(), new Node<>(otherOperand));
        tree = new Tree<>(newParent);

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Expression makeUnaryOperation (UnaryOperator operator) {
        if (operator.getOperatorType() != OperatorType.UNARY) {
            throw new RuntimeException(operator.toString() + " is not an unary operator!");
        }

        Node<Payload> newParent = new Node<Payload>(operator);
        newParent.addChild(tree.getParentNode());
        tree = new Tree<>(newParent);

        return this;
    }

    public Fractionable reduce () {
        return reduce(getTree().getParentNode());
    }

    private Fractionable reduce (Node<Payload> currentNode) {
        if (currentNode.getValue().isFraction()) {
            return  (Fraction) currentNode.getValue();
        } else {
            Operator operator = (Operator) currentNode.getValue();

            List<Node<Payload>> children = currentNode.getChildren();
            ArrayList<Fractionable> evaluated = children.stream().map(this::reduce).collect(Collectors.toCollection(ArrayList::new));

            if (evaluated.size() != operator.getArity()) {
                throw new RuntimeException("Wrong number of operands " + evaluated + " for operator " + operator + "!");
            }

            return operator.invoke(evaluated);
        }
    }

    @Override
    public String toString() {
        return tree.toString();
    }

    public static void main(String[] args) {
//        final Payload plus = new BinaryOperator(Token.ADD, (fractionable, fractionable2) -> fractionable.fractionValue().add(fractionable2.fractionValue()), EvaluatingOrder.LEFT_TO_RIGHT);
//        final Payload left = new Symbol(3);
//        final Payload right = new Symbol(5);
//
//        Node<Payload> parent = new Node<>(plus);
//        Node<Payload> l = new Node<>(left);
//        Node<Payload> r = new Node<>(right);
//
//        parent.addChild(l, r);
//
//        var tree = new Tree<>(parent);
//        System.out.println(tree);
//
//        final Payload mult = new BinaryOperator(Token.MULTIPLY, (fractionable, fractionable2) -> fractionable.fractionValue().multiply(fractionable2.fractionValue()), EvaluatingOrder.LEFT_TO_RIGHT);
//        Node<Payload> newParent = new Node<>(mult);
//        Node<Payload> nl = new Node<>(right);
//
//        newParent.addChild(nl, parent);
//
//        tree = new Tree<>(newParent);
//        System.out.println(tree);
//        System.out.println("---");

//        Expression expression = new Expression();
//        expression.makeBinaryOperation(operatorAdd, new Symbol("10"));
//        expression.makeUnaryOperation(operatorNeg);
//        expression.makeBinaryOperation(operatorDiv, new Symbol("4"));
//        expression.makeUnaryOperation(operatorSqr);
////        System.out.println(expression);
//
//        for (var a : expression.tree) {
//            System.out.println(a + "::");
//        }

    }
}
