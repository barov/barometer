package nl.barov.www.barometer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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

        //DEFINING THE EDITTEXT VOOR HET INVOEREN VAN JE NIEUWE GEBRUIKERSNAAM
        EditText editTextField = (EditText) findViewById(R.id.input_naam);

        //OPHALEN VAN DE SHAREDPREF VOOR DE GEBRUIKESNAAM
        final String gebruikersnaam = getSharedPrefUserName();

        //DE HUIDIGE GEBRUIKERSNAAM ALS HINT TOEVOEGEN AAN DE EDITTEXT
        editTextField.setHint(gebruikersnaam);

        //DEFINING THE BUTTON VOOR HET OPSLAAN VAN EEN NIEUWE GEBRUIKERSNAAM
        Button saveButton = (Button) findViewById(R.id.save_button);

        //OP HET MOMENT DAT ER GEKLIKT WORDT OP DE BUTTON
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DE EDITTEXT WORDT NOGMAAL GEDEFINEERD VANWEGE ISSUES
                EditText editText = (EditText) findViewById(R.id.input_naam);

                //DE INVOER WORDT GECONVERT NAAR EEN STRING
                String new_name = editText.getText().toString();

                //ER WORDT GEKEKEN NAAR DE INVOER VAN DIE STRING
                if(new_name.equals("")) {
                    //ALS DE INVOER LEEG IS, DAN WORDT ER EEN SNACKBAR GETOOND
                    showSnackbar(getString(R.string.nothing_added));

                } else if (new_name.equals(gebruikersnaam)) {
                    //ALS DE INVOER HETZELFDE IS AL DE HUIDIGE GEBRUIKERSNAAM, WORDT EEN SNACKBAR GETOOND
                    showSnackbar(getString(R.string.same_name));

                }
                else {
                    //UIT VOEREN ALS DE INVOER WEL VOLDOET
                    setSharedPrefUserName(new_name);

                    //TOAST LATEN ZIEN ALS DE NAAM IS GEWIJZIGD
                    showMessage(getString(R.string.changed_name) + " " + new_name + ".");

                    //DE ACTIVITY OPNIEUW STARTEN, ZODAT DE LIJST WORDT VERNIEUWD
                    restartActivity();
                }
            }
        });

        //DEFINING THE BUTTON SETDEFAULTS
        Button defaultButton = (Button) findViewById(R.id.setDefaults);

        //ONDERSTAANDE CODE UIT TE VOEREN ALS ER GEKLIKT WORDT OP DE BUTTON
        assert defaultButton != null;
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HET TERUGZETTEN VAN DE SHAREDPREF SIGN IN, ZODAT ER WEER INGELOGD MOET WORDEN
                settingBackSharedPrefSignin();

                //DE CIJFERS IN DE DATABASE WORDEN TERUGGEZET
                zetCijfersTerug();

                //HET OPHALEN VAN DE SHAREDPREF, ZODAT WE WETEN WELKE SPEC IS INGESTELD
                String spec = getSharedPrefSpec();

                //IIPXXXX TERUGZETTEN
                changeIIPXXXX("IIPXXXX", spec);

                //DE SHAREDPREF WORDT TERUGGEZET, ZODAT IIPXXXX WEER IS INGESTELD
                settingBackSharedPrefSpec();

                //TONEN VAN EEN TOAST MET FEEDBACK DAT HET IS GELUKT
                showMessage(getString(R.string.default_set_back));

                //APPLICATIE WORDT OPNIEUW OPGESTART
                restartApplication();
            }
        });

        //DEFINING ALLE (RADIO)BUTTONS OP DE SETTINGS PAGINA
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
                //DE BUTTON VAN DE HUIDIGE INGESTELDE SPEC UITZETTEN
                unSetSpec(specialisatie);

                int count = countCheckedRB();

                if(!(count > 1)) {

                    //DE SHAREDPREF OPHALEN VAN DE HUIDIG INGESTELDE SPEC
                    String spec = getSharedPrefSpec();

                    if (rBMT.isChecked()) {
                        //TONEN VAN EEN MESSAGE
                        showMessage(getString(R.string.spec_keuze) + " " + getString(R.string.mt));

                        //VERANDEREN VAN HUIDIGE IIPXXXX NAAR IIPMEDT IN DE DATABASE
                        changeIIPXXXX("IIPMEDT", spec);

                        //SHAREDPREF VOOR DE SPEC ZETTEN OP MT
                        setSharedPrefSpec("MT");

                        //OPNIEUW STARTEN VAN DE ACTIVITY
                        restartActivity();

                    } else if (rBSE.isChecked()) {
                        //TONEN VAN EEN MESSAGE
                        showMessage(getString(R.string.spec_keuze) + " " + getString(R.string.SE));

                        //VERANDEREN VAN HUIDIGE IIPXXXX NAAR IIPSE IN DE DATABSE
                        changeIIPXXXX("IIPSE", spec);

                        //SHAREDPREF VOOR DE SPEC ZETTEN OP SE
                        setSharedPrefSpec("SE");

                        //OPNIEUW STARTEN VAN DE ACTIVITY
                        restartActivity();

                    } else if (rBBDAM.isChecked()) {
                        //TONEN VAN EEN MESSAGE
                        showMessage(getString(R.string.spec_keuze) + " " + getString(R.string.BDAM));

                        //VERANDEREN DATABASE
                        changeIIPXXXX("IIPBDAM", spec);

                        //VERANDEREN VAN DE SHAREDPREF NAAR BDAM
                        setSharedPrefSpec("BDAM");

                        //OPNIEUW LADEN VAN DE ACTIVITY
                        restartActivity();

                    } else if (rBFICT.isChecked()) {
                        //TONEN VAN EEN MESSAGE
                        showMessage(getString(R.string.spec_keuze) + " " + getString(R.string.FICT));

                        //DATABASE AANPASSEN NAAM IIPXXXX
                        changeIIPXXXX("IIPFICT", spec);

                        //SHAREDPREF AANPASSEN NAAR FICT
                        setSharedPrefSpec("FICT");

                        //OPNIEUW STARTEN ACTIVITY
                        restartActivity();

                    } else {
                        //TONEN VAN EEN SNACKBAR
                        showSnackbar(getString(R.string.samen_input));

                        //HET UNCHECKEN VAN DE RADIO BUTTON WORDT ONGEDAAN GEMAAKT
                        setSpecChecked(spec);
                    }
                } else {
                    String message = getString(R.string.more_than_one);
                    showMessage(message);
                    restartActivity();
                }
            }
        });
    }

    private int countCheckedRB() {
        final RadioButton rBMT = (RadioButton) findViewById(R.id.radioButtonMT);
        final RadioButton rBSE = (RadioButton) findViewById(R.id.radioButtonSE);
        final RadioButton rBBDAM = (RadioButton) findViewById(R.id.radioButtonBDAM);
        final RadioButton rBFICT = (RadioButton) findViewById(R.id.radioButtonFICT);

        int count = 0;
        if(rBMT.isChecked()) {
            count++;
        }
        if(rBBDAM.isChecked()) {
            count++;
        }
        if(rBFICT.isChecked()) {
            count++;
        }
        if(rBSE.isChecked()) {
            count++;
        }
        return count;
    }

    @Override
    public void onBackPressed() {
        //STARTEN VAN DE MAINACTIVITY OP HET MOMENT DAT ER OP BACK WORDT GEDRUKT
        startActivity(new Intent(this, MainActivity.class));

        //HUIDIGE INTENT WORDT AFGESLOTEN
        finish();
    }

    private String getSharedPrefUserName() {
        //OPHALEN VAN DE SHAREDPREF VOOR DE GEBRUIKERSNAAM
        SharedPreferences sharedpref = getSharedPreferences(getString(R.string.gebruikers_naam), Context.MODE_PRIVATE);
        return sharedpref.getString(getString(R.string.gebruikers_naam), "");
    }

    private void setSharedPrefUserName(String new_name) {
        //OPHALEN EN UPDATEN VAN DE SHAREDPREF VOOR DE GEBRUIKERSNAAM
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.gebruikers_naam), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.gebruikers_naam), new_name);
        editor.apply();
    }

    private void showMessage(String message) {
        //HET TONEN VAN EEN TOAST MET EEN GEGEVEN STRING
        Toast t = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
        t.show();
    }

    private void showSnackbar(String message) {
        //COORDINATORLAYOUT VAN DE SETTINGS OPVRAGEN
        CoordinatorLayout settingsLayout = (CoordinatorLayout) findViewById(R.id.SettingsCoordinatorLayout);

        //TONEN VAN EEN SNACKBAR MET EEN GEGEVEN MESSAGE
        Snackbar snackbar = Snackbar.make(settingsLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void settingBackSharedPrefSignin() {
        //TERUGZETTEN VAN DE SHAREDPREF VOOR HET INLOGGEN NAAR "SIGN_IN"
        SharedPreferences sharedPrefSignin = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);
        SharedPreferences.Editor editorSignin = sharedPrefSignin.edit();
        editorSignin.putString(getString(R.string.sign_in), "nee");
        editorSignin.apply();
    }

    private void settingBackSharedPrefSpec() {
        //TERUGZETTEN VAN DE SHAREDPREF VOOR DE SPECIALISATIE
        SharedPreferences sharedPrefSpec = getSharedPreferences(getString(R.string.spec), MODE_PRIVATE);
        SharedPreferences.Editor editorSpec = sharedPrefSpec.edit();
        editorSpec.putString(getString(R.string.spec), "spec");
        editorSpec.apply();
    }

    private void restartApplication() {
        //HET OPNIEUW STARTEN VAN DE APPLICATIE DOOR DE SPLASHACTIVITY TE STARTEN
        Intent intent = new Intent(SettingsActivity.this, SplashScreenActivity.class);
        startActivity(intent);

        //HUIDIGE INTENT WORDT GEFINISHT OM TE VOORKOMEN DAT ER TERUG GEGAAN KAN WORDEN IN DE APPLICATIE
        finish();
    }

    private void zetCijfersTerug() {
        //FUNCTIE VOOR HET TERUGZETTEN VAN DE CIJFERS IN DE DATABASE
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());
        ContentValues newValues = new ContentValues();
        newValues.put("grade", "Voer een cijfer in");
        dbHelper.update(DatabaseInfo.CourseTables.COURSE, newValues, null, null);
    }

    private void restartActivity() {
        //FUNCTIE VOOR HET OPNIEUW OPSTARTEN VAN DE HUIDGE ACTIVITY
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
        //SHAREDPREF INSTELLEN VOOR DE NIEUW INGEVOERDE SPECIALISATIE
        SharedPreferences sharedPrefSpec = getSharedPreferences(getString(R.string.spec), MODE_PRIVATE);
        SharedPreferences.Editor editorSpec = sharedPrefSpec.edit();
        editorSpec.putString(getString(R.string.spec), specialisatie);
        editorSpec.apply();
    }

    private String getSharedPrefSpec() {
        //SHAREDPREF OPVRAGEN VOOR DE SPECIALISATIE
        SharedPreferences sharedpref = getSharedPreferences(getString(R.string.spec), Context.MODE_PRIVATE);
        return sharedpref.getString(getString(R.string.spec), "");
    }

    private void setSpecChecked(String specialisatie) {
        //OPVRAGEN VAN DE RADIOBUTTON OP DE XML
        RadioButton rBMT = (RadioButton) findViewById(R.id.radioButtonMT);
        RadioButton rBSE = (RadioButton) findViewById(R.id.radioButtonSE);
        RadioButton rBBDAM = (RadioButton) findViewById(R.id.radioButtonBDAM);
        RadioButton rBFICT = (RadioButton) findViewById(R.id.radioButtonFICT);

        //CONTROLEREN WELKE SPECIALISATIE IS INGEVOERD
        if(specialisatie.equals("MT")) {
            //ALS MEDIATECHNOLOGIE IS INGEVOERD, DAN WORDT DE RADIOBUTTON TRUE
            assert rBMT != null;
            rBMT.setChecked(true);

        } else if(specialisatie.equals("SE")) {
            //ALS SE IS INGEVOERD, DAN WORDT DE RADIOBUTTON TRUE
            assert rBSE != null;
            rBSE.setChecked(true);

        } else if(specialisatie.equals("FICT")) {
            //ALS FICT IS INGEVOERD, DAN WORDT DE RADIOBUTTON TRUE
            assert rBFICT != null;
            rBFICT.setChecked(true);

        } else if(specialisatie.equals("BDAM")) {
            //ALS BDAM IS INGEVOERD, DAN WORDT DE RADIOBUTTON TRUE
            assert rBBDAM != null;
            rBBDAM.setChecked(true);
        }
    }

    private void unSetSpec(String specialisatie) {
        //OPVRAGEN VAN DE RADIOBUTTON OP DE XML
        RadioButton rBMT = (RadioButton) findViewById(R.id.radioButtonMT);
        RadioButton rBSE = (RadioButton) findViewById(R.id.radioButtonSE);
        RadioButton rBBDAM = (RadioButton) findViewById(R.id.radioButtonBDAM);
        RadioButton rBFICT = (RadioButton) findViewById(R.id.radioButtonFICT);

        //CONTROLEREN WAT DE HUIDIGE SPECIALISATIE IS
        if(specialisatie.equals("MT")) {
            //ALS DE HUIDIGE SPEC MT IS, WORDT DEZE GEUNCHECKT
            assert rBMT != null;
            rBMT.setChecked(false);

        } else if(specialisatie.equals("SE")) {
            //ALS DE HUIDIGE SPEC SE IS, WORDT DEZE GEUNCHECKT
            assert rBSE != null;
            rBSE.setChecked(false);

        } else if(specialisatie.equals("FICT")) {
            //ALS DE HUIDIGE SPEC FICT IS, WORDT DEZE GEUNCHECKT
            assert rBFICT != null;
            rBFICT.setChecked(false);

        } else if(specialisatie.equals("BDAM")) {
            //ALS DE HUIDIGE SPEC BDAM IS, WORDT DEZE GEUNCHECKT
            assert rBBDAM != null;
            rBBDAM.setChecked(false);
        }
    }

    private void changeIIPXXXX(String iipxxxx, String specialisatie) {
        //OPVRAGEN VAN DE HUIDIGE WAARDE VOOR DE NAAM VAN IIPXXXX IN DE DATABASE
        String oldSub = getOldSub(specialisatie);

        //NIEUWE SUB VOOR IIPXXX IN DE DATABASE ZETTEN
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());
        ContentValues newValues = new ContentValues();
        newValues.put("name", iipxxxx);
        dbHelper.update(DatabaseInfo.CourseTables.COURSE, newValues, "name=?", new String[]{oldSub});
    }

    private String getOldSub(String specialisatie) {
        if(specialisatie.equals("MT")) {
            return "IIPMEDT";
        } else if(specialisatie.equals("SE")) {
            return "IIPSE";
        } else if(specialisatie.equals("FICT")) {
            return "IIPFICT";
        } else if(specialisatie.equals("BDAM")) {
            return "IIPBDAM";
        } else if (specialisatie.equals("spec")) {
            return "IIPXXXX";
        } else {
            return "IIPXXXX";
        }
    }
}
