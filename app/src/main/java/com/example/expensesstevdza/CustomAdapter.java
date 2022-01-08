package com.example.expensesstevdza;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    public static Context context;
    Activity activity; //to refresh our main activity
    private ArrayList expense_id, expenseName, expenseAmount, expenseComment, month, year;

    public CustomAdapter(Activity activity, Context context,
                         ArrayList expense_id,
                         ArrayList expenseName,
                         ArrayList expenseAmount,
                         ArrayList expenseComment,
                         ArrayList month,
                         ArrayList year) {
        this.activity = activity;
        this.context = context;
        this.expense_id = expense_id;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.expenseComment = expenseComment;
        this.month = month;
        this.year = year;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.expense_id.setText(String.valueOf(expense_id.get(position)));
        holder.expenseName.setText(String.valueOf(expenseName.get(position)));
        holder.expenseAmount.setText(String.valueOf(expenseAmount.get(position)));
        holder.expenseComment.setText(String.valueOf(expenseComment.get(position)));
        holder.month.setText(String.valueOf(month.get(position)));
        holder.year.setText(String.valueOf(year.get(position)));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,UpdateActivity.class);
                intent.putExtra("id", String.valueOf(expense_id.get(position)));
                intent.putExtra("name", String.valueOf(expenseName.get(position)));
                intent.putExtra("amount", String.valueOf(expenseAmount.get(position)));
                intent.putExtra("comment", String.valueOf(expenseComment.get(position)));
                intent.putExtra("month", String.valueOf(month.get(position)));
                intent.putExtra("year", String.valueOf(year.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });

    }

    @Override
    public int getItemCount() {

        return expense_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView expense_id, expenseName, expenseAmount, expenseComment, month, year; //name of the my_row.xml file
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            expense_id = itemView.findViewById(R.id.expense_id);
            expenseName = itemView.findViewById(R.id.expenseName);
            expenseAmount = itemView.findViewById(R.id.expenseAmount);
            expenseComment = itemView.findViewById(R.id.expenseComment);
            month = itemView.findViewById(R.id.month);
            year = itemView.findViewById(R.id.year);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
