package nl.barov.www.barometer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText editTextField = (EditText) findViewById(R.id.input_naam);
        final String gebruikersnaam = getSharedPrefUserName();
        editTextField.setHint(gebruikersnaam);

        Button saveButton = (Button) findViewById(R.id.save_button);

        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.input_naam);
                String new_name = editText.getText().toString();
                if(new_name.equals("")) {
                    showMessage("U heeft niets ingevuld");
                } else if (new_name.equals(gebruikersnaam)) {
                    showMessage("U heeft dezelfde gebruikersnaam ingevuld.");
                }
                else {
                    setSharedPrefUserName(new_name);
                    showMessage("U gebruikersnaam is veranderd naar " + new_name + ".");
                    restartActivity();
                }
            }
        });

        Button defaultButton = (Button) findViewById(R.id.setDefaults);

        assert defaultButton != null;
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingBackSharedPref();
                zetCijfersTerug();
                showMessage("U heeft de instellingen teruggezet naar de dafaults.");
                restartApplication();
            }
        });

        final RadioButton rBMT = (RadioButton) findViewById(R.id.radioButtonMT);
        final RadioButton rBSE = (RadioButton) findViewById(R.id.radioButtonSE);
        final RadioButton rBBDAM = (RadioButton) findViewById(R.id.radioButtonBDAM);
        final RadioButton rBFICT = (RadioButton) findViewById(R.id.radioButtonFICT);
        Button saveSpecButton = (Button) findViewById(R.id.save_spec_button);

        final String specialisatie = getSharedPrefSpec();
        setSpecChecked(specialisatie);

        assert saveSpecButton != null;
        saveSpecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unSetSpec(specialisatie);

                if(rBMT.isChecked()) {
                    showMessage("MT");
                    setSharedPrefSpec("MT");
                    restartActivity();
                } else if (rBSE.isChecked()) {
                    showMessage("SE");
                    setSharedPrefSpec("SE");
                    restartActivity();
                } else if (rBBDAM.isChecked()) {
                    showMessage("BDAM");
                    setSharedPrefSpec("BDAM");
                    restartActivity();
                } else if (rBFICT.isChecked()) {
                    showMessage("FICT");
                    setSharedPrefSpec("FICT");
                    restartActivity();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private String getSharedPrefUserName() {
        SharedPreferences sharedpref = getSharedPreferences(getString(R.string.gebruikers_naam), Context.MODE_PRIVATE);
        return sharedpref.getString(getString(R.string.gebruikers_naam), "");
    }

    private void setSharedPrefUserName(String new_name) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.gebruikers_naam), new_name);
        editor.commit();
    }

    private void showMessage(String message) {
        Toast t = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        t.show();
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

    private void zetCijfersTerug() {
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());
        ContentValues newValues = new ContentValues();
        newValues.put("grade", "Voer een cijfer in");
        dbHelper.update(DatabaseInfo.CourseTables.COURSE, newValues, null, null);
    }

    private void restartActivity() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0, 0);
                finish();

                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
    }

    private void setSharedPrefSpec(String specialisatie) {
        SharedPreferences sharedPrefSpec = getSharedPreferences(getString(R.string.spec), MODE_PRIVATE);
        SharedPreferences.Editor editorSpec = sharedPrefSpec.edit();
        editorSpec.putString(getString(R.string.spec), specialisatie);
        editorSpec.commit();
    }

    private String getSharedPrefSpec() {
        SharedPreferences sharedpref = getSharedPreferences(getString(R.string.spec), Context.MODE_PRIVATE);
        return sharedpref.getString(getString(R.string.spec), "");
    }

    private void setSpecChecked(String specialisatie) {
        RadioButton rBMT = (RadioButton) findViewById(R.id.radioButtonMT);
        RadioButton rBSE = (RadioButton) findViewById(R.id.radioButtonSE);
        RadioButton rBBDAM = (RadioButton) findViewById(R.id.radioButtonBDAM);
        RadioButton rBFICT = (RadioButton) findViewById(R.id.radioButtonFICT);

        if(specialisatie.equals("MT")) {
            assert rBMT != null;
            rBMT.setChecked(true);
        } else if(specialisatie.equals("SE")) {
            assert rBSE != null;
            rBSE.setChecked(true);
        } else if(specialisatie.equals("FICT")) {
            assert rBFICT != null;
            rBFICT.setChecked(true);
        } else if(specialisatie.equals("BDAM")) {
            assert rBBDAM != null;
            rBBDAM.setChecked(true);
        }
    }

    private void unSetSpec(String specialisatie) {
        RadioButton rBMT = (RadioButton) findViewById(R.id.radioButtonMT);
        RadioButton rBSE = (RadioButton) findViewById(R.id.radioButtonSE);
        RadioButton rBBDAM = (RadioButton) findViewById(R.id.radioButtonBDAM);
        RadioButton rBFICT = (RadioButton) findViewById(R.id.radioButtonFICT);

        if(specialisatie.equals("MT")) {
            assert rBMT != null;
            rBMT.setChecked(false);
        } else if(specialisatie.equals("SE")) {
            assert rBSE != null;
            rBSE.setChecked(false);
        } else if(specialisatie.equals("FICT")) {
            assert rBFICT != null;
            rBFICT.setChecked(false);
        } else if(specialisatie.equals("BDAM")) {
            assert rBBDAM != null;
            rBBDAM.setChecked(false);
        }
    }
}
