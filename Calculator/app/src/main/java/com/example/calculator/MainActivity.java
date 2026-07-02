package com.example.calculator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    TextView tvResult;
    double firstNumber = 0;
    String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        Button btnPlus = findViewById(R.id.btnPlus);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnEqual = findViewById(R.id.btnEqual);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide = findViewById(R.id.btnDivide);
        Button btnDot = findViewById(R.id.btnDot);
        Button btnBackspace = findViewById(R.id.btnBackspace);

        setNumberButton(R.id.btn0, "0");
        setNumberButton(R.id.btn1, "1");
        setNumberButton(R.id.btn2, "2");
        setNumberButton(R.id.btn3, "3");
        setNumberButton(R.id.btn4, "4");
        setNumberButton(R.id.btn5, "5");
        setNumberButton(R.id.btn6, "6");
        setNumberButton(R.id.btn7, "7");
        setNumberButton(R.id.btn8, "8");
        setNumberButton(R.id.btn9, "9");

        btnClear.setOnClickListener(v -> {
            clearCalculator();
        });

        btnPlus.setOnClickListener(v -> {
            appendOperator("+");
        });
        btnMinus.setOnClickListener(v -> {
            appendOperator("-");

            });

        btnEqual.setOnClickListener(v -> {
            calculateResult();
        });

        btnMultiply.setOnClickListener(v -> {
            appendOperator("*");
        });

        btnDivide.setOnClickListener(v -> {
            appendOperator("/");
        });

        btnDot.setOnClickListener(v -> {
            appendDot();
        });
        btnBackspace.setOnClickListener(v -> {
            deleteLastChar();
        });
    }

    private void setNumberButton(int buttonId, String number) {
        // Get the button from XML by its id.
        Button button = findViewById(buttonId);

        // Run this code when the user clicks the button.
        button.setOnClickListener(v -> {
            appendNumber(number);
        });
    }

    private void appendNumber(String number) {
        // Get the current text from the result screen.
        String currentText = tvResult.getText().toString();

        // Replace 0 with the new number.
        if (currentText.equals("0")) {
            tvResult.setText(number);
        } else {
            // Add the new number beside the old text.
            tvResult.setText(currentText + number);
        }
    }

    private void clearCalculator() {
        // Reset the result screen to zero.
        tvResult.setText("0");
    }

    private void calculateResult() {
        // Get the full expression from the result screen.
        String expression = tvResult.getText().toString();

        // Do nothing if the expression is empty.
        if (expression.isEmpty()) {
            return;
        }

        // Do nothing if the expression ends with an operator.
        if (expression.endsWith("+") || expression.endsWith("-") || expression.endsWith("*") || expression.endsWith("/")) {
            return;
        }

        // Split numbers using all operators.
        String[] numbers = expression.split("[+\\-*/]");

        // If there are no numbers, stop.
        if (numbers.length == 0) {
            return;
        }

        // Start result with the first number.
        double result = Double.parseDouble(numbers[0]);

        // This index points to the next number in numbers array.
        int numberIndex = 1;

        // Loop over the expression to find operators.
        for (int i = 0; i < expression.length(); i++) {
            char currentChar = expression.charAt(i);

            // Check if current character is an operator.
            if (currentChar == '+' || currentChar == '-' || currentChar == '*' || currentChar == '/') {
                // Get the next number.
                double nextNumber = Double.parseDouble(numbers[numberIndex]);

                // Apply the operator.
                if (currentChar == '+') {
                    result = result + nextNumber;
                } else if (currentChar == '-') {
                    result = result - nextNumber;
                } else if (currentChar == '*') {
                    result = result * nextNumber;
                } else if (currentChar == '/') {
                    // Prevent division by zero.
                    if (nextNumber == 0) {
                        tvResult.setText("Error");
                        return;
                    }

                    result = result / nextNumber;
                }

                // Move to the next number.
                numberIndex++;
            }
        }

        // Show the result on the screen.
        tvResult.setText(formatResult(result));
    }

    private void appendOperator(String operator) {
        // Get the current text from the result screen.
        String currentText = tvResult.getText().toString();

        // Do not add an operator if the last character is already an operator.
        if (currentText.endsWith("+") || currentText.endsWith("-") || currentText.endsWith("*") || currentText.endsWith("/")) {
            return;
        }

        // Add the operator to the expression.
        tvResult.setText(currentText + operator);
    }

    private void appendDot() {
        // Get the current text from the result screen.
        String currentText = tvResult.getText().toString();

        // Get the last number only.
        String[] parts = currentText.split("[+\\-*/]");

        // Get the current number being typed.
        String lastNumber = parts[parts.length - 1];

        // Do not add dot if the current number already has a dot.
        if (lastNumber.contains(".")) {
            return;
        }

        // Add dot to the current number.
        tvResult.setText(currentText + ".");
    }

    private String formatResult(double result) {
        // Check if the result is a whole number.
        if (result == (long) result) {
            // Return the number without .0.
            return String.valueOf((long) result);
        }

        // Return the decimal number normally.
        return String.valueOf(result);
    }

    private void deleteLastChar() {
        // Get the current text from the result screen.
        String currentText = tvResult.getText().toString();

        // If there is only one character, reset the screen to zero.
        if (currentText.length() <= 1 || currentText.equals("Error")) {
            tvResult.setText("0");
            return;
        }

        // Remove the last character from the text.
        String newText = currentText.substring(0, currentText.length() - 1);

        // If the new text is empty, show zero.
        if (newText.isEmpty()) {
            tvResult.setText("0");
        } else {
            tvResult.setText(newText);
        }
    }

}





