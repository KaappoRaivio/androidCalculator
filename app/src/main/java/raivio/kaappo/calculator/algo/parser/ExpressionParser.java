package raivio.kaappo.calculator.algo.parser;

import raivio.kaappo.calculator.algo.expression.*;
import raivio.kaappo.calculator.algo.lexer.Lexer;
import raivio.kaappo.calculator.algo.lexer.token.FoundToken;
import raivio.kaappo.calculator.algo.lexer.token.NumberToken;
import raivio.kaappo.calculator.algo.lexer.token.SymbolToken;
import raivio.kaappo.calculator.algo.lexer.token.Token;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;
import raivio.kaappo.calculator.algo.operator.BoundingOperator;
import raivio.kaappo.calculator.algo.operator.binaryoperator.BinaryOperator;
import raivio.kaappo.calculator.algo.operator.genericoperator.GenericOperatorGroup;
import raivio.kaappo.calculator.algo.operator.genericoperator.GenericOperatorStack;
import raivio.kaappo.calculator.algo.operator.genericoperator.Operator;
import raivio.kaappo.calculator.algo.operator.genericoperator.OperatorType;
import raivio.kaappo.calculator.algo.operator.unaryoperator.UnaryOperator;

public class ExpressionParser<T extends Fractionable> {
    private Lexer lexer;
    private ValueProvider<T> valueProvider;
    private GenericOperatorStack genericOperatorStack;
    private SymbolTable symbolTable;
    private boolean mustClose;

    public ExpressionParser (String input, ValueProvider<T> valueProvider, GenericOperatorStack genericOperatorStack, BinaryOperator implicitOperator, SymbolTable symbolTable) {
        this(input, valueProvider, genericOperatorStack, implicitOperator, symbolTable, true);
    }

    public ExpressionParser (String input, ValueProvider<T> valueProvider, GenericOperatorStack genericOperatorStack, BinaryOperator implicitOperator, SymbolTable symbolTable, boolean mustClose) {
        this.symbolTable = symbolTable;
        this.mustClose = mustClose;
        lexer = new Lexer(input, implicitOperator);
        this.valueProvider = valueProvider;
        this.genericOperatorStack = genericOperatorStack;


        if (lexer.isEmpty()) {
            throw new RuntimeException("Input cannot be nothing!");
        }
    }

    public Expression parse () {
        Expression expression = binaryEval(0);

        FoundToken token = lexer.getNextToken();
        if (!token.is(Token.END)) {
            throw new RuntimeException("End expected, got " + token + " instead!");
        } else {
            return expression;
        }
    }

    private Expression binaryEval (int operatorStackPointer) {
        if (operatorStackPointer >= genericOperatorStack.size()) {
            return boundaryUnary();
        }

        GenericOperatorGroup currentOperators = genericOperatorStack.getGroup(operatorStackPointer);
        if (currentOperators.getGroupType() == OperatorType.UNARY) {
            return prefixUnary(operatorStackPointer);
        }

        Expression leftOperand = binaryEval(operatorStackPointer + 1);
        FoundToken operatorToken = lexer.getNextToken();

        if (currentOperators.isOperator(operatorToken)) {
            Operator operator = currentOperators.getOperator(operatorToken);

            if (operator.getOperatorType() != OperatorType.BINARY) {
                throw new RuntimeException("Operator " + operator + " is inside a GenericOperatorGroup of type BINARY but appears to have type " + operator.getOperatorType() + "!");
            }

            BinaryOperator binaryOperator = (BinaryOperator) operator;

            switch (binaryOperator.getEvaluatingOrder()) {
                case LEFT_TO_RIGHT:
                    while (currentOperators.isOperator(operatorToken)) {
                        binaryOperator = (BinaryOperator) currentOperators.getOperator(operatorToken);
                        Expression rightOperand = binaryEval(operatorStackPointer + 1);

//                        leftOperand = binaryOperator.invoke(leftOperand, rightOperand);
                        leftOperand = leftOperand.makeBinaryOperation(binaryOperator, rightOperand);

                        operatorToken = lexer.getNextToken();
                    }

                    lexer.revert();

                    return leftOperand;
                case RIGHT_TO_LEFT:
//                    return binaryOperator.invoke(leftOperand, binaryEval(operatorStackPointer));
                    return leftOperand.makeBinaryOperation(binaryOperator, binaryEval(operatorStackPointer));
               default:
                    throw new RuntimeException("Unknown error " + binaryOperator.getEvaluatingOrder() + "!");
            }
        } else {
//            throw new RuntimeException("Operator " + operatorToken + " is not known by the parser!");
            lexer.revert();
            return leftOperand;
        }
    }


    private Expression prefixUnary (int operatorStackPointer) {
        if (operatorStackPointer >= genericOperatorStack.size()) {
            return boundaryUnary();
        }
        GenericOperatorGroup currentOperators = genericOperatorStack.getGroup(operatorStackPointer);
        if (currentOperators.getGroupType() == OperatorType.BINARY) {
            return binaryEval(operatorStackPointer);
        }

        FoundToken token = lexer.getNextToken();

        if (currentOperators.isOperator(token)) {
            Operator operator = currentOperators.getOperator(token);
            UnaryOperator unaryOperator = (UnaryOperator) operator;
//            return unaryOperator.invoke(prefixUnary(operatorStackPointer));
            return prefixUnary(operatorStackPointer).makeUnaryOperation(unaryOperator);
        } else {
            lexer.revert();
            return binaryEval(operatorStackPointer + 1);
        }

    }

    private Expression number () {
        FoundToken token = lexer.getNextToken();
        Expression value;

        if (token instanceof NumberToken) {
            value = new Expression(valueProvider.valueOf(((NumberToken) token).getValue()));
        } else if (token instanceof SymbolToken) {
            value = new Expression(symbolTable.getValue((SymbolToken) token));
        }
        else {
            System.out.println(lexer);
            throw new RuntimeException("Invalid token " + token);
        }

        return suffixUnary(value);
    }

    private Expression suffixUnary (Expression expression) {
        FoundToken nextToken = lexer.getNextToken();

//        System.out.println(genericOperatorStack.getSuffixOperators() + ", " + nextToken);

        if (genericOperatorStack.getSuffixOperators().isOperator(nextToken)) {
            Operator operator = genericOperatorStack.getSuffixOperators().getOperator(nextToken);

            return suffixUnary(expression.makeUnaryOperation(((UnaryOperator) operator)));
        } else {
            lexer.revert();
            return expression;
        }
    }

    private Expression boundaryUnary () {
        FoundToken nextToken = lexer.getNextToken();

        if (genericOperatorStack.getBoundaryOperators().isOperator(nextToken)) {
            BoundingOperator operator = (BoundingOperator) genericOperatorStack.getBoundaryOperators().getOperator(nextToken);
            Expression operand = binaryEval(0);

            FoundToken expectedClosing = lexer.getNextToken();

            if (operator.getRightToken() != expectedClosing.getTokenType()) {
                if (mustClose) {
                    throw new RuntimeException("Unbalanced bounding operator tokens, expecting " + operator.getRightToken() + " but found " + expectedClosing.getTokenType() + "! " + lexer);
                } else {
                    lexer.revert();
                }
            }


            Node<Payload> parentNode = new Node<>(operator);
            parentNode.addChild(operand.getTree().getParentNode());

            return suffixUnary(new Expression(new Tree<>(parentNode)));
        } else {
            lexer.revert();
            return number();
        }
    }

    public Lexer getLexer() {
        return lexer;
    }

}
