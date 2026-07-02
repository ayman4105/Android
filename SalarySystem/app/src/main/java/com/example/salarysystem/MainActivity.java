package com.example.salarysystem;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // This input stores the user's experience years.
    private EditText etExperienceYears;

    // This input stores the user's job title.
    private EditText etJob;

    // This input stores the user's country.
    private EditText etCountry;

    // This TextView shows the expected salary result.
    private TextView tvOutput;

    // This button starts the salary calculation.
    private Button btnCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call the parent Activity setup.
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge drawing.
        EdgeToEdge.enable(this);

        // Connect this Java Activity with activity_main.xml.
        setContentView(R.layout.activity_main);

        // Apply safe padding for status bar, camera cutout, and navigation bar.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            // Get the real system bars size from Android.
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Move app content below status bar and above navigation bar.
            view.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            // Return the same insets after handling them.
            return insets;
        });

        // Connect XML views with Java variables.
        etExperienceYears = findViewById(R.id.etExperienceYears);
        etJob = findViewById(R.id.etJob);
        etCountry = findViewById(R.id.etCountry);
        tvOutput = findViewById(R.id.tvOutput);
        btnCalculate = findViewById(R.id.btnCalculate);

        // Run salary calculation when the user clicks the button.
        btnCalculate.setOnClickListener(v -> {
            calculateExpectedSalary();
        });
    }

    private void calculateExpectedSalary() {
        // Read experience years from the input.
        double years = getNumber(etExperienceYears);

        // Read job title from the input and convert it to lowercase.
        String job = etJob.getText().toString().trim().toLowerCase();

        // Read country from the input and convert it to lowercase.
        String country = etCountry.getText().toString().trim().toLowerCase();

        // Get the base salary based on the job title.
        double baseSalary = getBaseSalary(job);

        // Calculate experience bonus. Each year adds 5% of the base salary.
        double experienceBonus = baseSalary * years * 0.05;

        // Get country multiplier based on the country.
        double countryMultiplier = getCountryMultiplier(country);

        // Calculate the final expected salary.
        double expectedSalary = (baseSalary + experienceBonus) * countryMultiplier;

        // Show the final result on the screen.
        tvOutput.setText(
                "Job: " + etJob.getText().toString() +
                        "\nCountry: " + etCountry.getText().toString() +
                        "\nExperience Years: " + formatNumber(years) +
                        "\nExpected Salary: " + formatNumber(expectedSalary)
        );
    }

    private double getBaseSalary(String job) {
        // Return base salary based on the job title.
        if (job.contains("engineer")) {
            return 12000;
        } else if (job.contains("developer")) {
            return 15000;
        } else if (job.contains("manager")) {
            return 20000;
        } else if (job.contains("technician")) {
            return 8000;
        } else {
            return 10000;
        }
    }

    private double getCountryMultiplier(String country) {
        // Return salary multiplier based on the country.
        if (country.contains("egypt")) {
            return 1.0;
        } else if (country.contains("saudi")) {
            return 2.2;
        } else if (country.contains("uae")) {
            return 2.5;
        } else if (country.contains("germany")) {
            return 3.0;
        } else if (country.contains("usa")) {
            return 4.0;
        } else {
            return 1.0;
        }
    }

    private double getNumber(EditText editText) {
        // Get text from the EditText.
        String text = editText.getText().toString().trim();

        // Return zero if the input is empty.
        if (text.isEmpty()) {
            return 0;
        }

        // Convert the text to a double number.
        return Double.parseDouble(text);
    }

    private String formatNumber(double number) {
        // Remove .0 if the number is whole.
        if (number == (long) number) {
            return String.valueOf((long) number);
        }

        // Return decimal number normally.
        return String.valueOf(number);
    }
}