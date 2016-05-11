package com.example.wubingzhang.week9.accounts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.wubingzhang.week9.R;
import com.example.wubingzhang.week9.accounts.Database.*;

public class AccountsActivity extends AppCompatActivity {

    private DailyCostDB dbDaily;
    private SQLiteDatabase db;

    public int day;
    private String month;
    private int monthNumber;
    private int years;

    private TextView totalAmoutTextView;

    private String statusMain;
    private ListView list;
    SimpleCursorAdapter adapter;

    String[] str = {"January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);
        setCurrentDate();

        statusMain = "Expense";

        dbDaily = new DailyCostDB(getApplicationContext());
        db = dbDaily.getWritableDatabase();


        totalAmoutTextView = (TextView) findViewById(R.id.total_amount);
        list = (ListView) findViewById(R.id.list_data_view);
        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountsActivity.this, AddActivity.class);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("year", years);
                startActivity(intent);
            }
        });

        /*LinearLayout settingButton = (LinearLayout) findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountsActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });*/

        /*LinearLayout summaryButton = (LinearLayout) findViewById(R.id.summary_button);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountsActivity.this, SummaryActivity.class);
                intent.putExtra("year", years);
                intent.putExtra("day", day);
                intent.putExtra("month", month);
                intent.putExtra("monthNumber", monthNumber);
                startActivity(intent);
            }
        });*/

        Button calendarButton = (Button) findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AccountsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Log.i("DateChekk", years + "/" + month + "/" + day);
                                years = year;
                                monthNumber = monthOfYear;
                                day = dayOfMonth;
                                month = str[monthOfYear];
                                setDateText();
                                onResume();

                            }
                        }, years, monthNumber, day);
                datePickerDialog.show();
            }
        });


        final int nonClickColor = Color.argb(255, 255, 255, 255);
        final int onClickColor = Color.argb(213, 213, 213, 213);
        final LinearLayout expenseButton = (LinearLayout) findViewById(R.id.expense_button);

        //final LinearLayout incomeButton = (LinearLayout) findViewById(R.id.income_button);

        final LinearLayout backgroundLayout = (LinearLayout) findViewById(R.id.background_main);
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusMain = "Expense";

                //backgroundLayout.setBackgroundColor(Color.parseColor("#f45b3d"));
                expenseButton.setBackgroundColor(onClickColor);
                //incomeButton.setBackgroundColor(nonClickColor);

                onResume();

            }
        });

        /*incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusMain = "Income";

                backgroundLayout.setBackgroundColor(Color.parseColor("#97cf2e"));
                expenseButton.setBackgroundColor(nonClickColor);
                incomeButton.setBackgroundColor(onClickColor);

                onResume();
            }
        });*/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("_id", id);
                intent.putExtra("type", statusMain);
                startActivity(intent);
            }
        });

    }




    private void setDateText() {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        int tmpYear = today.year;
        int tmpMonth = today.month;
        int tmpDay = today.monthDay;

        TextView dateTV = (TextView) findViewById(R.id.data_textview);
        if (day == tmpDay && monthNumber == tmpMonth && years == tmpYear) {
            dateTV.setText("TODAY");
        }
        else {
            dateTV.setText(day + "/" + month + "/" + years);
        }
    }

    private void setCurrentDate() {
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        years = today.year;
        monthNumber = today.month;
        day = today.monthDay;
        month = str[monthNumber];
        Log.i("CurrentDate", years + " / " + month + " / " + day);
    }


    private Cursor getData() {
        String selectQuery = "SELECT " +
                DailyCostDB.COL_ID + "," +
                DailyCostDB.COL_CATALOGUE + "," +
                DailyCostDB.COL_AMOUNT + "," +
                DailyCostDB.COL_IC_ID  +
                " FROM " + DailyCostDB.TABLE_NAME +
                " WHERE " +
                DailyCostDB.COL_DAY + " LIKE '" + day + "'" +
                " AND " +
                DailyCostDB.COL_MONTH + " LIKE '" + month + "'" +
                " AND " +
                DailyCostDB.COL_YEAR + " LIKE '" + years + "'" +
                " AND " +
                DailyCostDB.COL_TYPE + " LIKE '" + statusMain + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;

    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = getData();
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_maint_item,
                cursor,
                new String[] {DailyCostDB.COL_IC_ID, DailyCostDB.COL_CATALOGUE, DailyCostDB.COL_AMOUNT},
                new int[] {R.id.list_ic, R.id.list_catalogues, R.id.list_amount}
        );
        list.setAdapter(adapter);
        if(cursor.moveToFirst()) {
            getAllAmount(cursor);
        }
        else {
            totalAmoutTextView.setText("0");
        }
    }

    private void getAllAmount(Cursor cursor) {
        int sum = 0;
            cursor.moveToFirst();
            do {
                sum += Integer.parseInt(cursor.getString(cursor.getColumnIndex(DailyCostDB.COL_AMOUNT)));
            } while (cursor.moveToNext());
        totalAmoutTextView.setText(String.valueOf(sum));
    }

}
