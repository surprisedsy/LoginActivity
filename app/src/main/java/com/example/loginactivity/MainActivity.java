package com.example.loginactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.mindrot.jbcrypt.BCrypt;

public class MainActivity extends AppCompatActivity {

    private long backKeyClickTime = 0;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private Button login, join;
    private CheckBox auto;
    private EditText id, pass;

    private SharedPreferences userInfo;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
        click();
        logIn();
        checkAutoLogin();
    }

    public void Init() {
        id = (EditText) findViewById(R.id.idText);
        pass = (EditText) findViewById(R.id.passText);

        auto = (CheckBox) findViewById(R.id.checkBox);
        login = (Button) findViewById(R.id.loginBtn);
        join = (Button) findViewById(R.id.joinBtn);
    }

    public void click() {
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinIntent = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(joinIntent);
            }
        });
    }

    public void logIn() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("userData")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String inputId = id.getText().toString();
                                String inputPass = pass.getText().toString();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String convert = document.get("Id").toString();
                                    String passConvert = document.get("Pass").toString();

                                    if (convert.equals(inputId)) {
                                        if (BCrypt.checkpw(inputPass, passConvert)) {
                                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                            loginIntent.putExtra("IdInfo", id.getText().toString());
                                            startActivity(loginIntent);
                                        } else {
                                            Toast.makeText(MainActivity.this, "비번 안맞음", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });
            }
        });
    }

    public void checkAutoLogin() {
        userInfo = getSharedPreferences("autoLoginData", 0);
        editor = userInfo.edit();

        if (userInfo.getBoolean("AutoLogin", false)) {
            id.setText(userInfo.getString("Id", ""));
            pass.setText(userInfo.getString("Pass", ""));
            auto.setChecked(true);
        }

        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String editID = id.getText().toString();
                    String editPass = pass.getText().toString();

                    editor.putString("Id", editID);
                    editor.putString("Pass", editPass);
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
//        super.onBackPressed();

        if (System.currentTimeMillis() > backKeyClickTime + 1500) {
            backKeyClickTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyClickTime + 1500) {
            this.finish();
        }
    }
}
