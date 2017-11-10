package com.sachinchandil.firebasehandson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sachinchandil.utils.Initializer;
import com.sachinchandil.utils.ProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseLoginActivity extends AppCompatActivity implements Initializer, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_GOOGLE_SIGN_IN = 1;
    private final String TAG = FirebaseLoginActivity.class.getSimpleName();
    // -- Views
    @BindView(R.id.button_sign_in)
    SignInButton buttonSignIn;
    @BindView(R.id.button_sign_out)
    Button buttonSignOut;
    @BindView(R.id.tv_welcome_message)
    TextView tvWelcomeMessage;
    @BindView(R.id.button_firebase_cloud_database)
    Button buttonCloudDatabase;
    // -- Member variables
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);
        ButterKnife.bind(this);

        initialize();
        setUpEvents();
    }

    @Override
    public void initialize() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Google sign in configuration steps
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //TODO("pass `default_web_client_id` in requestIdToken method")
                .requestIdToken("")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();

    }

    @Override
    public void setUpEvents() {
        buttonSignIn.setOnClickListener(view -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        });

        buttonSignOut.setOnClickListener(view -> {
            mFirebaseAuth.signOut();
            updateUI(null);
        });

        buttonCloudDatabase.setOnClickListener(view -> {
            Intent intent = new Intent(this, CloudDatabaseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already logged in and update UI accordingly.
        // mFirebaseAuth.getCurrentUser() method returns null, if user is not signed in.
        //TODO("pass FirebaseUser in updateUI method, Hint: use `mFirebaseAuth.getCurrentUser()`")
        updateUI(null);
    }


    /**
     * updates UI according to login status.
     * @param user if not null -> user is already signed in.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Toast.makeText(FirebaseLoginActivity.this, "Welcome " + user.getDisplayName(),
                    Toast.LENGTH_SHORT).show();
            buttonSignOut.setVisibility(View.VISIBLE);
            buttonSignIn.setVisibility(View.GONE);
            tvWelcomeMessage.setText(String.format("Welcome %s", user.getDisplayName()));
            buttonCloudDatabase.setVisibility(View.VISIBLE);
        } else {
            buttonSignIn.setVisibility(View.VISIBLE);
            buttonSignOut.setVisibility(View.GONE);
            tvWelcomeMessage.setText(R.string.do_login_message);
            buttonCloudDatabase.setVisibility(View.GONE);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.getStatus());
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogleCredential(account);
        } else {
            Toast.makeText(FirebaseLoginActivity.this, "Google signin failed!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * uses Google credentials to login into firebase.
     * @param account
     */
    private void firebaseAuthWithGoogleCredential(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        // get credential
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        progressDialog.show("Signing you in ...");

        //TODO("pass AuthCredential in place of null in following line")
        mFirebaseAuth.signInWithCredential(null)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FirebaseLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(FirebaseLoginActivity.this, "Google signin connection failed!",
                Toast.LENGTH_SHORT).show();
    }


}
