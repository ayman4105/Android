# Android Studio Quick README

## 1. What is Android Studio?

Android Studio is the official IDE used to build Android applications.

It helps you with:

- Writing Java/Kotlin code
- Designing XML layouts
- Managing SDK versions
- Building the APK
- Running the app on a real phone or emulator
- Debugging errors

---

## 2. First Problem We Fixed: Project JDK

At the beginning, Android Studio showed:

```text
Project JDK is not defined
```

This means Gradle did not know which Java/JDK to use to build the project.

### Fix

Go to:

```text
File
→ Settings
→ Build, Execution, Deployment
→ Build Tools
→ Gradle
→ Gradle JDK
```

Select:

```text
jbr-21 JetBrains Runtime
```

Then run:

```text
File → Sync Project with Gradle Files
Build → Rebuild Project
```

---

## 3. Android Studio Main Areas

```text
+-----------------------------------------------------+
| Top Bar                                             |
| Run / Device / Build / Sync                         |
+----------------------+------------------------------+
| Project Tree         | Code Editor                  |
| app / res / java     | MainActivity.java / XML      |
+----------------------+------------------------------+
| Build / Problems / Logcat                           |
+-----------------------------------------------------+
```

### Important Windows

| Window | Usage |
|---|---|
| Project | Shows app files |
| Gradle | Shows build tasks |
| Problems | Shows code/XML errors |
| Build Output | Shows build result |
| Emulator / Device | Runs the app |

---

## 4. Project Structure

In Android view, the project looks like this:

```text
app
├── manifests
│   └── AndroidManifest.xml
├── kotlin+java
│   └── com.example.calculator
│       └── MainActivity.java
├── res
│   ├── layout
│   │   └── activity_main.xml
│   ├── values
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── mipmap
│       └── app icons
└── Gradle Scripts
```

---

## 5. Important Files

### `AndroidManifest.xml`

This file tells Android what the app contains.

It defines:

- App name
- App icon
- App theme
- Activities
- Launcher screen

Important part:

```xml
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.LAUNCHER" />
</intent-filter>
```

This means:

```text
This Activity is the first screen opened from the app icon.
```

---

### `MainActivity.java`

This is the Java code for the main screen.

The most important function is:

```java
protected void onCreate(Bundle savedInstanceState)
```

`onCreate()` runs when the screen is created.

Basic flow:

```text
User opens app
        ↓
Android starts MainActivity
        ↓
onCreate() runs
        ↓
setContentView(activity_main.xml)
        ↓
UI appears
```

Important line:

```java
setContentView(R.layout.activity_main);
```

This connects Java with the XML layout.

---

### `activity_main.xml`

This file draws the screen.

It contains UI elements like:

- `TextView`
- `Button`
- `GridLayout`
- `ConstraintLayout`

Example relationship:

```xml
<Button
    android:id="@+id/btn7"
    android:text="7" />
```

Java can access it using:

```java
Button btn7 = findViewById(R.id.btn7);
```

---

## 6. XML to Java Connection Flow

```text
activity_main.xml
        ↓
android:id="@+id/btn7"
        ↓
R.id.btn7
        ↓
findViewById(R.id.btn7)
        ↓
Java can control the button
```

---

## 7. Android UI Basic Idea

```text
XML = Shape
Java = Behavior
```

Example:

```text
XML creates Button 7
Java says what happens when Button 7 is clicked
```

---

## 8. Common Android Studio Commands

| Action | Path |
|---|---|
| Sync Gradle | File → Sync Project with Gradle Files |
| Build project | Build → Rebuild Project |
| Run app | Green Run button |
| Open XML preview | Open layout XML → Design/Split |
| Open SDK Manager | Settings → Android SDK |
| Create app icon | app → Right Click → New → Image Asset |

---

## 9. Changing App Icon

Go to:

```text
app
→ Right Click
→ New
→ Image Asset
```

Then:

```text
Icon Type: Launcher Icons
Source Asset: Image
Choose image path
Next
Finish
```

Android Studio generates icons in:

```text
res/mipmap
```

Manifest uses them here:

```xml
android:icon="@mipmap/ic_launcher"
android:roundIcon="@mipmap/ic_launcher_round"
```

If the icon does not update on the phone:

```text
Uninstall app
Run again
```

---

## 10. Quick Mental Model

```text
Manifest
   ↓ decides first screen
MainActivity.java
   ↓ runs code
activity_main.xml
   ↓ draws UI
findViewById()
   ↓ connects Java to XML views
setOnClickListener()
   ↓ handles button clicks
```

---

## Final Summary

Android Studio builds the app using Gradle.  
`AndroidManifest.xml` tells Android how to launch the app.  
`MainActivity.java` controls the screen behavior.  
`activity_main.xml` draws the UI.  
`findViewById()` connects Java code to XML elements.
