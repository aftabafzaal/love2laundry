package com.love2laundry.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UKActivity extends Config {


    private String TAG = UKActivity.class.getSimpleName();
    private EditText postCode;
    private View mProgressView;
    private View mUKFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.Config();
        setContentView(R.layout.activity_uk);
        // Set up the login form.
        postCode = findViewById(R.id.postcode);

        Button mCheckPriceButton = findViewById(R.id.postcode_button);
        mCheckPriceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mUKFormView = findViewById(R.id.uk_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        postCode.setError(null);
        // Store values at the time of the login attempt.
        String pc = postCode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        // Check for a valid email address.
        if (TextUtils.isEmpty(pc)) {
            postCode.setError(getString(R.string.error_field_required));
            focusView = postCode;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            sharedpreferences = getSharedPreferences("country", MODE_PRIVATE);
            String server = sharedpreferences.getString("server",null);
            String action = sharedpreferences.getString("loadData",null);

            String q="?type=load&device_id="+androidId+"&post_code="+postCode.getText().toString()+"&versions=1|2|3&more_info=1";
            String url= server+action+q;

            SharedPreferences.Editor sp = sharedpreferences.edit();
            sp.putString("postCode", postCode.getText().toString());
            sp.commit();

            Intent intent=new Intent(UKActivity.this,ListingActivity.class);

            intent.putExtra("request", url);
            startActivity(intent);
        }
    }
}