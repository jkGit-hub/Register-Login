package com.jkapps.loginphpmysql;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText et_userName, et_email, et_password, et_passwordConfirm;
    RadioGroup radioGroupMember;
    ProgressBar pb;
    TextView tv_pb, tv_cancel;
    Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        //if the user is already logged in we will directly start the start activity
//        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
//            finish();
//            startActivity(new Intent(this, StartActivity.class));
//            return;
//        }

        et_userName = findViewById(R.id.et_userName);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_passwordConfirm = findViewById(R.id.et_passwordConfirm);
        radioGroupMember = findViewById(R.id.radioMember);

        pb = findViewById(R.id.pb);
        tv_pb = findViewById(R.id.tv_pb);

        btnRegister = findViewById(R.id.btnRegister);
        tv_cancel = findViewById(R.id.tv_cancel);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void registerUser() {
        final String username = et_userName.getText().toString().trim();
        final String email = et_email.getText().toString().trim();
        final String password = et_password.getText().toString().trim();
        final String password_confirm = et_passwordConfirm.getText().toString().trim();
        final String member = ((RadioButton) findViewById(radioGroupMember.getCheckedRadioButtonId())).getText().toString();

        //validating ..
        if (TextUtils.isEmpty(username)) {
            et_userName.setError("Please enter your username.");
            et_userName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            et_email.setError("Email is required in case you forget your password.");
            et_email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("This Email is invalid.");
            et_email.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            et_password.setError("Enter your password.");
            et_password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password_confirm)) {
            et_passwordConfirm.setError("Confirm your password.");
            et_passwordConfirm.requestFocus();
            return;
        }

        if (!password_confirm.equals(password)) {
            et_passwordConfirm.setError("Your password and confirmation password do not match.");
            et_passwordConfirm.requestFocus();
            return;
        }

        class RegisterUser extends AsyncTask<Void, Void, String> {

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("member", member);

                //returning the response
                return requestHandler.sendPostRequest(URLs.URL_REGISTER, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb.setVisibility(View.VISIBLE);
                tv_pb.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pb.setVisibility(View.GONE);
                tv_pb.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {
                        toastMessage(obj.getString("message"));

                        JSONObject userJson = obj.getJSONObject("user");

                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("member")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        toastMessage("Error occurred.  Please try again.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
