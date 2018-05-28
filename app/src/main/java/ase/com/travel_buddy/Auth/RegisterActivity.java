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


public class RegisterActivity extends AppCompatActivity {

    private Future<JsonObject> mAuthTask = null;
    private Future<JsonObject> mRegisterTask = null;

    // UI references.
    private EditText mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set up the login form.
        mNameView = findViewById(R.id.name);
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        mConfirmPasswordView = findViewById(R.id.confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
    }


    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String name = mNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirm_password = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(confirm_password) && !doPasswordsMatch(password, confirm_password)) {
            mConfirmPasswordView.setError(getString(R.string.error_match_password));
            focusView = mConfirmPasswordView;
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

        if (TextUtils.isEmpty(name)) {
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            JsonObject registerBodyAuth = new JsonObject();
            final JsonObject registerBodyDatabase = new JsonObject();
            registerBodyAuth.addProperty("email", email);
            registerBodyAuth.addProperty("password", password);
            registerBodyAuth.addProperty("returnSecureToken", true);
            registerBodyDatabase.addProperty("email", email);
            registerBodyDatabase.addProperty("name", name);
            mAuthTask = Ion.with(getApplicationContext())
                    .load(getString(R.string.firebase_register))
                    .setJsonObjectBody(registerBodyAuth)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (result.get("error") == null) {
                                final String idToken = result.get("idToken").getAsString();
                                final String email = result.get("email").getAsString();
                                final String userId = result.get("localId").getAsString();
                                final String refreshToken = result.get("refreshToken").getAsString();
                                mRegisterTask = Ion.with(getApplicationContext())
                                        .load(getString(R.string.firebase_db) + "users.json?auth=" + idToken)
                                        .setJsonObjectBody(registerBodyDatabase)
                                        .asJsonObject()
                                        .setCallback(new FutureCallback<JsonObject>() {
                                            @Override
                                            public void onCompleted(Exception e, JsonObject result) {
                                                if (result.get("error") == null) {
                                                    SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "access_token", idToken);
                                                    SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "email", email);
                                                    SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "user_id", userId);
                                                    SharedPreferencesBuilder.setSharedPreference(getApplicationContext(), "refresh_token", refreshToken);
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    RegisterActivity.this.finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), getString(R.string.error_register_user), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
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

    private boolean doPasswordsMatch(String password1, String password2) {
        return password1.equals(password2);
    }
}
