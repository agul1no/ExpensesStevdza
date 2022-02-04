package com.example.expensesstevdza;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MaximumActivity extends AppCompatActivity {

    private EditText maxAmountInputEditText;
    private Button applyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximum);

        maxAmountInputEditText = findViewById(R.id.maxAmountInputEditText);
        applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(maxAmountInputEditText.getText().toString().contains(".")){
                    Toast.makeText(MaximumActivity.this, "Please introduce a valid amount", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO the value the user enters should be store in the database and call whenever the user runs the app till it would be changed
                    MyDataBaseHelper db = new MyDataBaseHelper(MaximumActivity.this);
                    db.createTableMaxAmount();
                    db.updateMaxAmount(Integer.parseInt(maxAmountInputEditText.getText().toString()));
                    maxAmountInputEditText.setText("");
                    Intent intent = new Intent(MaximumActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }
        });

    } //End of onCreate

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MaximumActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}