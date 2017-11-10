package com.sachinchandil.firebasehandson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.sachinchandil.model.User;
import com.sachinchandil.utils.Initializer;
import com.sachinchandil.utils.ProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CloudDatabaseActivity extends AppCompatActivity implements Initializer {

    public static final String CONFIG_KEY_ENABLE_TITLE = "enable_title";
    private static final String TAG = CloudDatabaseActivity.class.getSimpleName();
    // -- Views
    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.spinnerGender)
    Spinner spinnerGender;
    @BindView(R.id.spinnerMaritalStatus)
    Spinner spinnerMaritalStatus;
    @BindView(R.id.textViewTitle)
    TextView textViewTitle;

    // -- Member variables
    private boolean mEditMode;
    private ArrayAdapter<String> mAdapterGender;
    private ArrayAdapter<String> mAdapterMaritalStatus;
    private DatabaseReference mDatabaseReference;
    private ValueEventListener mUserInfoChangeListener;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseRemoteConfig mRemoteConfig;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_database);
        ButterKnife.bind(this);

        initialize();
        setUpEvents();
    }


    @Override
    public void initialize() {
        mEditMode = false;
        mProgressDialog = new ProgressDialog(this);

        mFirebaseAuth = FirebaseAuth.getInstance();

        // get firebase DatabaseReference at ( "Users/{uid}" ) path, to save and retrieve data from/to firebase database.
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseAuth.getUid());

        mAdapterGender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrayGender));
        spinnerGender.setAdapter(mAdapterGender);

        mAdapterMaritalStatus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrayMaritalStatus));
        spinnerMaritalStatus.setAdapter(mAdapterMaritalStatus);
        resetViewState();

        // listener to watch changes in firebase database
        mUserInfoChangeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    setUserData(user);
                    Log.d(TAG, "Changes updated.");
                    Toast.makeText(CloudDatabaseActivity.this, "Data fetched.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(CloudDatabaseActivity.this, "No data fetched.", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Query Cancelled: " + databaseError.getMessage());
                Toast.makeText(CloudDatabaseActivity.this, "Data fetch cancelled!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        };

        mProgressDialog.show("Fetching user profile information...");
        // attaching ValueEventListener to be notified about changes in firebase database.
        // do not forget to remove "mUserInfoChangeListener" in onStop method.
        mDatabaseReference.addValueEventListener(mUserInfoChangeListener);

        setUpRemoteConfig();
    }

    /**
     * sets up remote config.
     */
    private void setUpRemoteConfig() {
        mRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set default values
        mRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        // create setting for development mode, which we can use to bypass expiration duration configured for final app release.
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mRemoteConfig.setConfigSettings(configSettings);

        fetchConfigValue();
    }

    /**
     * fetches config value
     */
    private void fetchConfigValue(){
        // default cache expiration time in seconds
        long cacheExpiration = 3600;

        // if developer mode, reset cache expiration time to 0 to bypass expiration duration.
        if(mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()){
            cacheExpiration = 0;
        }

        mRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Fetch successful.");
                mRemoteConfig.activateFetched();

            } else {
                Log.d(TAG, "Fetch failed!");
            }
            resetViewState();
        });
    }

    @Override
    public void setUpEvents() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if (mEditMode)
            inflater.inflate(R.menu.menu_save, menu);
        else
            inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemEdit:
                mEditMode = true;
                break;
            case R.id.itemSave:
                mEditMode = false;
                saveUserDataOnFirebaseDatabase();
                break;
            case R.id.itemCancel:
                mEditMode = false;
                break;
        }

        resetViewState();
        invalidateOptionsMenu();
        return true;
    }

    /**
     * saves user profile information on firebase database.
     */
    public void saveUserDataOnFirebaseDatabase() {
        User user = getUserData();
        mProgressDialog.show("Saving profile information...");

        // get FirebaseUser to access email id
        FirebaseUser userAcc = mFirebaseAuth.getCurrentUser();

        if (userAcc != null) {
            user.setEmailAddress(userAcc.getEmail());

            // save user info to firebase database, CompletionListener is passed to be notified about operation completion.
            mDatabaseReference.setValue(user, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    Log.d(TAG, "Data saved successfully.");
                    mProgressDialog.dismiss();
                } else {
                    Log.d(TAG, "Data saving failed!");
                    Toast.makeText(this, "Data saving failed!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private User getUserData() {
        User user = new User();
        user.setTitle("");
        user.setName(editTextName.getText().toString());
        user.setGender(mAdapterGender.getItem(spinnerGender.getSelectedItemPosition()));
        user.setMaritalStatus(mAdapterMaritalStatus.getItem(spinnerMaritalStatus.getSelectedItemPosition()));
        return user;
    }

    private void setUserData(User user) {
        textViewTitle.setText(user.getTitle());
        editTextName.setText(user.getName());
        spinnerGender.setSelection(mAdapterGender.getPosition(user.getGender()));
        spinnerMaritalStatus.setSelection(mAdapterMaritalStatus.getPosition(user.getMaritalStatus()));
    }

    /**
     * Reset views according to edit mode
     */
    private void resetViewState() {
        if (mEditMode) {
            editTextName.setEnabled(true);
            spinnerMaritalStatus.setEnabled(true);
            spinnerGender.setEnabled(true);
        } else {
            editTextName.setEnabled(false);
            spinnerMaritalStatus.setEnabled(false);
            spinnerGender.setEnabled(false);
        }

        if(mRemoteConfig != null) {
            // show title textView if `enable_title` is set to true on firebase console.
            textViewTitle.setVisibility(mRemoteConfig.getBoolean(CONFIG_KEY_ENABLE_TITLE) ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remove ValueChangeListener as we no longer need it after onStop
        // it also prevents memory leak.
        mDatabaseReference.removeEventListener(mUserInfoChangeListener);
    }
}
