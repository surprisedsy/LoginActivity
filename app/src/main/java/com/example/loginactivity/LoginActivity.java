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
                            id.setText("아이디 : " + document.get("Id").toString());
                            pass.setText("비밀번호 : " +document.get("Pass").toString());
                            name.setText("이름 : " +document.get("Name").toString());
                            birth.setText("생일 : " +document.get("Birth").toString());
                            email.setText("이메일 : " +document.get("Email").toString());
                            gender.setText("성별 : " +document.get("Gender").toString());
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
