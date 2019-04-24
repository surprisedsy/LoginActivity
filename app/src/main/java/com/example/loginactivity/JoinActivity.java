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

public class JoinActivity extends AppCompatActivity {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX
            = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

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

        Init();
        buttons();
    }

    public void Init() {
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
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserId();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (joinPassEdTxt.getText().toString().equals(joinPassCheckEdTxt.getText().toString())) {
                    insertUserData();
                } else
                    insertUserData();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMain();
            }
        });
    }

    public void checkUserId() {
        final String checkId = joinIdEdTxt.getText().toString();

        firestore.collection("userData")
                .whereEqualTo("Id", checkId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            idCheckList.add(document.get("Id").toString());
                        }
                        if (!idCheckList.contains(checkId))
                            Toast.makeText(getApplicationContext(), "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "이미 등록된 아이디 입니다.", Toast.LENGTH_SHORT).show();
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
        Map<String, Object> user = new HashMap<>();

        int radioBtn = radioGroup.getCheckedRadioButtonId();
        genderRaBtn = findViewById(radioBtn);

        String passStr = BCrypt.hashpw(joinPassEdTxt.getText().toString(), BCrypt.gensalt());
        String checkStr = joinPassCheckEdTxt.getText().toString();
        String emailStr = joinEmailEdTxt.getText().toString();
//        String emailStr = validateEmail(joinEmailEdTxt.getText().toString());

        user.put("Id", joinIdEdTxt.getText().toString());
        user.put("Pass", passStr);
        user.put("Name", joinNameEdTxt.getText().toString());
        user.put("Birth", joinBirthEdTxt.getText().toString());
        user.put("Email", emailStr);
        user.put("Gender", genderRaBtn.getText().toString());

        if (!joinPassEdTxt.getText().toString().equals(checkStr)) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        } else if (idCheckList.contains(joinIdEdTxt.getText().toString())) {
            Toast.makeText(getApplicationContext(), "사용중인 아이디로는 가입할 수 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            firestore.collection("userData")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "가입 성공", Toast.LENGTH_LONG).show();
                            goToMain();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public void goToMain() {
        Intent backIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(backIntent);
        finish();
    }
}


