package nl.barov.www.barometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText inputNaam;
    private TextInputLayout inputLayoutNaam;
    private Button submitButton;
    private String mNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputNaam = (EditText) findViewById(R.id.input_naam);
        submitButton = (Button) findViewById(R.id.submit_button);

        // Set Groepcode if possible (Nearly not, but hey!)
        //Get sharedpreferences
        SharedPreferences sharedPrefCode = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
        mNaam = sharedPrefCode.getString(getString(R.string.gebruikers_naam), "naam");

        if (mNaam != "") {
            inputNaam.setText(mNaam);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
        String key_check = getResources().getString(R.string.gebruikers_naam);
        String code_check = sharedPref.getString(getString(R.string.gebruikers_naam), key_check);
        Log.d("Gebruikersnaam_Pref", code_check);

        SharedPreferences sharedPrefSignin = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);
        SharedPreferences.Editor editorSignin = sharedPrefSignin.edit();
        editorSignin.putString(getString(R.string.sign_in), "ja");
        editorSignin.commit();

        //Launching the next activity to login with Google.
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

