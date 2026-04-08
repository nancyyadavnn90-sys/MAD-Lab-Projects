package com.example.currencyconverterapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    // UI components
    EditText amount;
    Spinner fromCurrency, toCurrency;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply saved theme (Light/Dark mode) before loading UI
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("DarkMode", false);

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Set layout after applying theme
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        amount = findViewById(R.id.amount);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        result = findViewById(R.id.result);

        // Currency list for dropdowns
        String[] currencies = {"INR", "USD", "JPY", "EUR"};

        // Adapter to populate spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromCurrency.setAdapter(adapter);
        toCurrency.setAdapter(adapter);
    }

    // Method triggered when Convert button is clicked
    public void convert(View view) {

        // Conversion rates matrix
        double[][] rates = {
                {1, 0.012, 1.6, 0.011},
                {83, 1, 133, 0.92},
                {0.62, 0.0075, 1, 0.0069},
                {90, 1.08, 145, 1}
        };

        String input = amount.getText().toString();

        // Validate input
        if (input.isEmpty()) {
            result.setText(getString(R.string.enter_amount_error));
            return;
        }

        double amt = Double.parseDouble(input);

        // Get selected currencies
        int from = fromCurrency.getSelectedItemPosition();
        int to = toCurrency.getSelectedItemPosition();

        // Handle same currency case
        if (from == to) {
            result.setText(getString(R.string.same_currency, amt));
            return;
        }

        // Perform conversion
        double converted = amt * rates[from][to];

        // Display result
        result.setText(getString(R.string.converted_value, converted));
    }

    // Open Settings screen
    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}