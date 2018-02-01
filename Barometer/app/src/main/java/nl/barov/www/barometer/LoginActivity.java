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
    private String mNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //DEFINEN VAN EEN EDITTEXT EN BUTTON
        inputNaam = (EditText) findViewById(R.id.input_naam);
        Button submitButton = (Button) findViewById(R.id.submit_button);

        //UIT TE VOEREN CODE OP HET MOMENT DAT DE BUTTON WORDT AANGEKLIKT
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //OPHALEN VAN DE SHAREDPREF SIGN IN EN DEZE UPDATEN
                SharedPreferences sharedPrefSignin = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);
                SharedPreferences.Editor editorSignin = sharedPrefSignin.edit();
                editorSignin.putString(getString(R.string.sign_in), "ja");
                editorSignin.apply();

                mNaam = inputNaam.getText().toString();

                //OPHALEN VAN DE SHAREDPREF GEBRUIKERSNAAM EN DEZE UPDATEN
                SharedPreferences sharedPrefCode = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
                SharedPreferences.Editor editorSharedPrefCode = sharedPrefCode.edit();
                editorSharedPrefCode.putString(getString(R.string.gebruikers_naam), mNaam);
                editorSharedPrefCode.apply();

                //TONEN VAN EEN TOAST
                showToast(mNaam);

                //STARTEN VAN DE MAINACTIVITY
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void showToast(String mNaam) {
        //TONEN VAN EEN TOAST
        Toast.makeText(this, "Welkom " + mNaam , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //HUIDIGE INTENT AFSLUITEN OM TE VOORKOMEN DAT DEZE KAN WORDEN BENADERD VANUIT EEN ANDERE ACTIVITY
        finish();
    }
}

