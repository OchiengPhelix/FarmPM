package ke.co.realcodes.farmpm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import ke.co.realcodes.farmpm.model.UserResponse;
import ke.co.realcodes.farmpm.rest.ApiClient;
import ke.co.realcodes.farmpm.rest.ProfileInfo;
import ke.co.realcodes.farmpm.rest.RegisterApi;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private FloatingActionButton camera;
    private Bundle userData;
    private String randomId = null;
    private String firstName = null;
    private String lastName = null;
    private String userName = null;
    private String profilePicture = null;
    private String profilePictureName = null;
    private String userLocation = null;
    private Button finish;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText usernameInput;
    private EditText locationInput;
    private MaterialDialog dialog;
    private String newFirstNameInput = null;
    private String newLastNameInput = null;
    private String newUserNameInput = null;
    private String newUserLocationInput = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userData = getIntent().getExtras();
        randomId = this.userData.getString("randomId");
        firstName = this.userData.getString("firstName");
        lastName = this.userData.getString("lastName");
        userName = this.userData.getString("userName");
        profilePicture = this.userData.getString("profilePicture");
        profilePictureName = this.userData.getString("profilePictureName");
        userLocation = this.userData.getString("userLocation");

        firstNameInput = (EditText) findViewById(R.id.fname_profile_input);
        lastNameInput = (EditText) findViewById(R.id.lname_profile_input);
        usernameInput = (EditText) findViewById(R.id.username_profile_input);
        locationInput = (EditText) findViewById(R.id.location_profile_input);

        if (firstName != null){
            firstNameInput.setText(firstName);
        }
        if (lastName != null){
            lastNameInput.setText(lastName);
        }
        if (userName != null){
            usernameInput.setText(userName);
        }
        if (userLocation != null){
            locationInput.setText(userLocation);
        }
        newFirstNameInput = firstNameInput.getText().toString();
        newLastNameInput = lastNameInput.getText().toString();
        newUserNameInput = usernameInput.getText().toString();
        newUserLocationInput = locationInput.getText().toString();
        camera = (FloatingActionButton) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfilePicture();
            }
        });
        finish = (Button) findViewById(R.id.finish_button);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser(){
        new AsyncTask<String, String, UserResponse>() {
            @Override
            protected void onPreExecute() {
                progressDialog();
                super.onPreExecute();
            }

            @Override
            protected UserResponse doInBackground(String... params) {
                try {
                    Response response = ApiClient.getClient().create(ProfileInfo.class).insertUserInformation(randomId, newFirstNameInput, newLastNameInput, newUserNameInput, newUserLocationInput).execute();
                    Log.d("RESP::", response.code() + ": " + response.message());
                    return (UserResponse) response.body();
                } catch (Exception e) {
                    return new UserResponse(false, e.getMessage());
                }
            }

            @Override
            protected void onPostExecute(UserResponse response) {
                dialog.hide();
                super.onPostExecute(response);
                if(response.success == true && response.message != "Error" && response.random_id != null && response.verification_code != null){
                    Toast.makeText(ProfileActivity.this, response.message, Toast.LENGTH_LONG).show();
                    randomId = response.random_id;
                    nextActivity();
                }else{
                    verificationNotSent();
                }
            }
        }.execute();
    }

    public void progressDialog(){
        dialog = new MaterialDialog.Builder(this)
                .content("Connecting...")
                .contentColor(Color.BLACK)
                .progress(true, 0)
                .widgetColorRes(R.color.colorPrimary)
                .show();
    }

    private void nextActivity(){
        Intent home = new Intent(this, VerifyActivity.class);
        Bundle homeActivity = new Bundle();
        homeActivity.putString("randomId", randomId);
        home.putExtras(homeActivity);
        startActivity(home);
    }

    private void verificationNotSent(){
        new MaterialDialog.Builder(this)
                .content("Verification code not sent, please try again")
                .contentColor(Color.BLACK)
                .positiveText("Try again")
                .negativeText("Exit")
                .positiveColorRes(R.color.colorPrimary)
                .negativeColorRes(R.color.colorPrimary)
                .buttonsGravity(GravityEnum.CENTER)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //exit the app and return to where the app was launched from
                        exitApp();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //try sending code again
                        usernameInput.requestFocus();
                        usernameInput.setFocusableInTouchMode(true);
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(usernameInput, InputMethodManager.SHOW_IMPLICIT);
                    }
                })
                .show();
    }

    private void exitApp() {
        //close all activities and terminate app
        ActivityCompat.finishAffinity(this);
    }

    public void getProfilePicture(){
        Toast.makeText(ProfileActivity.this, "Profile picture upload here", Toast.LENGTH_SHORT).show();
    }
}
