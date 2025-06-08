package com.archive.app.activity; // 确保包名与您的项目结构一致

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.R; // 确保R文件的包名正确

import java.text.DecimalFormat;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CalculatorActivity";

    private TextView tvExpression, tvResult;

    // 状态变量
    private StringBuilder currentInput;         // 当前正在输入的数字字符串
    private StringBuilder expressionDisplay;    // 用于显示在顶部的完整表达式字符串
    private double operand1 = Double.NaN;       // 第一个操作数
    private String pendingOperator = null;      // 等待执行的操作符

    private boolean isNewNumber = true;         // 标记是否开始输入新数字 (例如，在操作符或等于号之后)
    private boolean hasDecimal = false;         // 当前输入数字是否已有小数点
    private boolean operatorJustClicked = false; // 标记操作符是否刚被点击 (用于替换操作符或处理后续输入)

    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

       //  Toolbar (可选，如果您的计算器Activity需要一个标题栏)
   /*      Toolbar toolbar = findViewById(R.id.toolbar_calculator); // 假设您在布局中添加了Toolbar
         setSupportActionBar(toolbar);
         if (getSupportActionBar() != null) {
             getSupportActionBar().setTitle("计算器");
             getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 显示返回键
         }*/

        tvExpression = findViewById(R.id.tv_calculator_expression);
        tvResult = findViewById(R.id.tv_calculator_result);

        currentInput = new StringBuilder();
        expressionDisplay = new StringBuilder();
        decimalFormat = new DecimalFormat("#.##########"); // 最多10位小数，整数不显示小数点

        setClickListeners();
        clearAll(); // 初始化显示
    }

    private void setClickListeners() {
        // 数字按钮
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_dot).setOnClickListener(this);

        // 操作按钮
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_subtract).setOnClickListener(this);
        findViewById(R.id.btn_multiply).setOnClickListener(this);
        findViewById(R.id.btn_divide).setOnClickListener(this);
        findViewById(R.id.btn_percent).setOnClickListener(this); // Modulo
        findViewById(R.id.btn_equals).setOnClickListener(this);

        // 功能按钮
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_backspace).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_0) onDigitClick("0");
        else if (id == R.id.btn_1) onDigitClick("1");
        else if (id == R.id.btn_2) onDigitClick("2");
        else if (id == R.id.btn_3) onDigitClick("3");
        else if (id == R.id.btn_4) onDigitClick("4");
        else if (id == R.id.btn_5) onDigitClick("5");
        else if (id == R.id.btn_6) onDigitClick("6");
        else if (id == R.id.btn_7) onDigitClick("7");
        else if (id == R.id.btn_8) onDigitClick("8");
        else if (id == R.id.btn_9) onDigitClick("9");
        else if (id == R.id.btn_dot) onDecimalClick();
        else if (id == R.id.btn_add) onOperatorClick("+");
        else if (id == R.id.btn_subtract) onOperatorClick("-");
        else if (id == R.id.btn_multiply) onOperatorClick("×");
        else if (id == R.id.btn_divide) onOperatorClick("÷");
        else if (id == R.id.btn_percent) onOperatorClick("%_mod"); // 特殊标记求余
        else if (id == R.id.btn_equals) onEqualsClick();
        else if (id == R.id.btn_clear) clearAll();
        else if (id == R.id.btn_backspace) onBackspaceClick();

        updateDisplay();
    }

    private void onDigitClick(String digit) {
        if (isNewNumber) {
            currentInput.setLength(0); // 清空当前输入
            hasDecimal = false;
            isNewNumber = false;
            if (!digit.equals("0")) operatorJustClicked = false; // 开始输入数字，不再是刚点完操作符
        }

        // 防止过长输入 (可选)
        if (currentInput.length() < 15) {
            // 处理数字"0"的特殊情况，避免出现 "00", "07" 等
            if (currentInput.length() == 1 && currentInput.toString().equals("0") && !digit.equals(".")) {
                if (!digit.equals("0")) { // 如果当前是"0"，且新输入不是"0"
                    currentInput.setLength(0);
                    expressionDisplay.setLength(expressionDisplay.length() -1); // 移除表达式中的尾随0
                }
            }
            currentInput.append(digit);
            expressionDisplay.append(digit);
        }
        operatorJustClicked = false; // 输入数字后，标记操作符不是刚点击的
        Log.d(TAG, "Digit clicked: " + digit + ", currentInput: " + currentInput);
    }

    private void onDecimalClick() {
        if (isNewNumber) { // 如果是新数字，先补0
            currentInput.setLength(0);
            currentInput.append("0");
            expressionDisplay.append("0");
            isNewNumber = false;
            operatorJustClicked = false;
        }
        if (!hasDecimal) {
            if (currentInput.length() == 0) { // 如果当前输入为空，先补0
                currentInput.append("0");
                expressionDisplay.append("0");
            }
            currentInput.append(".");
            expressionDisplay.append(".");
            hasDecimal = true;
        }
        operatorJustClicked = false;
        Log.d(TAG, "Decimal clicked, currentInput: " + currentInput);
    }

    private void onOperatorClick(String operatorSymbol) {
        String displayOp = operatorSymbol.equals("%_mod") ? "%" : operatorSymbol;

        if (currentInput.length() == 0 && Double.isNaN(operand1)) {
             // 开始就输入操作符，且不是减号 (用于负数)，则忽略
            if (operatorSymbol.equals("-") && currentInput.length() == 0) {
                 currentInput.append("-");
                 expressionDisplay.append("-");
                 isNewNumber = false; // 允许继续输入数字
                 operatorJustClicked = false;
                 updateDisplay();
                 return;
            }
            Log.d(TAG, "Operator clicked with no prior input: " + operatorSymbol);
            return; 
        }

        if (operatorJustClicked) { // 用户连续点击操作符，替换上一个
            if (expressionDisplay.length() > 0 && !Character.isDigit(expressionDisplay.charAt(expressionDisplay.length() -1)) && expressionDisplay.charAt(expressionDisplay.length() -1) != '.') { //确保最后一个是操作符
                 expressionDisplay.setLength(expressionDisplay.length() - 1); // 移除旧操作符显示
            }
            expressionDisplay.append(displayOp);
            pendingOperator = operatorSymbol;
            Log.d(TAG, "Operator changed to: " + operatorSymbol);
            updateDisplay(); // 更新显示，但不执行计算
            return;
        }

        // 如果当前有输入，先尝试计算
        if (currentInput.length() > 0) {
            try {
                double currentNumber = Double.parseDouble(currentInput.toString());
                if (!Double.isNaN(operand1) && pendingOperator != null) {
                    operand1 = performCalculation(operand1, currentNumber, pendingOperator);
                    tvResult.setText(decimalFormat.format(operand1)); // 显示中间结果
                } else {
                    operand1 = currentNumber;
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing current input: " + currentInput, e);
                Toast.makeText(this, "输入错误", Toast.LENGTH_SHORT).show();
                clearAll();
                return;
            }
        }
        // else if (Double.isNaN(operand1)) { // 如果operand1还是NaN，说明这是第一个数字刚输入完点操作符
             // (逻辑已在上面处理：operand1 = currentNumber)
        // }

        pendingOperator = operatorSymbol;
        expressionDisplay.append(displayOp);
        currentInput.setLength(0);
        hasDecimal = false;
        isNewNumber = true;
        operatorJustClicked = true;
        Log.d(TAG, "Operator clicked: " + operatorSymbol + ", operand1: " + operand1);
    }

    private void onEqualsClick() {
        if (Double.isNaN(operand1) || pendingOperator == null || currentInput.length() == 0) {
            Log.d(TAG, "Equals clicked with insufficient data.");
            // 如果有operand1但没有currentInput，可以考虑重复上次操作，或直接返回
            if(!Double.isNaN(operand1) && pendingOperator != null && currentInput.length() == 0 && expressionDisplay.length() > 0){
                 // 避免 A + = 的情况, 这种情况通常等于 A+A
                 // 但为了简单，这里什么也不做，除非有明确的 currentInput
            }
            return;
        }

        try {
            double currentNumber = Double.parseDouble(currentInput.toString());
            double result = performCalculation(operand1, currentNumber, pendingOperator);
            
            // expressionDisplay.append("="); // 不再在此处加等于号，由updateDisplay统一处理
            tvResult.setText(decimalFormat.format(result));
            // expressionDisplay.append(decimalFormat.format(result)); // 显示完整表达式

            // 为下一次计算做准备
            operand1 = result; // 结果作为下一次运算的第一个操作数
            pendingOperator = null;
            currentInput.setLength(0);
            currentInput.append(decimalFormat.format(result)); // 当前输入变为结果
            hasDecimal = currentInput.toString().contains(".");
            isNewNumber = true; 
            operatorJustClicked = false; // 等于后，不再是操作符刚点击状态
            Log.d(TAG, "Equals clicked, result: " + result);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing current input on equals: " + currentInput, e);
            Toast.makeText(this, "计算错误", Toast.LENGTH_SHORT).show();
            clearAll();
        }
        // updateDisplay() 会在onClick的末尾调用，这里不再单独调用，避免显示不一致
    }

    private void clearAll() {
        currentInput.setLength(0);
        expressionDisplay.setLength(0);
        operand1 = Double.NaN;
        pendingOperator = null;
        isNewNumber = true;
        hasDecimal = false;
        operatorJustClicked = false;
        tvResult.setText("0");
        tvExpression.setText("");
        Log.d(TAG, "Calculator cleared.");
    }

    private void onBackspaceClick() {
        if (!isNewNumber && currentInput.length() > 0) {
            char removedChar = currentInput.charAt(currentInput.length() - 1);
            currentInput.setLength(currentInput.length() - 1);
            if (expressionDisplay.length() > 0) {
                 expressionDisplay.setLength(expressionDisplay.length() -1);
            }
            if (removedChar == '.') {
                hasDecimal = false;
            }
            if (currentInput.length() == 0) {
                // 如果通过退格清空了当前输入，行为类似于刚输入完一个操作数
                // isNewNumber = true; // 允许重新输入新数字，但不完全重置
                // tvResult.setText("0");
            }
        } else if (operatorJustClicked && expressionDisplay.length() > 0) {
            // 如果是刚点了操作符，则删除操作符
            expressionDisplay.setLength(expressionDisplay.length() -1);
            pendingOperator = null;
            operatorJustClicked = false;
            // 恢复到操作符前的状态可能比较复杂，简单处理：允许重新输入操作符或数字
            isNewNumber = false; // 允许基于operand1继续输入
        }
        // 如果expressionDisplay为空，则tvResult也应该为0
        if(expressionDisplay.length() == 0){
            tvResult.setText("0");
        }
        Log.d(TAG, "Backspace clicked, currentInput: " + currentInput);
    }

    private double performCalculation(double num1, double num2, String op) {
        Log.d(TAG, "Performing calculation: " + num1 + " " + op + " " + num2);
        switch (op) {
            case "+":
                return num1 + num2;
            case "-":
                return num1 - num2;
            case "×":
                return num1 * num2;
            case "÷":
                if (num2 == 0) {
                    Toast.makeText(this, "除数不能为零", Toast.LENGTH_SHORT).show();
                    clearAll(); // 或者返回NaN等错误状态
                    return Double.NaN; // 返回NaN表示错误
                }
                return num1 / num2;
            case "%_mod": // Modulo
                 if (num2 == 0) {
                    Toast.makeText(this, "模数不能为零", Toast.LENGTH_SHORT).show();
                    clearAll();
                    return Double.NaN;
                }
                return num1 % num2;
            default:
                return num2; //或num1，或抛异常
        }
    }

    private void updateDisplay() {
        if (currentInput.length() > 0) {
            tvResult.setText(currentInput.toString());
        } else if (!Double.isNaN(operand1) && pendingOperator == null && !isNewNumber) {
             // 等于之后，operand1存有结果，currentInput为空，但isNewNumber为true
             // 这种情况会在等于后，下一次输入数字前，这里是为了避免清空显示
        } else if (expressionDisplay.length() == 0 ){
            tvResult.setText("0");
        }
        // else tvResult.setText("0"); // 如果没有当前输入，显示0

        tvExpression.setText(expressionDisplay.toString());
        Log.v(TAG, "Display updated. Expression: '" + expressionDisplay.toString() + "', Result/Input: '" + tvResult.getText().toString() + "'");
    }

    // 如果您的Activity需要Toolbar返回键功能
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    //     if (item.getItemId() == android.R.id.home) {
    //         finish(); // 关闭当前Activity
    //         return true;
    //     }
    //     return super.onOptionsItemSelected(item);
    // }
} 