package nl.barov.www.barometer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class InstellingenActivity extends AppCompatActivity {

    private String newNaam;
    private EditText inputNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instellingen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inputNaam = (EditText) findViewById(R.id.input_naam);
        Button save = (Button) findViewById(R.id.save_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newNaam = inputNaam.getText().toString();
                SharedPreferences sharedPrefCode = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
                SharedPreferences.Editor editorSharedPrefCode = sharedPrefCode.edit();
                editorSharedPrefCode.putString(getString(R.string.gebruikers_naam), newNaam);
                editorSharedPrefCode.commit();
                Intent i = new Intent(InstellingenActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
