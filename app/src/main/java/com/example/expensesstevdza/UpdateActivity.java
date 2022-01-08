package com.example.expensesstevdza;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
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

public class UpdateActivity extends AppCompatActivity {

    private TextInputLayout expenseNameInputLayout2, expenseAmountInputLayout2, expenseCommentInputLayout2;
    private EditText expenseNameInputEditText2, expenseAmountInputEditText2, expenseCommentInputEditText2;
    private Spinner spinnerMonth2, spinnerYear2;
    private Button updateButton, deleteButton;

    String id, expenseName, expenseAmount, expenseComment, month, year;

    public static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    public static SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
    public static int currentMonth = calendar.get(calendar.MONTH);
    public static int currentYear = calendar.get(calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        currentMonth = calendar.get(calendar.MONTH);
        //creating the spinner data

        ArrayList<String> months2 = new ArrayList<>();

        for(int i = 0; i < 6 ; i++){
            months2.add(new DateFormatSymbols().getMonths()[currentMonth - i]);
            if(currentMonth == 0) { //January is the month 0
                currentMonth = 12;
            }
        }

        ArrayList<Integer> years2 = new ArrayList<>();
        years2.add(currentYear);
        years2.add(currentYear-1);



        //finding the id

        expenseNameInputLayout2 = findViewById(R.id.expenseNameInputLayout2);
        expenseAmountInputLayout2 = findViewById(R.id.expenseAmountInputLayout2);
        expenseCommentInputLayout2 = findViewById(R.id.expenseCommentInputLayout2);
        expenseNameInputEditText2 = findViewById(R.id.expenseNameInputEditText2);
        expenseAmountInputEditText2 = findViewById(R.id.expenseAmountInputEditText2);
        expenseCommentInputEditText2 = findViewById(R.id.expenseCommentInputEditText2);

        spinnerMonth2 = findViewById(R.id.spinnerMonth2);
        spinnerYear2 = findViewById(R.id.spinnerYear2);

        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        //first we call this
        getAndSetIntentData();

        //set the action bar title after getAndSetIntentData Method
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(expenseName);
        }

        //click listener for the Buttons
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expenseNameInputEditText2.getText().toString().equals("")){
                    Toast.makeText(UpdateActivity.this, "Please enter a name for the expense", Toast.LENGTH_LONG).show();
                }else if(expenseAmountInputEditText2.getText().toString().equals("") | expenseAmountInputEditText2.getText().toString().contains("..") | expenseAmountInputEditText2.getText().toString().endsWith(".") | expenseAmountInputEditText2.getText().toString().startsWith(".")){
                    Toast.makeText(UpdateActivity.this, "Please enter an amount", Toast.LENGTH_LONG).show();
                } else {
                    MyDataBaseHelper myDB = new MyDataBaseHelper(UpdateActivity.this);
                    myDB.updateData(id, expenseNameInputEditText2.getText().toString().trim(),
                            expenseAmountInputEditText2.getText().toString(),
                            expenseCommentInputEditText2.getText().toString(),
                            spinnerMonth2.getSelectedItem().toString(),
                            Integer.parseInt(spinnerYear2.getSelectedItem().toString()));
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }


            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        //creating the spinner adapters

        ArrayAdapter<String> monthAdapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                months2
        );

        spinnerMonth2.setAdapter(monthAdapter2);

        ArrayAdapter<Integer> yearAdapter2 = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                years2
        );

        spinnerYear2.setAdapter(yearAdapter2);

    } // End of the onCreate Method

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name") && getIntent().hasExtra("amount") && getIntent().hasExtra("comment") &&
                getIntent().hasExtra("month") && getIntent().hasExtra("year")){
            //Getting Data from Intent
            id = getIntent().getStringExtra("id");
            expenseName = getIntent().getStringExtra("name");
            expenseAmount = getIntent().getStringExtra("amount");
            expenseComment = getIntent().getStringExtra("comment");
            month = getIntent().getStringExtra("month");
            year = getIntent().getStringExtra("year");

            //Setting intent Data
            expenseNameInputEditText2.setText(expenseName);
            expenseAmountInputEditText2.setText(expenseAmount);
            expenseCommentInputEditText2.setText(expenseComment);
            //TODO data from the MainActivity should be passed to the spinnerMonth2 and spinnerYear2 over the intent to the UpdateActivity

        }else{
            Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + expenseName + "?");
        builder.setMessage("Are you sure you want to delete " + expenseName + "?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UpdateActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}