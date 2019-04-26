package com.example.loginactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private long mBackKeyClickTime = 0;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Button loginBtn, joinBtn;
    private CheckBox autoLoginCBox;
    private EditText idEdText, passEdText;

    private SharedPreferences userInfo;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        loginBtn.setOnClickListener(this);
        joinBtn.setOnClickListener(this);

        checkAutoLogin();
    }

    public void init() {
        idEdText = findViewById(R.id.idEdText);
        passEdText = findViewById(R.id.passEdText);

        autoLoginCBox =  findViewById(R.id.autoLoginCheckBox);
        loginBtn =  findViewById(R.id.loginBtn);
        joinBtn =  findViewById(R.id.joinBtn);
    }

    public void checkAutoLogin() {
        userInfo = getSharedPreferences("autoLoginData", 0);
        editor = userInfo.edit();

        if (userInfo.getBoolean("AutoLogin", false)) {
            idEdText.setText(userInfo.getString("Id", ""));
            passEdText.setText(userInfo.getString("Pass", ""));
            autoLoginCBox.setChecked(true);
        }

        autoLoginCBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String userID = idEdText.getText().toString();
                    String userPass = passEdText.getText().toString();

                    editor.putString("Id", userID);
                    editor.putString("Pass", userPass);
                    editor.putBoolean("AutoLogin", true);
                    editor.commit();
                } else {
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyClickTime + 1500) {
            mBackKeyClickTime = System.currentTimeMillis();
            shortToastMessage("한번 더 누르시면 종료됩니다.");
            return;
        }
        if (System.currentTimeMillis() <= mBackKeyClickTime + 1500) {
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.loginBtn:
                firestore.collection("userData")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String userId = idEdText.getText().toString();
                                String userPass = passEdText.getText().toString();

                                ArrayList<String> idList = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String convertId = document.get("Id").toString();
                                    String convertPass = document.get("Pass").toString();

                                    idList.add(convertId);

                                    if (convertId.equals(userId)) {
                                        if (BCrypt.checkpw(userPass, convertPass)) {
                                            Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                                            loginIntent.putExtra("IdInfo", userId);

                                            if(!autoLoginCBox.isChecked())
                                            {
                                                idEdText.setText(null);
                                                passEdText.setText(null);
                                            }

                                            startActivity(loginIntent);
                                        } else {
                                            shortToastMessage("비밀번호를 확인해주세요");
                                        }
                                    }
                                }
                                if (!idList.contains(userId))
                                    shortToastMessage("아이디를 확인해주세요");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                break;
            case R.id.joinBtn:
                Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(joinIntent);
                finish();
                break;
        }
    }

    public void shortToastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
