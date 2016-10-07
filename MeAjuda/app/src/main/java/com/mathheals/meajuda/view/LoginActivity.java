package com.mathheals.meajuda.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mathheals.meajuda.R;
import com.mathheals.meajuda.model.User;
import com.mathheals.meajuda.presenter.UserPresenter;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Sets on click listener on login button and new account link
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        TextView newAccountLink = (TextView) findViewById(R.id.newAccountLink);
        newAccountLink.setOnClickListener(this);

    }

    @Override
    public void onClick(View view){
        if(view.getId() == R.id.loginButton){
            //Gets the text on email and password
            EditText emailField = (EditText) findViewById(R.id.emailField);
            String emailTyped = emailField.getText().toString();

            EditText passwordField = (EditText) findViewById(R.id.passwordField);
            String passwordTyped = passwordField.getText().toString();

            UserPresenter userPresenter = UserPresenter.getInstance();
            try{
                String message = userPresenter.authenticateUser(emailTyped, passwordTyped,
                        getBaseContext());

                if(message.equals(getResources().getString(R.string.error_login_or_email))){
                    emailField.requestFocus();
                    emailField.setError(message);
                }
                else if(message.equals(getResources().getString(R.string.error_password))){
                    emailField.requestFocus();
                    emailField.setError(message);
                }
                else{
                    SharedPreferences session = PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext());
                    userPresenter.createLoginSession(emailTyped, session);

                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            } catch(JSONException e){
                e.printStackTrace();
            }

        }
        else if(view.getId() == R.id.newAccountLink){
            UserRegister userRegisterFragment = new UserRegister();
            openFragment(userRegisterFragment);
        }
    }

    private void openFragment(Fragment fragmentToBeOpen){
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.login_container, fragmentToBeOpen);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
