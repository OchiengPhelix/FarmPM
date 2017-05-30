package ke.co.realcodes.farmpm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private int sessionStatus = 0;
    private String randomId = null;
    private String userPhoneNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Thread splashThread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(3000);
                    startSessionActivity();
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        splashThread.start();
    }

    private void startSessionActivity(){
        if ((sessionStatus)==0){
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
        }
        else {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
        }
    }
}
