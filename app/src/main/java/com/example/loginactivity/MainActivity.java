package com.example.loginactivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long backKeyClickTime = 0;

    private Button login, join;
    private CheckBox auto;
    private EditText id, pass;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();
        click();
        checkAutoLogin();
    }

    public void Init() {
        id = (EditText) findViewById(R.id.idText);
        pass = (EditText) findViewById(R.id.passText);

        auto = (CheckBox) findViewById(R.id.checkBox);
    }

    public void click() {
        login = (Button) findViewById(R.id.loginBtn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        join = (Button) findViewById(R.id.joinBtn);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent joinIntent = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(joinIntent);
            }
        });
    }

    public void checkAutoLogin() {
        settings = getSharedPreferences("settings", 0);
        editor = settings.edit();

        auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String inputId = id.getText().toString();
                String inputPass = pass.getText().toString();

                if (isChecked) {

                    editor.putString("Id", inputId);
                    editor.putString("Pass", inputPass);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();

                } else {
                    editor.clear();
                    editor.commit();
                }
                if (settings.getBoolean("autoLogin", false)) {
                    id.setText(settings.getString("Id", ""));
                    pass.setText(settings.getString("Pass", ""));
                    auto.setChecked(true);
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
