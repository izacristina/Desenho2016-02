package com.mathheals.meajuda.view.users;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mathheals.meajuda.R;
import com.mathheals.meajuda.model.User;
import com.mathheals.meajuda.presenter.UserPresenter;

public class ViewProfile extends Fragment {

    private User currentUser;
    private SharedPreferences session;

    public ViewProfile(User user){
        this.currentUser = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_user_show,
                container, false);

        session = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());

        showUserOnProfile(view, currentUser);

        return view;
    }

    public void showUserOnProfile(View view, User user){
        TextView userName = (TextView) view.findViewById(R.id.userName);
        userName.setText(user.getFirstName()+" "+user.getLastName());

        TextView userLogin = (TextView) view.findViewById(R.id.userLogin);
        userLogin.setText(user.getUsername());

        TextView userRating = (TextView) view.findViewById(R.id.userRating);
        userRating.setText(String.valueOf(user.getRating()));
    }

    @Override
    public void onDestroy(){
        if(this.getArguments().getBoolean("comeFromSearch")){
            getActivity().finish();
        }

        super.onDestroy();
    }
}