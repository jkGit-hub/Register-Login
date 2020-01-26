package com.jkapps.loginphpmysql;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    TextView tv_id, tv_userName, tv_email, tv_member, tv_callName;
    Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //if the user is not logged in, start the login activity
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }


        tv_id = findViewById(R.id.tv_id);
        tv_userName = findViewById(R.id.tv_userName);
        tv_email = findViewById(R.id.tv_email);
        tv_member = findViewById(R.id.tv_member);

        tv_callName = findViewById(R.id.tv_callName);

        //getting the current user
        User user = SharedPrefManager.getInstance(this).getUser();

        tv_id.setText(String.valueOf(user.getId()));
        tv_userName.setText(user.getUsername());
        tv_email.setText(user.getEmail());
        tv_member.setText(user.getMember());

        tv_callName.setText("Hey, " + user.getUsername() + "!");

        btnLogOut = findViewById(R.id.btnLogOut);

        final SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(getResources().getString(R.string.prefLoginStatus), "Logged Out.");
                editor.apply();
                SharedPrefManager.getInstance(getApplicationContext()).logout();
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
