package com.example.expensesstevdza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class AddActivity extends AppCompatActivity {

    private TextInputLayout expenseNameInputLayout, expenseAmountInputLayout, expenseCommentInputLayout;
    private EditText expenseNameInputEditText, expenseAmountInputEditText, expenseCommentInputEditText;
    private Spinner spinnerMonth, spinnerYear;
    private Button addButton;

    public static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    public static SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
    public static int currentMonth = calendar.get(Calendar.MONTH);
    public static int currentYear = calendar.get(Calendar.YEAR);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        currentMonth = calendar.get(calendar.MONTH);
        //creating the value of the spinners
        ArrayList<String> months = new ArrayList<>();

        for(int i = 0; i < 6 ; i++){
            months.add(new DateFormatSymbols().getMonths()[currentMonth - i]);
            if(currentMonth == 0) { //January is the month 0
                currentMonth = 12;
            }
        }

        ArrayList<Integer> years = new ArrayList<>();
        years.add(currentYear);
        years.add(currentYear-1);

        expenseNameInputLayout = findViewById(R.id.expenseNameInputLayout);
        expenseAmountInputLayout = findViewById(R.id.expenseAmountInputLayout);
        expenseCommentInputLayout = findViewById(R.id.expenseCommentInputLayout2);
        expenseNameInputEditText = findViewById(R.id.expenseNameInputEditText);
        expenseAmountInputEditText = findViewById(R.id.expenseAmountInputEditText);
        expenseCommentInputEditText = findViewById(R.id.expenseCommentInputEditText);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        spinnerYear = findViewById(R.id.spinnerYear);
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Data validation
                if(expenseNameInputEditText.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "Please enter a name for the expense", Toast.LENGTH_LONG).show();
                }else if(expenseAmountInputEditText.getText().toString().equals("") | expenseAmountInputEditText.getText().toString().contains("..") | expenseAmountInputEditText.getText().toString().endsWith(".") | expenseAmountInputEditText.getText().toString().startsWith(".")){
                    Toast.makeText(AddActivity.this, "Please enter a valid amount", Toast.LENGTH_LONG).show();
                } else {
                    MyDataBaseHelper db = new MyDataBaseHelper(AddActivity.this);
                    db.addExpense(expenseNameInputEditText.getText().toString().trim(),
                            Double.valueOf(expenseAmountInputEditText.getText().toString().trim()),
                            expenseCommentInputEditText.getText().toString().trim(),
                            spinnerMonth.getSelectedItem().toString(),
                            spinnerYear.getSelectedItem().toString());
                    expenseNameInputEditText.setText("");
                    expenseAmountInputEditText.setText("");
                    expenseCommentInputEditText.setText("");
                    finish();
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });

        //setting the value of the spinners to the adapters
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                months
        );

        spinnerMonth.setAdapter(monthAdapter);

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                years
        );

        spinnerYear.setAdapter(yearAdapter);

    }// End onCreate

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}