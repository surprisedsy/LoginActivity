package com.example.loginactivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private EditText joinIdEdTxt, joinPassEdTxt, joinPassCheckEdTxt,
            joinBirthEdTxt, joinEmailEdTxt, joinNameEdTxt;
    private RadioGroup radioGroup;
    private RadioButton genderRaBtn;
    private Button idCheckBtn, submitBtn, backBtn;

    final List<String> idCheckList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        init();
        buttons();
    }

    public void init() {
        joinIdEdTxt = findViewById(R.id.joinIdText);
        joinPassEdTxt = findViewById(R.id.joinPassText);
        joinPassCheckEdTxt = findViewById(R.id.joinPassCheckText);
        joinNameEdTxt = findViewById(R.id.joinNameText);
        joinBirthEdTxt = findViewById(R.id.joinBirthText);
        joinEmailEdTxt = findViewById(R.id.joinEmailText);

        radioGroup = findViewById(R.id.radioGroup);

        idCheckBtn = findViewById(R.id.idCheckBtn);
        submitBtn = findViewById(R.id.submitBtn);
        backBtn = findViewById(R.id.backBtn);
    }

    public void buttons() {
        idCheckBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    public void checkUserId() {
        final String joinIdCheckStr = joinIdEdTxt.getText().toString();

        firestore.collection("userData")
                .whereEqualTo("Id", joinIdCheckStr)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            idCheckList.add(document.get("Id").toString());
                        }
                        if (joinIdCheckStr.isEmpty())
                            shortToastMessage("아이디를 입력해 주세욧");
                        else if (!idCheckList.contains(joinIdCheckStr))
                            shortToastMessage("사용 가능한 아이디 입니다.");
                        else
                            shortToastMessage("이미 등록된 아이디 입니다.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void insertUserData() {
        Map<String, Object> userInfo = new HashMap<>();

        int radioBtn = radioGroup.getCheckedRadioButtonId();
        genderRaBtn = findViewById(radioBtn);

        String passStr = BCrypt.hashpw(joinPassEdTxt.getText().toString(), BCrypt.gensalt());
        String passCheckStr = joinPassCheckEdTxt.getText().toString();
        String joinEmailStr = joinEmailEdTxt.getText().toString();

        if (!joinPassEdTxt.getText().toString().equals(passCheckStr)) {
            shortToastMessage("비밀번호가 일치하지 않습니다.");
        } else if (idCheckList.contains(joinIdEdTxt.getText().toString())) {
            shortToastMessage("사용중인 아이디로는 가입할 수 없습니다.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(joinEmailStr).matches()) {
            shortToastMessage("이메일 형식으로 입력해 주세요.");
        } else {
            userInfo.put("Id", joinIdEdTxt.getText().toString());
            userInfo.put("Pass", passStr);
            userInfo.put("Name", joinNameEdTxt.getText().toString());
            userInfo.put("Birth", joinBirthEdTxt.getText().toString());
            userInfo.put("Email", joinEmailStr);
            userInfo.put("Gender", genderRaBtn.getText().toString());

            firestore.collection("userData")
                    .add(userInfo)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            shortToastMessage("가입 성공");
                            goToMain();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            shortToastMessage("가입 실패");
                        }
                    });
        }
    }

    public void shortToastMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void goToMain() {
        Intent backIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(backIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMain();
    }

    @Override
    public void onClick(View view) {
        int buttonId = view.getId();
        switch (buttonId)
        {
            case R.id.idCheckBtn:
                checkUserId();
                break;
            case R.id.submitBtn:
                if (joinPassEdTxt.getText().toString().equals(joinPassCheckEdTxt.getText().toString()))
                    insertUserData();
                else
                    insertUserData();
                break;
            case R.id.backBtn:
                goToMain();
                break;
        }
    }
}


