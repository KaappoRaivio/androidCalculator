package raivio.kaappo.calculator;

import android.view.KeyEvent;

import java.util.HashMap;
import java.util.Map;

public class HardwareKeyboardUtil {
    public static Map<Integer, String> keyMap = new HashMap<Integer, String>() {{
        put(KeyEvent.KEYCODE_0, "0");
        put(KeyEvent.KEYCODE_1, "1");
        put(KeyEvent.KEYCODE_2, "2");
        put(KeyEvent.KEYCODE_3, "3");
        put(KeyEvent.KEYCODE_4, "4");
        put(KeyEvent.KEYCODE_5, "5");
        put(KeyEvent.KEYCODE_6, "6");
        put(KeyEvent.KEYCODE_7, "7");
        put(KeyEvent.KEYCODE_8, "8");
        put(KeyEvent.KEYCODE_9, "9");

//        put(KeyEvent.KEYCODE_MULTI)
    }};
}
