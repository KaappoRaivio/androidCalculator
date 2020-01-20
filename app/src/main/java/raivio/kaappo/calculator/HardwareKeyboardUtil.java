package raivio.kaappo.calculator;

import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HardwareKeyboardUtil {
    public static Map<String, String> keyMap = new HashMap<String, String>() {{
        put("+", "+");
        put("-", "–");
        put("*", "×");
        put("/", "÷");

        put("s", "sin(");
        put("c", "cos(");
        put("t", "tan(");
        put("q", "√(");

        put("(", "(");
        put(")", ")");
        put("l", "log(");
        put("!", "!");
        put("^", "^");

        put("e", "e");
        put("p", "π");
        put("%", "%");

        put(",", ".");
        put(".", ".");


        put("0", "0");
        put("1", "1");
        put("2", "2");
        put("3", "3");
        put("4", "4");
        put("5", "5");
        put("6", "6");
        put("7", "7");
        put("8", "8");
        put("9", "9");
    }};

    static boolean isValidInput (String s) {
        return keyMap.containsKey(s);
    }

    static String getInput (String s) {
        return Optional.ofNullable(keyMap.get(s)).orElse("");
    }
}
