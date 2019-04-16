package com.example.loginactivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private TextView id, pass, name, birth, email, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
        showUserInfo();
    }

    public void Init()
    {
        id = (TextView) findViewById(R.id.userId);
        pass = (TextView) findViewById(R.id.userPass);
        name = (TextView) findViewById(R.id.userName);
        birth = (TextView) findViewById(R.id.userBirth);
        email = (TextView) findViewById(R.id.userEmail);
        gender = (TextView) findViewById(R.id.userGender);
    }

    public void showUserInfo()
    {
        Intent getData = getIntent();
        String data = getData.getStringExtra("IdInfo");

        firestore.collection("userData")
                .whereEqualTo("Id", data)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot document : task.getResult())
                        {
                            id.setText(document.get("Id").toString());
                            pass.setText(document.get("Pass").toString());
                            name.setText(document.get("Name").toString());
                            birth.setText(document.get("Birth").toString());
                            email.setText(document.get("Email").toString());
                            gender.setText(document.get("Gender").toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
