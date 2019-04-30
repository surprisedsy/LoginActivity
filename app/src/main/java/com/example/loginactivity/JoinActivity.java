package com.example.loginactivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.loginactivity.databinding.ActivityJoinBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

public class JoinActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityJoinBinding binding;
    RadioButton genderRaBtn;

    final List<String> idCheckList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join);
        binding.setJoinActivity(this);

    }

    public void checkUserId() {
        final String joinIdCheckStr = binding.joinIdText.getText().toString();

        db.collection("userData")
                .whereEqualTo("Id", joinIdCheckStr)
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        idCheckList.add(document.get("Id").toString());
                    }
                    if (joinIdCheckStr.isEmpty())
                        shortToastMessage("아이디를 입력해 주세욧");
                    else if (!idCheckList.contains(joinIdCheckStr))
                        shortToastMessage("사용 가능한 아이디 입니다.");
                    else
                        shortToastMessage("이미 등록된 아이디 입니다.");
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    public void insertUserData() {
        Map<String, Object> userInfo = new HashMap<>();

        int radioBtn = binding.radioGroup.getCheckedRadioButtonId();
        genderRaBtn = findViewById(radioBtn);

        String idStr = binding.joinIdText.getText().toString();
        String passStr = BCrypt.hashpw(binding.joinPassText.getText().toString(), BCrypt.gensalt());
        String passCheckStr = binding.joinPassCheckText.getText().toString();
        String joinEmailStr = binding.joinEmailText.getText().toString();
        String joinBirthStr = binding.joinBirthText.getText().toString();

        if (idCheckList.contains(idStr)) {
            shortToastMessage("사용중인 아이디로는 가입할 수 없습니다.");
        } else if (!binding.joinPassText.getText().toString().equals(passCheckStr)) {
            shortToastMessage("비밀번호가 일치하지 않습니다.");
        } else if (!validationDate(joinBirthStr)) {
            shortToastMessage("올바른 생일 형식으로 입력해 주세요.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(joinEmailStr).matches()) {
            shortToastMessage("이메일 형식으로 입력해 주세요.");
        }  else {
            userInfo.put("Id", idStr);
            userInfo.put("Pass", passStr);
            userInfo.put("Name", binding.joinNameText.getText().toString());
            userInfo.put("Birth", joinBirthStr);
            userInfo.put("Email", joinEmailStr);
            userInfo.put("Gender", genderRaBtn.getText().toString());

            db.collection("userData")
                    .add(userInfo)
                    .addOnSuccessListener(documentReference -> {
                        shortToastMessage("가입 성공");
                        goToMain();
                    })
                    .addOnFailureListener(e -> shortToastMessage("가입 실패"));
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

    public boolean validationDate(String birth)
    {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            dateFormat.setLenient(false);
            dateFormat.parse(birth);
            return true;

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMain();
    }
}


