package raivio.kaappo.calculator.algo.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Node<T> implements Serializable {
    private T value;
    private Node<T> parent;
    private List<Node<T>> children;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private boolean flag;

    public Node (T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    private void setParent (Node<T> parent) {
        this.parent = parent;
    }

    @SafeVarargs
    public final Node<T> addChild(Node<T>... child) {
        Arrays.stream(child).forEach(node -> node.setParent(this));
        children.addAll(Arrays.asList   (child));

        return this;
    }

    public T getValue () {
        return value;
    }

    public List<Node<T>> getChildren () {
        return children;
    }

    public Node<T> getParent () {
        return parent;
    }

    @Override
    public String toString () {
        return string(0);
//        return value.toString();
    }

    private String string(int amountOfTab) {

        String tab = new String(new char[amountOfTab]).replace("\0", "\t");;

        if (children.isEmpty()) {
            return tab + value.toString();
        } else {
            return tab + value.toString() + " {\n" + children
                    .stream()
                    .map(item -> item.string(amountOfTab + 1))
                    .collect(Collectors.joining("\n")) + "\n" + tab + "}";
        }
    }



}
