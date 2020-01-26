package com.jkapps.loginphpmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    EditText et_userName, et_password;
    ProgressBar pb;
    TextView tv_pb, tv_createOne;
    CheckBox cb_login;
    Button btnLogin, btnGoogleLogin, btnFacebookLogin;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        et_userName = findViewById(R.id.et_userName);
        et_password = findViewById(R.id.et_password);
        pb = findViewById(R.id.pb);
        tv_pb = findViewById(R.id.tv_pb);
        tv_createOne = findViewById(R.id.tv_createOne);
        cb_login = findViewById(R.id.cb_login);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
        btnFacebookLogin = findViewById(R.id.btnFacebookLogin);

        tv_createOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        String loginStatus = sharedPreferences.getString(getResources().getString(R.string.prefLoginStatus), "");
        if (loginStatus.equals("Logged in.")) {
            startActivity(new Intent(MainActivity.this, StartActivity.class));
        }
    }

    private void userLogin() {

        final String username = et_userName.getText().toString();
        final String password = et_password.getText().toString();

        if (TextUtils.isEmpty(username)) {
            et_userName.setError("Please enter your username.");
            et_userName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please enter your password.");
            et_password.requestFocus();
            return;
        }

        class UserLogin extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pb.setVisibility(View.VISIBLE);
                tv_pb.setVisibility(View.VISIBLE);
                tv_pb.setText("Logging in ...");
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pb.setVisibility(View.GONE);
                tv_pb.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        toastMessage(obj.getString("message"));

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        User user = new User(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("member")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        SharedPreferences.Editor editor = sharedPreferences.edit();  //to merge SharedPrefManager?
                        if (cb_login.isChecked()) {
                            editor.putString(getResources().getString(R.string.prefLoginStatus), "Logged in.");
                        } else {
                            editor.putString(getResources().getString(R.string.prefLoginStatus), "Logged out.");
                        }

                        finish();
                        startActivity(new Intent(getApplicationContext(), StartActivity.class));
                    } else {
                        toastMessage("Invalid username or password.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {

                RequestHandler requestHandler = new RequestHandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);

                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private  void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
