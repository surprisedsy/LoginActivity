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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

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

        idCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserId();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass.getText().toString().equals(passCheck.getText().toString())) {
                    insertUserData();
                    finish();
                } else
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

        String passTxt = BCrypt.hashpw(pass.getText().toString(), BCrypt.gensalt());
        String checkTxt = passCheck.getText().toString();
        String emailTxt = email.getText().toString();

        user.put("Id", id.getText().toString());
        user.put("Pass", passTxt);
        user.put("Name", name.getText().toString());
        user.put("Birth", birth.getText().toString());
        user.put("Email", emailTxt);
        user.put("Gender", gender.getText().toString());

        if (!pass.getText().toString().equals(checkTxt)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        } else {
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
    }

    public void checkUserId() {

        final String editTxtId = id.getText().toString();

        firestore.collection("userData")
                .whereEqualTo("Id", editTxtId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.get("Id").equals(editTxtId)) {
                                Log.d("aaaaaa", editTxtId);
                                Toast.makeText(JoinActivity.this, "이미 등록된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("aaaaaa", editTxtId);
                                Log.d("aaaaaa", "else문 들어감");
                                Toast.makeText(JoinActivity.this, "사용 가능한 아이디 입니다.", Toast.LENGTH_SHORT).show();
                            }

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

    public static boolean validateEmail(String emailStr) {
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


