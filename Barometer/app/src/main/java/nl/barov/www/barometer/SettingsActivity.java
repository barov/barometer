package nl.barov.www.barometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button defaultButton = (Button) findViewById(R.id.setDefaults);

        assert defaultButton != null;
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingBackSharedPref();
                //deletingDatabase();
                restartApplication();
            }
        });
    }

    private void settingBackSharedPref() {
        SharedPreferences sharedPrefSignin = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);
        SharedPreferences.Editor editorSignin = sharedPrefSignin.edit();
        editorSignin.putString(getString(R.string.sign_in), "nee");
        editorSignin.commit();
    }

    private void restartApplication() {
        Intent intent = new Intent(SettingsActivity.this, SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }

}
