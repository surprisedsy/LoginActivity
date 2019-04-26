package com.example.loginactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.loginactivity.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity{

    private long mBackKeyClickTime = 0;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ActivityLoginBinding binding;

    private SharedPreferences userInfo;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLoginActiity(this);

        checkAutoLogin();
    }

    public void login()
    {
        firestore.collection("userData")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String userId = binding.idEdText.getText().toString();
                        String userPass = binding.passEdText.getText().toString();

                        ArrayList<String> idList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String convertId = document.get("Id").toString();
                            String convertPass = document.get("Pass").toString();

                            idList.add(convertId);

                            if (convertId.equals(userId)) {
                                if (BCrypt.checkpw(userPass, convertPass)) {
                                    Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    loginIntent.putExtra("IdInfo", userId);

                                    if(!binding.autoLoginCheckBox.isChecked())
                                    {
                                        binding.idEdText.setText(null);
                                        binding.passEdText.setText(null);
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
    }

    public void join()
    {
        Intent joinIntent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(joinIntent);
        finish();
    }

    public void checkAutoLogin() {
        userInfo = getSharedPreferences("autoLoginData", 0);
        editor = userInfo.edit();

        if (userInfo.getBoolean("AutoLogin", false)) {
            binding.idEdText.setText(userInfo.getString("Id", ""));
            binding.passEdText.setText(userInfo.getString("Pass", ""));
            binding.autoLoginCheckBox.setChecked(true);
        }

        binding.autoLoginCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    String userID = binding.idEdText.getText().toString();
                    String userPass = binding.passEdText.getText().toString();

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

    public void shortToastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
