package ase.com.travel_buddy.Auth;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.concurrent.Future;

import ase.com.travel_buddy.Main.MainActivity;
import ase.com.travel_buddy.R;
import ase.com.travel_buddy.Utils.SharedPreferencesBuilder;


public class LoginActivity extends AppCompatActivity {


    private Future<JsonObject> mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private TextView mRegisterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPreferencesBuilder.getSharedPreference(getApplicationContext(),"access_token").length() > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);

        mRegisterTextView = findViewById(R.id.register_start_intent);

        mRegisterTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            JsonObject loginBodyAuth = new JsonObject();
            loginBodyAuth.addProperty("email", email);
            loginBodyAuth.addProperty("password", password);
            loginBodyAuth.addProperty("returnSecureToken", true);
            mAuthTask = Ion.with(getApplicationContext())
                    .load(getString(R.string.firebase_login))
                    .setJsonObjectBody(loginBodyAuth)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result.get("error") == null) {
                                String idToken = result.get("idToken").getAsString();
                                String email = result.get("email").getAsString();
                                String userId = result.get("localId").getAsString();
                                String refreshToken = result.get("refreshToken").getAsString();
                                SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "access_token", idToken);
                                SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "email", email);
                                SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "user_id", userId);
                                SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "refresh_token", refreshToken);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_auth_request), Toast.LENGTH_SHORT).show();
                            }
                            mAuthTask = null;
                        }
                    });

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}

