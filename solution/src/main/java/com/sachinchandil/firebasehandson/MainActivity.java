package com.sachinchandil.firebasehandson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.buttonFirebaseLogin)
    Button buttonFirebaseLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        buttonFirebaseLogin.setOnClickListener( v -> {
            Intent intent = new Intent(this, FirebaseLoginActivity.class);
            startActivity(intent);
        });
    }
}
