package ke.co.realcodes.farmpm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private String backPage = "welcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        Button terms = (Button) findViewById(R.id.terms_button);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTerms();
            }
        });

        TextView termsLink = (TextView) findViewById(R.id.terms_statement);
        termsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View terms) {
                openTerms();
            }
        });
    }

    private void acceptTerms() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }

    private void openTerms() {
        Intent termsOfService = new Intent(this, TermsActivity.class);
        Bundle returnView = new Bundle();
        returnView.putString("backPage", backPage);
        termsOfService.putExtras(returnView);
        startActivity(termsOfService);
    }
}
