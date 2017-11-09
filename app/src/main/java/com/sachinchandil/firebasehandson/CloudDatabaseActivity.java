package com.sachinchandil.firebasehandson;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.sachinchandil.utils.Initializer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CloudDatabaseActivity extends AppCompatActivity implements Initializer {

    // -- Views
    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.spinnerGender)
    Spinner spinnerGender;
    @BindView(R.id.spinnerMaritalStatus)
    Spinner spinnerMaritalStatus;

    // -- Member variables
    private boolean editMode;
    private ArrayAdapter<String> mAdapterGender;
    private ArrayAdapter<String> mAdapterMaritalStatus;


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
        editMode = false;

        mAdapterGender = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrayGender));
        spinnerGender.setAdapter(mAdapterGender);

        mAdapterMaritalStatus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.arrayMaritalStatus));
        spinnerMaritalStatus.setAdapter(mAdapterMaritalStatus);
        resetViewState();
    }

    @Override
    public void setUpEvents() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if(editMode)
            inflater.inflate(R.menu.menu_save, menu);
        else
            inflater.inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemEdit:
                editMode = true;
                break;
            case R.id.itemSave:
                editMode = false;
                break;
            case R.id.itemCancel:
                editMode = false;
                break;
        }
        resetViewState();
        invalidateOptionsMenu();
        return true;
    }

    private void resetViewState(){
        if(editMode){
            editTextName.setEnabled(true);
            spinnerMaritalStatus.setEnabled(true);
            spinnerGender.setEnabled(true);
        }else{
            editTextName.setEnabled(false);
            spinnerMaritalStatus.setEnabled(false);
            spinnerGender.setEnabled(false);

        }
    }
}
