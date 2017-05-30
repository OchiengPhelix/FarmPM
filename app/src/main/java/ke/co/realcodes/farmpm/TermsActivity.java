package ke.co.realcodes.farmpm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TermsActivity extends AppCompatActivity {

    private Bundle previousPage;
    private String backPage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_service);

        previousPage = getIntent().getExtras();
        backPage = previousPage.getString("backPage");
        Toast.makeText(TermsActivity.this, backPage, Toast.LENGTH_SHORT).show();
    }
}
