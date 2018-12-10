package com.example.alarm;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        itemET = findViewById(R.id.item_text);
        btn = findViewById(R.id.add_btn);
        itemsList = findViewById(R.id.items_list);

        items = FileHelper.readData(this);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        itemsList.setAdapter(adapter);
        itemsList.setOnItemClickListener(this);


        btn.setOnClickListener(this);
        findViewById(R.id.buttonSetAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if(Build.VERSION.SDK_INT >= 23) {
                    calendar.set(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getHour(),
                            timePicker.getMinute(),
                            0
                    );
                } else {
                    calendar.set(
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getCurrentHour(),
                            timePicker.getCurrentMinute(),
                            0
                    );

                }
                setAlarm(calendar.getTimeInMillis());

            }
        });
    }

    private void setAlarm(long timeInMillis) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC, timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    private EditText itemET;
    private Button btn;
    private ListView itemsList;
    // a list to receive items
    private ArrayList<String> items;
    // an adapter
    private ArrayAdapter<String> adapter;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                //get the string entered in itemET
                String itemEntered = itemET.getText().toString();
                adapter.add(itemEntered);
                // clear itemET
                itemET.setText("");
                FileHelper.writeFile(items, this);
                //a toast
                Toast.makeText(this, "Successfully Added", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        items.remove(position);
        adapter.notifyDataSetChanged();
        FileHelper.writeFile(items, this);
        Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
    }
}
