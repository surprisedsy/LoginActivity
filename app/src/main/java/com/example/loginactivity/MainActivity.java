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

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private TextView userIdTxt, userPassTxt, userNameTxt,
            userBirthTxt, userEmailTxt, userGenderTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Init();
        showUserInfo();
    }

    public void Init()
    {
        userIdTxt = (TextView) findViewById(R.id.userId);
        userPassTxt = (TextView) findViewById(R.id.userPass);
        userNameTxt = (TextView) findViewById(R.id.userName);
        userBirthTxt = (TextView) findViewById(R.id.userBirth);
        userEmailTxt = (TextView) findViewById(R.id.userEmail);
        userGenderTxt = (TextView) findViewById(R.id.userGender);
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
                            userIdTxt.setText("아이디 : " + document.get("Id").toString());
                            userPassTxt.setText("비밀번호 : " +document.get("Pass").toString());
                            userNameTxt.setText("이름 : " +document.get("Name").toString());
                            userBirthTxt.setText("생일 : " +document.get("Birth").toString());
                            userEmailTxt.setText("이메일 : " +document.get("Email").toString());
                            userGenderTxt.setText("성별 : " +document.get("Gender").toString());
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
