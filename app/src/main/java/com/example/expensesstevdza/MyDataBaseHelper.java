package com.example.expensesstevdza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "expenses.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_expenses";
    private static final String TABLE_MAXAMOUNT = "max_amount";
    private static final String COLUMN_MAXAMOUNT = "amount";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "expense_name";
    private static final String COLUMN_AMOUNT = "expense_amount";
    private static final String COLUMN_COMMENT = "expense_comment";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_YEAR = "year";

    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " + COLUMN_AMOUNT + " DOUBLE, " +
                COLUMN_COMMENT + " TEXT, " + COLUMN_MONTH + " TEXT, " + COLUMN_YEAR + " TEXT);";

        db.execSQL(query);

        String query2 = "CREATE TABLE " + TABLE_MAXAMOUNT + " (" + COLUMN_MAXAMOUNT + " INTEGER);";
        db.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { //int i = oldVersion, int i1 = newVersion
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addExpense(String expenseName, Double expenseAmount, String expenseComment, String month, String year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, expenseName);
        cv.put(COLUMN_AMOUNT, expenseAmount);
        cv.put(COLUMN_COMMENT, expenseComment);
        cv.put(COLUMN_MONTH,month);
        cv.put(COLUMN_YEAR, year);
        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Insert data failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Data added successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData(){
//        String selectedMonth = MainActivity.getSpinnerMonthItem();
//        String selectedYear = MainActivity.getSpinnerYearItem();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MONTH + " LIKE '%" + MainActivity.selectedMonth +
                        "' AND "+ COLUMN_YEAR + " LIKE '%" + MainActivity.selectedYear + "'";

        Log.d("Print", query);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }

    void updateData(String row_id, String name, String amount, String comment, String month, int year){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, name);
        cv.put(COLUMN_AMOUNT,amount);
        cv.put(COLUMN_COMMENT,comment);
        cv.put(COLUMN_MONTH,month);
        cv.put(COLUMN_YEAR,year);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[] {row_id});

        if(result == -1){
            Toast.makeText(context,"Update failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"Update successfully", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "_id=?", new String[] {row_id});
        if(result == -1){
            Toast.makeText(context, "delete failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "successfully deleted", Toast.LENGTH_SHORT).show();
        }
    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME);
    }

    public void createTableMaxAmount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_MAXAMOUNT + " (" + COLUMN_MAXAMOUNT + " INTEGER);";
        db.execSQL(query2);
    }

    void addMaxAmount (int maxAmount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_MAXAMOUNT, maxAmount);

        long result = db.insert(TABLE_MAXAMOUNT,null, cv);
        if(result == -1){
            Toast.makeText(context, "Error setting the Max. Amount", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Max. Amount set successfully", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readMaxAmount (){
        String query = "SELECT * FROM " + TABLE_MAXAMOUNT;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query,null);
        }

        return cursor;
    }

    public boolean checkIfMaxAmountTableIsEmpty(){
        boolean empty = true;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MAXAMOUNT, null);

        if (cursor != null && cursor.moveToFirst()) {
            empty = (cursor.getInt (0) == 0);
        }
        cursor.close();

        return empty;
    }
}
