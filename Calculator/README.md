# Calculator App README

## 1. Project Goal

Build a simple Android Calculator app using:

- Java
- XML layout
- `ConstraintLayout`
- `GridLayout`
- Buttons
- `TextView`

The app supports:

- Numbers `0 → 9`
- Operators `+ - * /`
- Decimal point `.`
- Clear `C`
- Backspace `⌫`
- Equal `=`
- Basic result formatting

---

## 2. App UI Structure

The UI is built in:

```text
app/src/main/res/layout/activity_main.xml
```

Main structure:

```text
ConstraintLayout
├── TextView tvResult
└── GridLayout buttonsGrid
    ├── C      /      ⌫      *
    ├── 7      8      9      -
    ├── 4      5      6      +
    ├── 1      2      3      .
    └── 0      =
```
![alt text](<Screenshot from 2026-07-02 16-42-04.png>)

---

## 3. UI Flow

```text
Screen opens
   ↓
TextView shows 0
   ↓
User presses buttons
   ↓
Button click changes tvResult
   ↓
User presses =
   ↓
calculateResult() runs
   ↓
Result appears in tvResult
```

---

## 4. Important XML Elements

### Result Screen

```xml
<TextView
    android:id="@+id/tvResult"
    android:text="0" />
```

This is the calculator display.

Java controls it using:

```java
tvResult = findViewById(R.id.tvResult);
```

---

### Buttons Grid

```xml
<android.widget.GridLayout
    android:columnCount="4"
    android:rowCount="5">
```

`GridLayout` arranges buttons in rows and columns.

`columnCount="4"` means:

```text
Every 4 buttons create one row
```

---

## 5. Java File

The logic is inside:

```text
app/src/main/java/com/example/calculator/MainActivity.java
```

Important flow:

```text
onCreate()
   ↓
setContentView()
   ↓
findViewById()
   ↓
setOnClickListener()
   ↓
calculator functions
```

---

## 6. Main Java Functions

### `setNumberButton()`

Purpose:

```text
Connect number buttons to appendNumber()
```

Instead of writing click code for every number, we use one function:

```java
setNumberButton(R.id.btn7, "7");
```

Flow:

```text
btn7 clicked
   ↓
appendNumber("7")
   ↓
tvResult updates
```

---

### `appendNumber()`

Purpose:

```text
Add the pressed number to the display
```

Example:

```text
Screen = 0
Press 1
Screen = 1

Screen = 1
Press 2
Screen = 12
```

Logic:

```text
If screen is 0:
    replace 0 with new number
Else:
    append number to current text
```

---

### `appendOperator()`

Purpose:

```text
Add + - * / to the expression
```

It prevents invalid input like:

```text
12++
12--
12+*
12//
```

Flow:

```text
Screen = 12
Press +
Screen = 12+
```

---

### `appendDot()`

Purpose:

```text
Add decimal point to the current number
```

It prevents invalid decimal numbers like:

```text
12.5.3
```

Example:

```text
12.5 + 3.2 is allowed
12.5.3 is blocked
```

---

### `clearCalculator()`

Purpose:

```text
Reset screen to 0
```

Flow:

```text
Screen = 12+3
Press C
Screen = 0
```

---

### `deleteLastChar()`

Purpose:

```text
Delete last typed character
```

Examples:

```text
123  → 12
12+  → 12
1    → 0
Error → 0
```

It uses:

```java
substring(0, currentText.length() - 1)
```

---

### `calculateResult()`

Purpose:

```text
Read the full expression and calculate the result
```

Example:

```text
12+3*2
```

Current calculation style:

```text
Left to Right
```

So:

```text
12 + 3 = 15
15 * 2 = 30
```

Result:

```text
30
```

Important note:

```text
This version does not apply operator precedence yet.
```

Meaning:

```text
2 + 3 * 4
```

Current result:

```text
20
```

Real calculator result should be:

```text
14
```

This can be improved later by adding operator precedence.

---

### `formatResult()`

Purpose:

```text
Remove .0 from whole numbers
```

Examples:

```text
3.0  → 3
12.0 → 12
2.5  → 2.5
```

---

## 7. Full App Logic Diagram

```text
User presses button
        ↓
Is it number?
        ↓ yes
appendNumber()
        ↓
Update tvResult

User presses operator
        ↓
appendOperator()
        ↓
Update tvResult

User presses =
        ↓
calculateResult()
        ↓
formatResult()
        ↓
Show final result
```

---

## 8. Button Mapping

| Button | Function |
|---|---|
| `0-9` | `appendNumber()` |
| `+ - * /` | `appendOperator()` |
| `.` | `appendDot()` |
| `C` | `clearCalculator()` |
| `⌫` | `deleteLastChar()` |
| `=` | `calculateResult()` |

---

## 9. Styling the App

The UI style is controlled from XML.

Common attributes:

```xml
android:background="#121212"
android:textColor="#FFFFFF"
android:textSize="22sp"
android:padding="8dp"
android:layout_margin="4dp"
```

To make the keypad fill the screen:

```xml
android:layout_height="0dp"
app:layout_constraintBottom_toBottomOf="parent"
```

For each button:

```xml
android:layout_height="0dp"
android:layout_rowWeight="1"
```

---

## 10. Current Limitation

The calculator currently evaluates operations from left to right.

Example:

```text
2+3*4
```

Current behavior:

```text
2+3 = 5
5*4 = 20
```

Future improvement:

```text
Apply operator precedence:
1. * and /
2. + and -
```

---

## 11. Quick Study Summary

```text
XML draws the calculator
Java controls calculator behavior
TextView displays the expression/result
Buttons call functions
calculateResult reads tvResult and calculates
```

Main app flow:

```text
activity_main.xml
        ↓
MainActivity.java
        ↓
findViewById()
        ↓
setOnClickListener()
        ↓
append / clear / delete / calculate
        ↓
tvResult updated
```

---

## Final Summary

This calculator app teaches the core Android basics:

- XML UI design
- Java Activity logic
- Connecting XML to Java using `findViewById()`
- Handling button clicks using `setOnClickListener()`
- Building expression strings
- Calculating and displaying results
