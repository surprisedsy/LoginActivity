package com.example.loginactivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import com.example.loginactivity.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainActivity(this);

        showUserInfo();
    }

    public void showUserInfo()
    {
        Intent getData = getIntent();
        String getUserData = getData.getStringExtra("IdInfo");

        db.collection("userData")
                .whereEqualTo("Id", getUserData)
                .get()
                .addOnCompleteListener(task -> {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        binding.userIdTxt.setText("아이디 : " + document.get("Id").toString());
                        binding.userPassTxt.setText("비밀번호 : " +document.get("Pass").toString());
                        binding.userNameTxt.setText("이름 : " +document.get("Name").toString());
                        binding.userBirthTxt.setText("생일 : " +document.get("Birth").toString());
                        binding.userEmailTxt.setText("이메일 : " +document.get("Email").toString());
                        binding.userGenderTxt.setText("성별 : " +document.get("Gender").toString());
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private void goToLogin() {
        Intent mainIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToLogin();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(actionbar);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> goToLogin());

        return true;
    }

}
