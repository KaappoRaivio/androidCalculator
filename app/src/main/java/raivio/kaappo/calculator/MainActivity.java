package raivio.kaappo.calculator;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import raivio.kaappo.calculator.algo.expression.Expression;
import raivio.kaappo.calculator.algo.expression.SymbolTable;
import raivio.kaappo.calculator.algo.math.error.MathError;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fraction;
import raivio.kaappo.calculator.algo.math.fraction.fraction.Fractionable;
import raivio.kaappo.calculator.algo.operator.binaryoperator.BinaryOperator;
import raivio.kaappo.calculator.algo.operator.genericoperator.GenericOperatorGroup;
import raivio.kaappo.calculator.algo.operator.genericoperator.GenericOperatorStack;
import raivio.kaappo.calculator.algo.operator.genericoperator.OperatorType;
import raivio.kaappo.calculator.algo.parser.ExpressionParser;
import raivio.kaappo.calculator.algo.parser.MyValueProvider;

public class MainActivity extends AppCompatActivity {
    private Stack<String> previous = new Stack<>();
    private static boolean useDecimal = false;

    private AsyncTask<String, String, String> currentTask;


    private static final GenericOperatorStack operatorStack = new GenericOperatorStack(
            new GenericOperatorGroup(
                    OperatorType.UNARY,
                    Expression.operatorEll,
                    Expression.operatorFac
            ),

            new GenericOperatorGroup(
                    OperatorType.UNARY,
                    Expression.operatorAbs,
                    Expression.operatorParen
            ),

            new GenericOperatorGroup(OperatorType.BINARY, Expression.operatorAdd, Expression.operatorSub),
            new GenericOperatorGroup(OperatorType.BINARY, Expression.operatorMul, Expression.operatorDiv),
            new GenericOperatorGroup(OperatorType.UNARY,  Expression.operatorNeg, Expression.operatorPos),
            new GenericOperatorGroup(OperatorType.BINARY, Expression.operatorPow, Expression.operatorIPo),
            new GenericOperatorGroup(OperatorType.BINARY, Expression.operatorRot, Expression.operatorIRo),

            new GenericOperatorGroup(OperatorType.UNARY,  Expression.operatorSqr, Expression.operatorISq,
                    Expression.operatorSin, Expression.operatorCos,
                    Expression.operatorTan, Expression.operatorL10,
                    Expression.operatorLo2, Expression.operatorLon,
                    Expression.operatorArcsin, Expression.operatorArccos, Expression.operatorArctan
                    )

    );



    private TextView preview;
    private EditText result;

    private ExpressionParser<Fraction> parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = findViewById(R.id.preview);
        result = findViewById(R.id.result);
        parser = new ExpressionParser<Fraction>("", new MyValueProvider<>(), operatorStack, (BinaryOperator) Expression.operatorMul, SymbolTable.defaultTable, false);


        preview.setText("");
        result.setText("");

        findViewById(R.id.backspace).setOnLongClickListener(view -> {
            result.setText("");
            preview.setText("");

            return true;
        });

    }

    public void onSimpleButtonClick (View view) {
        previous.push(result.getText().toString());
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Button button = (Button) view;

        String toAdd = button.getText().toString();
        result.append(toAdd);

        evalInputAndPreviewResult();

    }

    public void onFunctionButtonClick (View view) {
        previous.push(result.getText().toString());
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        Button button = (Button) view;

        String toAdd = button.getText().toString() + "(";
        result.append(toAdd);

        evalInputAndPreviewResult();
    }

    private void evalInputAndPreviewResult () {
        if (currentTask != null) {
            System.out.println(currentTask.cancel(false));
            System.out.println(currentTask.getStatus());
        }
        currentTask = new MyAsyncTask(this, result, preview, parser);
        currentTask.execute(result.getText().toString());

//        parser.setInput();
//        String result;
//        try {
//            if (useDecimal) {
//                result = parser.parse().reduce().fractionValue().toDecimal().toString();
//            } else {
//                result = parser.parse().reduce().toString();
//            }
//        } catch (RuntimeException e) {
//            return;
//        }
//        preview.setText(result);
    }

    public void commitResult (View view) {
        previous = new Stack<>();
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        result.setText(preview.getText());
        preview.setText("");
    }

    public void delete (View view) {
//        parser.getLexer().deleteToken();
        try {
            result.setText(previous.pop());
        } catch (Exception e) {
            result.setText("");
        }
        evalInputAndPreviewResult();
    }

    public void changeAngleUnit (View view) {
        Fraction.USE_DEGREES = !Fraction.USE_DEGREES;
        if (Fraction.USE_DEGREES) {
            ((Button) view).setText("RAD");
        } else {
            ((Button) view).setText("DEG");
        }

        evalInputAndPreviewResult();
    }

    public void decimalToggle (View view) {
        useDecimal = !useDecimal;
        if (useDecimal) {
            ((Button) view).setText("F");
        } else {
            ((Button) view).setText("D");
        }

        evalInputAndPreviewResult();
    }

    public void onInvertButtonClick (View view) {
//        view.setSelected(true);
        System.out.println("moi");
        if (((ToggleButton) view).isChecked()) {
            ((TextView) findViewById(R.id.sine)).setText("asin");
            ((TextView) findViewById(R.id.cosine)).setText("acos");
            ((TextView) findViewById(R.id.tangent)).setText("atan");
        } else {
            ((TextView) findViewById(R.id.sine)).setText("sin");
            ((TextView) findViewById(R.id.cosine)).setText("cos");
            ((TextView) findViewById(R.id.tangent)).setText("tan");
        }
    }

//    private void vibrate () {
//        Vibrator v = (Vibrator) Objects.requireNonNull(getSystemService(Context.VIBRATOR_SERVICE));
//        v.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
//    }

    static class MyAsyncTask extends AsyncTask<String, String, String> {
        private WeakReference<Activity> context;
        private WeakReference<EditText> result;
        private WeakReference<TextView> preview;
        private ExpressionParser<Fraction> parser;


        MyAsyncTask (Activity context, EditText result, TextView preview, ExpressionParser<Fraction> parser) {
            super();
            this.context = new WeakReference<>(context);
            this.result = new WeakReference<>(result);
            this.preview = new WeakReference<>(preview);
            this.parser = parser;
        }

        @Override
        protected String doInBackground(String... objects) {
            try {

                if (useDecimal) {
                    return parser.setInput(objects[0]).parse().reduce().fractionValue().toString();
                } else {
                    return parser.setInput(objects[0]).parse().reduce().fractionValue().toDecimal().toString();
                }


            } catch (MathError e) {
                return e.getResult();
            }

            catch (Exception e) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            if (context != null) {
                context.get().runOnUiThread(() -> {
                    if (preview != null) {
                        preview.get().setText(o);
                    }
                });
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {

    }
}
