package ke.co.realcodes.farmpm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import ke.co.realcodes.farmpm.model.UserResponse;
import ke.co.realcodes.farmpm.rest.ApiClient;
import ke.co.realcodes.farmpm.rest.ResendCodeApi;
import retrofit2.Response;

public class VerifyActivity extends AppCompatActivity {

    private long finishTime;
    private Bundle loginData;
    private String onlineCode = null;
    private String userPhoneNumber = null;
    private String randomId = null;
    private TextView phoneNumberText;
    private TextView resendCodeCounter;
    private TextView resendCodeText;
    private EditText verificationCodeInput;
    private String verificationCodeString;
    private Button verifyButton;
    private TextView wrongNumber;
    private MaterialDialog dialog;
    private String firstName;
    private String lastName;
    private String userName;
    private String userLocation;
    private String profilePicture;
    private String profilePictureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify);
        downCounter();
        loginData = getIntent().getExtras();
        userPhoneNumber = loginData.getString("userPhoneNumber");
        onlineCode = loginData.getString("verificationCode");
        randomId = loginData.getString("randomId");
        firstName = loginData.getString("firstName");
        lastName = loginData.getString("lastName");
        userName = loginData.getString("userName");
        userLocation = loginData.getString("userLocation");
        profilePicture = loginData.getString("profilePicture");
        profilePictureName = loginData.getString("profilePictureName");

        phoneNumberText = (TextView) findViewById(R.id.verification_phone_number_text);
        phoneNumberText.setText("Please enter the code sent to " + userPhoneNumber+".");
        verificationCodeInput = (EditText) findViewById(R.id.verification_code);
        wrongNumber = (TextView) findViewById(R.id.wrong_number);
        wrongNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongNumberUser();
            }
        });
        verifyButton = (Button) findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });
        resendCodeText = (TextView) findViewById(R.id.resend_text);
        resendCodeCounter = (TextView) findViewById(R.id.resend_countdown);
        resendCodeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finishTime > 0){
                    Toast.makeText(VerifyActivity.this, "Please wait, resend code in "+finishTime+" seconds", Toast.LENGTH_SHORT).show();
                }else {
                    resendVerificationCode();
                    downCounter();
                }
            }
        });
        resendCodeCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finishTime > 0){
                    Toast.makeText(VerifyActivity.this, "Please wait, resend code in "+finishTime+" seconds", Toast.LENGTH_SHORT).show();
                }else {
                    resendVerificationCode();
                    downCounter();
                }
            }
        });
    }

    public void wrongNumberUser() {
        onBackPressed();
        finish();
    }

    public void downCounter(){
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                resendCodeCounter.setText(" " + millisUntilFinished / 1000+" seconds");
                finishTime = millisUntilFinished / 1000;
            }
            public void onFinish() {
                resendCodeCounter.setText("");
                resendCodeText.setText("Resend code");
                finishTime = 0;
            }
        }.start();
    }

    public void verifyCode() {
        verificationCodeString = verificationCodeInput.getText().toString();
        if (verificationCodeString.isEmpty() || verificationCodeString.length() != 6) {
            new MaterialDialog.Builder(this)
                    .content("Please enter a valid verification code")
                    .contentColor(Color.BLACK)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.colorPrimary)
                    .buttonsGravity(GravityEnum.CENTER)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            verificationCodeInput.requestFocus();
                            verificationCodeInput.setFocusableInTouchMode(true);
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(verificationCodeInput, InputMethodManager.SHOW_IMPLICIT);
                        }
                    })
                    .show();
        } else if (verificationCodeString.equals(onlineCode)) {
            nextActivity();
        } else {
            new MaterialDialog.Builder(this)
                    .content("Invalid verification code, try again")
                    .contentColor(Color.BLACK)
                    .positiveText("Try again")
                    .positiveColorRes(R.color.colorPrimary)
                    .buttonsGravity(GravityEnum.CENTER)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            verificationCodeInput.requestFocus();
                            verificationCodeInput.setFocusableInTouchMode(true);
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.showSoftInput(verificationCodeInput, InputMethodManager.SHOW_IMPLICIT);
                        }
                    })
                    .show();
        }
    }

    public void resendVerificationCode(){
        new AsyncTask<String, String, UserResponse>() {
            @Override
            protected void onPreExecute() {
                progressDialog();
                super.onPreExecute();
            }

            @Override
            protected UserResponse doInBackground(String... params) {
                try {
                    Response response = ApiClient.getClient().create(ResendCodeApi.class).resendCode(userPhoneNumber, randomId).execute();
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
                    onlineCode = response.verification_code;
                    Toast.makeText(VerifyActivity.this, response.message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(VerifyActivity.this, "Verification code not sent, please try again", Toast.LENGTH_LONG).show();
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

    public void nextActivity(){
        if (userName == "new" && firstName == "new" && lastName == "new" && userLocation == "new"){
            Intent profile = new Intent(this, ProfileActivity.class);
            Bundle verifyActivity = new Bundle();
            verifyActivity.putString("randomId", randomId);
            verifyActivity.putString("firstName", firstName);
            verifyActivity.putString("lastName", lastName);
            verifyActivity.putString("username", userName);
            verifyActivity.putString("location", userLocation);
            verifyActivity.putString("profilePicture", profilePicture);
            verifyActivity.putString("profilePictureName", profilePictureName);
            profile.putExtras(verifyActivity);
            startActivity(profile);
        }else {
            Intent home = new Intent(this, HomeActivity.class);
            Bundle homeActivity = new Bundle();
            homeActivity.putString("randomId", randomId);
            home.putExtras(homeActivity);
            startActivity(home);
        }
    }
}
