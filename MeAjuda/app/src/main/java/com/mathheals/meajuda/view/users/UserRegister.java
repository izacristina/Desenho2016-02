package com.mathheals.meajuda.view.users;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mathheals.meajuda.R;
import com.mathheals.meajuda.dao.JSONHelper;
import com.mathheals.meajuda.model.School;
import com.mathheals.meajuda.model.User;
import com.mathheals.meajuda.presenter.UserPresenter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserRegister extends Fragment implements View.OnClickListener, NoCopySpan {
    private EditText firstNameField = null;
    private EditText lastNameField = null;
    private EditText mailField = null;
    private EditText mailConfirmationField = null;
    private EditText usernameField = null;
    private EditText passwordField = null;
    private EditText passwordConfirmField = null;
    private AutoCompleteTextView schoolField = null;

    private String firstName = "";
    private String lastName = "";
    private String username = "";
    private String mail = "";
    private String password = "";
    private String passwordConfirmation = "";
    private String mailConfirmation = "";
    private String school = "";

    public UserRegister(){
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_user_register, container, false);

        settingEditText(view);

        Button register = (Button) view.findViewById(R.id.saveButton);

        register.setOnClickListener(this);

        return view;
    }

    private void settingEditText(View view){
        this.firstNameField = (EditText) view.findViewById(R.id.firstNameField);
        this.lastNameField = (EditText) view.findViewById(R.id.lastNameField);
        this.mailField = (EditText) view.findViewById(R.id.mailField);
        this.usernameField = (EditText) view.findViewById(R.id.loginField);
        this.passwordField = (EditText) view.findViewById(R.id.passwordField);
        this.mailConfirmationField = (EditText) view.findViewById(R.id.confirmMailField);
        this.passwordConfirmField = (EditText) view.findViewById(R.id.confirmPasswordField);
        this.schoolField = (AutoCompleteTextView) view.findViewById(R.id.schoolField);

        schoolField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<School> schoolList = new ArrayList<>();

                try {
                    schoolList = JSONHelper.schoolListFromName(s.toString().replaceAll(" ", "%20"), 3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                List<String> schoolNameList = new ArrayList<>();

                for(int i = 0; i < schoolList.size(); i++) {
                    schoolNameList.add(schoolList.get(i).getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, schoolNameList);
                schoolField.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        List<String> countries = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,countries);
        schoolField.setAdapter(adapter);
    }



    @Override
    public void onClick(View v){
        getTextTyped();

        UserPresenter userPresenter = UserPresenter.getInstance(getContext());

        List<School> school = new ArrayList<>();
        try {
            school = JSONHelper.schoolListFromName(schoolField.getText().toString(), 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String message = userPresenter.registerUser(this.firstName, this.lastName, this.username,
                this.mail, this.mailConfirmation, this.password, this.passwordConfirmation,
                school.get(0).getSchoolCode(),
                getContext());

        switch(message){
            case User.USER_SUCCESSFULLY_REGISTERED:
                Toast.makeText(getActivity(), "Usuário Cadastrado com Sucesso", Toast.LENGTH_LONG).show();
                break;
            case User.FIRST_NAME_CANT_BE_EMPTY_NAME:
                firstNameField.requestFocus();
                firstNameField.setError(message);
                break;
            case User.FIRST_NAME_CANT_BE_HIGHER_THAN_50:
                firstNameField.requestFocus();
                firstNameField.setError(message);
                break;
            case User.LAST_NAME_CANT_BE_EMPTY_NAME:
                lastNameField.requestFocus();
                lastNameField.setError(message);
                break;
            case User.LAST_NAME_CANT_BE_HIGHER_THAN_50:
                lastNameField.requestFocus();
                lastNameField.setError(message);
                break;
            case User.EMAIL_CANT_BE_EMPTY_EMAIL:
                mailField.requestFocus();
                mailField.setError(message);
                break;
            case User.EMAIL_CANT_BE_HIGHER_THAN_150:
                mailField.requestFocus();
                mailField.setError(message);
                break;
            case User.INVALID_EMAIL:
                mailField.requestFocus();
                mailField.setError(message);
                break;
            case User.EMAIL_ARE_NOT_EQUALS:
                mailField.requestFocus();
                mailField.setError(message);
                break;
            case User.USERNAME_CANT_BE_EMPTY_USERNAME:
                usernameField.requestFocus();
                usernameField.setError(message);
                break;
            case User.USERNAME_CANT_BE_HIGHER_THAN_100:
                usernameField.requestFocus();
                usernameField.setError(message);
                break;
            case User.PASSWORD_CANT_BE_EMPTY_PASSWORD:
                passwordField.requestFocus();
                passwordField.setError(message);
                break;
            case User.PASSWORD_CANT_BE_LESS_THAN_6:
                passwordField.requestFocus();
                passwordField.setError(message);
                break;
            case User.PASSWORD_ARE_NOT_EQUALS:
                passwordField.requestFocus();
                passwordField.setError(message);
                break;
            case User.USERNAME_EXISTENT:
                usernameField.requestFocus();
                usernameField.setError(message);
                break;
            case User.CONFIRM_PASSWORD_CANT_BE_EMPTY:
                passwordConfirmField.requestFocus();
                passwordConfirmField.setError(message);
                break;
            case User.EMAIL_CONFIRMATION_CANT_BE_EMPTY:
                mailConfirmationField.requestFocus();
                mailConfirmationField.setError(message);
                break;
            default:
                break;
        }
    }

    private void getTextTyped(){
        this.firstName = firstNameField.getText().toString();
        this.lastName = lastNameField.getText().toString();
        this.username = usernameField.getText().toString();
        this.mail = mailField.getText().toString();
        this.mailConfirmation = mailConfirmationField.getText().toString();
        this.password = passwordField.getText().toString();
        this.passwordConfirmation = passwordConfirmField.getText().toString();
        this.school = schoolField.getText().toString();
    }

}
