package com.example.realfblaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ManualAttendance extends AppCompatActivity {

    EditText idTxt;

    private String nfcData;
    SimpleDateFormat sdf;

    DatabaseReference databaseAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_attendance);

        idTxt = findViewById(R.id.studentId);

        databaseAttendance = FirebaseDatabase.getInstance().getReference("AttendanceData");

        final Button checkInBtn = (Button) findViewById(R.id.checkInButton);
        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idTxt = findViewById(R.id.studentId);
                if (idTxt != null && idTxt.length() == 6 ) {

                    sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
                    String currentDateAndTime = sdf.format(new Date());

                    nfcData = idTxt.getText().toString() +  "(" + currentDateAndTime + ")0";

                   Toast.makeText(getApplicationContext(), "Successfully checked student in", Toast.LENGTH_LONG).show();

                    parse(nfcData);

                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid ID number. Try Again", Toast.LENGTH_LONG).show();
                }
            }
        });

        final ImageButton backBtn = findViewById(R.id.backBtnAttendanceManual);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        idTxt.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                int numOfChar = (s.toString().length());
                if (numOfChar == 6) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(idTxt.getWindowToken(), 0);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        final Button checkOutBtn = (Button) findViewById(R.id.checkOutButton);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idTxt = findViewById(R.id.studentId);
                if (idTxt != null && idTxt.length() == 6 ) {

                    TextView timeTxt = findViewById(R.id.time);

                    sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
                    String currentDateAndTime = sdf.format(new Date());
                    nfcData = idTxt.getText().toString() +  "(" + currentDateAndTime + ")1";
                    Toast.makeText(getApplicationContext(), "Successfully checked student out", Toast.LENGTH_LONG).show();
                    timeTxt.setText(nfcData);
                    parse(nfcData);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid ID number. Try Again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void parse(String message) {
        String string = message;
        int left = string.indexOf("(");
        int right = string.indexOf(")");

// pull out the text inside the parens
        String sub = string.substring(left+1, right);

        String idNum = string.substring(0, left);
        String timeDate = sub;
        String checkBoolean = string.substring(right+1);

        String id = databaseAttendance.push().getKey();

        AttendenceDatabase attendanceDatabase = new AttendenceDatabase(checkBoolean, idNum, timeDate);
        databaseAttendance.child(id).setValue(attendanceDatabase);

    }

    public void goBack() {
        Intent intent = new Intent(this, Main.class);
        startActivity(intent);
    }
}
