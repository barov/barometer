package nl.barov.www.barometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText inputNaam;
    private Button submitButton;
    private String mNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        inputNaam = (EditText) findViewById(R.id.input_naam);
        submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefSignin = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);
                SharedPreferences.Editor editorSignin = sharedPrefSignin.edit();
                editorSignin.putString(getString(R.string.sign_in), "ja");
                editorSignin.commit();

                mNaam = inputNaam.getText().toString();

                SharedPreferences sharedPrefCode = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
                SharedPreferences.Editor editorSharedPrefCode = sharedPrefCode.edit();
                editorSharedPrefCode.putString(getString(R.string.gebruikers_naam), mNaam);
                editorSharedPrefCode.commit();

                showToast(mNaam);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void showToast(String mNaam) {
        Toast.makeText(this, "Welkom " + mNaam , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

