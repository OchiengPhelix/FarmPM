package ke.co.realcodes.farmpm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import ke.co.realcodes.farmpm.rest.RegisterApi;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private String userPhoneNumber = null;
    private String randomId = null;
    private String verificationCode = null;
    private String firstName;
    private String lastName;
    private String userName;
    private String userLocation;
    private String profilePicture;
    private String profilePictureName;
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber();
            }
        });
    }

    private void phoneNumber() {
        final EditText phone = (EditText) findViewById(R.id.phone);
        String rawPhone = phone.getText().toString();

        if (rawPhone.length() == 10 || rawPhone.length() == 13) {
            userPhoneNumber = "+254" + rawPhone.substring(rawPhone.length() - 9);
            new MaterialDialog.Builder(this)
                    .content("A verification code will be sent to:\n" + userPhoneNumber + "\nIs it OK, or would you like to edit it?")
                    .contentColor(Color.BLACK)
                    .positiveText("Ok")
                    .negativeText("Edit")
                    .positiveColorRes(R.color.colorPrimary)
                    .negativeColorRes(R.color.colorPrimary)
                    .buttonsGravity(GravityEnum.CENTER)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            confirmNetworkCondition();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            phone.requestFocus();
                            phone.setFocusableInTouchMode(true);
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(phone, InputMethodManager.SHOW_IMPLICIT);
                        }
                    })
                    .show();
        } else {
            new MaterialDialog.Builder(this)
                    .content("Wrong phone number, please confirm your number")
                    .contentColor(Color.BLACK)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.colorPrimary)
                    .buttonsGravity(GravityEnum.CENTER)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            phone.requestFocus();
                            phone.setFocusableInTouchMode(true);
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(phone, InputMethodManager.SHOW_IMPLICIT);
                        }
                    })
                    .show();

        }
    }

    private void confirmNetworkCondition() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            sendVerificationCode();
        } else {
            new MaterialDialog.Builder(this)
                    .content("No internet access. Please turn on data or Wi-Fi in Settings")
                    .contentColor(Color.BLACK)
                    .positiveText("Settings")
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
                            //start settings view
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    })
                    .show();
        }
    }


    private void sendVerificationCode() {
        new AsyncTask<String, String, UserResponse>() {
            @Override
            protected void onPreExecute() {
                progressDialog();
                super.onPreExecute();
            }

            @Override
            protected UserResponse doInBackground(String... params) {
                try {
                    Response response = ApiClient.getClient().create(RegisterApi.class).registerUser(userPhoneNumber).execute();
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
                    Toast.makeText(LoginActivity.this, response.message, Toast.LENGTH_LONG).show();
                    randomId = response.random_id;
                    verificationCode = response.verification_code;
                    firstName = response.fname;
                    lastName = response.lname;
                    userName = response.username;
                    userLocation = response.location;
                    profilePicture = response.profile_picture;
                    profilePictureName = response.profile_picture_name;
                    nextActivity();
                }else{
                    verificationNotSent();
                }
            }
        }.execute();
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
                        phoneNumber();
                    }
                })
                .show();
    }

    private void exitApp() {
        //close all activities and terminate app
        ActivityCompat.finishAffinity(this);
    }

    private void nextActivity(){
        Intent verify = new Intent(this, VerifyActivity.class);
        Bundle verifyActivity = new Bundle();
        verifyActivity.putString("userPhoneNumber", userPhoneNumber);
        verifyActivity.putString("randomId", randomId);
        verifyActivity.putString("verificationCode", verificationCode);
        verifyActivity.putString("firstName", firstName);
        verifyActivity.putString("lastName", lastName);
        verifyActivity.putString("userLocation", userLocation);
        verifyActivity.putString("userName", userName);
        verifyActivity.putString("profilePicture", profilePicture);
        verifyActivity.putString("profilePictureName", profilePictureName);
        verify.putExtras(verifyActivity);
        startActivity(verify);
    }

    public void progressDialog(){
        dialog = new MaterialDialog.Builder(this)
                .content("Connecting...")
                .contentColor(Color.BLACK)
                .progress(true, 0)
                .widgetColorRes(R.color.colorPrimary)
                .show();
    }
}
