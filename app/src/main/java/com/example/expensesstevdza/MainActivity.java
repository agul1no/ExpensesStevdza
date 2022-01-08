package com.example.expensesstevdza;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recView;
    private FloatingActionButton fab;
    private TextView totalAmount, textNoData, maximum;
    private ProgressBar progressbar;
    private static Spinner spinnerMainMonth, spinnerMainYear;
    private ImageView imageEmpty;

    MyDataBaseHelper myDB;
    ArrayList<String> expense_id, expenseName, expenseAmount, expenseComment, month, year;
    public static String selectedMonth, selectedYear;
    CustomAdapter customAdapter;

    private double expenseCounter;
    public static int maxAmount = 0;
    private boolean empty = true;

    public static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    public static SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
    public static int currentMonth = calendar.get(Calendar.MONTH);
    public static int currentYear = calendar.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maximum = findViewById(R.id.maximum);
        try {
            //TODO check if the db exists, if don't create it and set maxAmount to 100, if it does exist read the value and set maxAmount to this value
            MyDataBaseHelper db = new MyDataBaseHelper(MainActivity.this);
            empty = db.checkIfMaxAmountTableIsEmpty();

            if(empty){
                maximum.setText("100 €");
                maxAmount = 100;
                db.addMaxAmount(maxAmount);
            }else {
                db.readMaxAmount();
                maxAmount = storeMaxAmountInVar();
                maximum.setText(maxAmount + " €");
            }
        }catch (Exception e){
            Toast.makeText(MainActivity.this, "Error reading the Max Amount Value", Toast.LENGTH_LONG).show();
            maximum.setText("100 €");
            maxAmount = 100;
        }

        currentMonth = calendar.get(Calendar.MONTH);

        textNoData = findViewById(R.id.textNoData);
        imageEmpty = findViewById(R.id.imageEmpty);
        spinnerMainMonth = findViewById(R.id.spinnerMainMonth);
        spinnerMainYear = findViewById(R.id.spinnerMainYear);
        progressbar = findViewById(R.id.progressbar);
        totalAmount = findViewById(R.id.totalAmount);
        recView = findViewById(R.id.recView);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);

            }
        });

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

        //setting the value of the spinners to the adapters
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                months
        );

        spinnerMainMonth.setAdapter(monthAdapter);

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                years
        );

        spinnerMainYear.setAdapter(yearAdapter);

        selectedMonth = getSpinnerMonthItem();
        selectedYear = getSpinnerYearItem();

        //calling the databasehelper and creating the arrays for the recycler view
        myDB = new MyDataBaseHelper(MainActivity.this);
        expense_id = new ArrayList<>();
        expenseName = new ArrayList<>();
        expenseAmount = new ArrayList<>();
        expenseComment = new ArrayList<>();
        month = new ArrayList<>();
        year = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(MainActivity.this,this, expense_id,expenseName,expenseAmount, expenseComment, month, year);
        recView.setAdapter(customAdapter);
        recView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        //setOnItemListener from the spinners
        spinnerMainMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = getSpinnerMonthItem();
                selectedYear = getSpinnerYearItem();
                expense_id.clear(); expenseName.clear(); expenseAmount.clear(); expenseComment.clear(); month.clear(); year.clear();
                myDB.readAllData();
                storeDataInArrays();
                customAdapter = new CustomAdapter(MainActivity.this,MainActivity.this, expense_id,expenseName,expenseAmount, expenseComment, month, year);
                recView.setAdapter(customAdapter);
                recView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                calculateTotalAmountAndSetProgressbar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerMainYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = getSpinnerMonthItem();
                selectedYear = getSpinnerYearItem();
                expense_id.clear(); expenseName.clear(); expenseAmount.clear(); expenseComment.clear(); month.clear(); year.clear();
                myDB.readAllData();
                storeDataInArrays();
                customAdapter = new CustomAdapter(MainActivity.this,MainActivity.this, expense_id,expenseName,expenseAmount, expenseComment, month, year);
                recView.setAdapter(customAdapter);
                recView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                calculateTotalAmountAndSetProgressbar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    } //End of onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    void calculateTotalAmountAndSetProgressbar(){
        // calculate the total of the expenses of the selected month and set the progress bar
        expenseCounter = 0;
        for(int i = 0; i < expenseAmount.size(); i++){
            expenseCounter = expenseCounter + Double.parseDouble(expenseAmount.get(i));
        }
        expenseCounter = (double) Math.round(expenseCounter * maxAmount) / maxAmount;
        totalAmount.setText(String.valueOf(expenseCounter) + " €");
        progressbar.setProgress((int) expenseCounter);
    }

    void updateRecyclerViewAfterSpinner(){
        customAdapter = null;
        storeDataInArrays();
    }

    void storeDataInArrays (){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            imageEmpty.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.VISIBLE);
        }else{
            while (cursor.moveToNext()){
                expense_id.add(cursor.getString(0));
                expenseName.add(cursor.getString(1));
                expenseAmount.add(cursor.getString(2));
                expenseComment.add(cursor.getString(3));
                month.add(cursor.getString(4));
                year.add(cursor.getString(5));
            }
            imageEmpty.setVisibility(View.GONE);
            textNoData.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //MenuInflater to inflate the Menu
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //delete all item
        if(item.getItemId() == R.id.deleteAll){
            confirmDialogDeleteAll();
        }

        //About item
        if(item.getItemId() == R.id.about){
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        //Exit
        if(item.getItemId() == R.id.exit){
            exitMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    public static String getSpinnerMonthItem(){
        String selectedMonth = spinnerMainMonth.getSelectedItem().toString();
        return selectedMonth;
    }

    public static String getSpinnerYearItem(){
        String selectedYear = spinnerMainYear.getSelectedItem().toString();
        return selectedYear;
    }

    void exitMenu(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit the Application?");
        builder.setMessage("Are you sure you want to leave the application?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                System.exit(0);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    void confirmDialogDeleteAll(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete All Records ?");
        builder.setMessage("Are you sure you want to delete all the data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDataBaseHelper myDB = new MyDataBaseHelper(MainActivity.this);
                myDB.deleteAllData();
                //refresh Activity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "All Data was successfully deleted", Toast.LENGTH_LONG).show();
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
        finish();
        System.exit(0);
    }

    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,MaximumActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    int storeMaxAmountInVar (){
        int maxAmount = 100;
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            Toast.makeText(MainActivity.this, "The Max Amount is empty", Toast.LENGTH_SHORT).show();
            maxAmount = 100;
        }else{
            while (cursor.moveToNext()){
                maxAmount = Integer.parseInt(cursor.getString(0));
            }
        }
        return maxAmount;
    }
}