package com.example.loginactivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private EditText id, pass, passCheck, name, birth, email;
    private RadioGroup group;
    private RadioButton gender;
    private Button idCheck, submit, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Init();
        buttons();
    }

    public void Init() {
        id = (EditText) findViewById(R.id.joinIdText);
        pass = (EditText) findViewById(R.id.joinPassText);
        passCheck = (EditText) findViewById(R.id.joinPassCheckText);
        name = (EditText) findViewById(R.id.joinNameText);
        birth = (EditText) findViewById(R.id.birthText);
        email = (EditText) findViewById(R.id.emailText);

        group = (RadioGroup) findViewById(R.id.radioGroup);

        idCheck = (Button) findViewById(R.id.idCheckBtn);
        submit = (Button) findViewById(R.id.okBtn);
        back = (Button) findViewById(R.id.backBtn);
    }

    public void buttons() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUserData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void insertUserData() {
        Map<String, Object> user = new HashMap<>();
        int radioBtn = group.getCheckedRadioButtonId();
        gender = (RadioButton) findViewById(radioBtn);

        user.put("Id", id.getText().toString());
        user.put("Pass", pass.getText().toString());
        user.put("Name", name.getText().toString());
        user.put("Birth", birth.getText().toString());
        user.put("Email", validateEmail(email.getText().toString()));
        user.put("Gender", gender.getText().toString());

        firestore.collection("userData")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(JoinActivity.this, "Join Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(JoinActivity.this, "Join Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public static boolean validateEmail(String emailStr)
    {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent backIntent = new Intent(JoinActivity.this, MainActivity.class);
        startActivity(backIntent);
        finish();
    }
}


