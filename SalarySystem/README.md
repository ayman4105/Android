# Salary System Android App

A simple Android app that estimates an expected salary based on:

- Experience years
- Job title
- Country

The task focuses on the basic Android flow:

```text
XML UI
  ↓
MainActivity.java
  ↓
findViewById()
  ↓
Button click
  ↓
Read inputs
  ↓
Calculate salary
  ↓
Show result
```

---

## 1. Project Idea

The app has three inputs and one output.

```text
User Input
├── Experience Years
├── Job
└── Country

Output
└── Expected Salary
```

The app uses a simple learning formula, not real market salary data.

```text
Base Salary = selected by Job
Experience Bonus = Base Salary × Years × 5%
Country Multiplier = selected by Country
Expected Salary = (Base Salary + Experience Bonus) × Country Multiplier
```

Example:

```text
Experience Years = 2
Job = Engineer
Country = Egypt

Base Salary = 12000
Experience Bonus = 12000 × 2 × 0.05 = 1200
Country Multiplier = 1.0
Expected Salary = (12000 + 1200) × 1.0 = 13200
```

---

## 2. App Screen

The UI is built in `activity_main.xml`.

```text
Screen
└── ScrollView
    └── LinearLayout vertical
        ├── Title: Salary System
        ├── EditText: Experience Years
        ├── EditText: Job
        ├── EditText: Country
        ├── Button: Calculate Expected Salary
        └── TextView: Output Result
```

Why `ScrollView`?

```text
Small screen / keyboard appears
        ↓
User can scroll
        ↓
Inputs stay accessible
```

---

## 3. Important XML IDs

These IDs connect XML to Java.

```xml
android:id="@+id/etExperienceYears"
android:id="@+id/etJob"
android:id="@+id/etCountry"
android:id="@+id/btnCalculate"
android:id="@+id/tvOutput"
```

In Java, they are accessed like this:

```java
etExperienceYears = findViewById(R.id.etExperienceYears);
etJob = findViewById(R.id.etJob);
etCountry = findViewById(R.id.etCountry);
tvOutput = findViewById(R.id.tvOutput);
btnCalculate = findViewById(R.id.btnCalculate);
```

Main rule:

```text
XML id
  ↓
R.id.<id_name>
  ↓
Java variable
```

---

## 4. MainActivity Flow

When the app starts:

```text
Android opens app
        ↓
MainActivity starts
        ↓
onCreate() runs
        ↓
setContentView(activity_main.xml)
        ↓
findViewById() connects views
        ↓
Button waits for user click
```

When the user clicks the button:

```text
btnCalculate clicked
        ↓
calculateExpectedSalary()
        ↓
Read experience years
        ↓
Read job
        ↓
Read country
        ↓
Get base salary
        ↓
Get country multiplier
        ↓
Calculate expected salary
        ↓
Show output in tvOutput
```

---

## 5. Key Functions

### `calculateExpectedSalary()`

This is the main logic function.

```text
Input fields
  ↓
getNumber()
  ↓
getBaseSalary()
  ↓
getCountryMultiplier()
  ↓
Expected salary equation
  ↓
tvOutput.setText()
```

It reads the user data and calculates the expected salary.

---

### `getNumber(EditText editText)`

Used to safely read numeric input from an `EditText`.

```text
EditText text
  ↓
trim spaces
  ↓
if empty → return 0
  ↓
convert String to double
```

Why it is useful:

```text
Empty input ""
  ↓
No crash
  ↓
Return 0
```

---

### `getBaseSalary(String job)`

Returns a base salary based on job title.

```text
engineer   → 12000
developer  → 15000
manager    → 20000
technician → 8000
other      → 10000
```

It uses `contains()` so it can handle values like:

```text
embedded engineer
software developer
project manager
```

---

### `getCountryMultiplier(String country)`

Adjusts salary based on country.

```text
egypt   → 1.0
saudi   → 2.2
uae     → 2.5
germany → 3.0
usa     → 4.0
other   → 1.0
```

---

### `formatNumber(double number)`

Removes `.0` from whole numbers.

```text
13200.0 → 13200
2.0     → 2
13200.5 → 13200.5
```

---

## 6. Safe Area / Status Bar Fix

The app uses `EdgeToEdge` with `WindowInsets` to avoid drawing content under the status bar, camera cutout, or navigation bar.

```text
Edge-to-edge screen
        ↓
Android calculates system bars
        ↓
Get top/bottom/left/right insets
        ↓
Apply padding to root view
        ↓
Content appears in safe area
```

Important code:

```java
EdgeToEdge.enable(this);

ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

    view.setPadding(
            systemBars.left,
            systemBars.top,
            systemBars.right,
            systemBars.bottom
    );

    return insets;
});
```

Why this is better than fixed padding:

```text
Fixed padding like 56dp
  ✗ may fail on different devices
  ✗ may fail with camera cutouts
  ✗ may fail with navigation modes

WindowInsets
  ✓ device-aware
  ✓ works with status bar
  ✓ works with navigation bar
  ✓ works with camera cutout
```

---

## 7. Full App Flow Diagram

```text
User opens app
      ↓
MainActivity.onCreate()
      ↓
EdgeToEdge.enable()
      ↓
setContentView(activity_main.xml)
      ↓
Apply WindowInsets padding
      ↓
findViewById connects views
      ↓
User enters:
  - Experience Years
  - Job
  - Country
      ↓
User clicks Calculate
      ↓
calculateExpectedSalary()
      ↓
getNumber(etExperienceYears)
      ↓
getBaseSalary(job)
      ↓
getCountryMultiplier(country)
      ↓
Expected Salary calculated
      ↓
tvOutput displays result
```

---

## 8. Quick Test Case

Input:

```text
Experience Years: 2
Job: Engineer
Country: Egypt
```

Expected calculation:

```text
Base Salary = 12000
Experience Bonus = 12000 × 2 × 0.05 = 1200
Country Multiplier = 1.0
Expected Salary = 13200
```

Expected output:

```text
Job: Engineer
Country: Egypt
Experience Years: 2
Expected Salary: 13200
```

---

## 9. Common Errors

### `package R does not exist`

Usually caused by a wrong package name.

Correct package for this project:

```java
package com.example.salarysystem;
```

Also check `build.gradle.kts`:

```kotlin
namespace = "com.example.salarysystem"
```

---

### Button does nothing

Check that the XML ID and Java ID match.

```xml
android:id="@+id/btnCalculate"
```

```java
btnCalculate = findViewById(R.id.btnCalculate);
```

Then make sure the click listener exists:

```java
btnCalculate.setOnClickListener(v -> {
    calculateExpectedSalary();
});
```

---

## 10. What This Task Teaches

```text
Android XML UI
EditText input
TextView output
Button click listener
findViewById()
Simple calculation logic
Safe number parsing
WindowInsets safe area handling
```

---

## 11. Final Reminder

The core Android idea:

```text
XML draws the screen.
Java controls the behavior.
```

For this app:

```text
activity_main.xml → input fields and result view
MainActivity.java → salary calculation logic
```
