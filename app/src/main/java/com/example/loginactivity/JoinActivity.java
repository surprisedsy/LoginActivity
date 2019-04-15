package com.example.loginactivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.firebase.firestore.FirebaseFirestore;

public class JoinActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private EditText id, pass, passCheck, name, birth, email;
    private RadioButton male, female;
    private Button idCheck, submit, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        Init();
    }

    public void Init()
    {
        id = (EditText) findViewById(R.id.joinIdText);
        pass = (EditText) findViewById(R.id.joinPassText);
        passCheck = (EditText) findViewById(R.id.joinPassCheckText);
        name = (EditText) findViewById(R.id.joinNameText);
        birth = (EditText) findViewById(R.id.birthText);
        email = (EditText) findViewById(R.id.emailText);

        male = (RadioButton) findViewById(R.id.maleRBtn);
        female = (RadioButton) findViewById(R.id.femaleRBtn);

        idCheck = (Button) findViewById(R.id.idCheckBtn);
        submit = (Button) findViewById(R.id.okBtn);
        back = (Button) findViewById(R.id.backBtn);
    }

    public void insertUserData()
    {

    }
}


