package nl.barov.www.barometer;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;
import nl.barov.www.barometer.gson.GsonRequest;
import nl.barov.www.barometer.gson.VolleyHelper;
import nl.barov.www.barometer.models.Course;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FULLSCREEN SPLASHSCREEN ALTIJD VOOR SETCONTENTVIEW
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

       //OPVRAGEN VAN DE SHAREDPREF OM TE KIJKEN OF ER IS INGELOGD
        final String ingelogd = getSharedPref();

        //KIJKEN OF ER IS INGELOGD
        if(!ingelogd.equals("ja")) {
            //ALS ER NOG NIET IS INGELOGD WORDT DE JSON GELADEN (IVM HET WIJZIGEN VAN DE DATABASE MBT DE SPECIALISATIES
            //HIERMEE WORDT VOORKOMEN DAT IIPXXXX TERUGKEERT IN DE DATABASE ALS DEZE IS GEWIJZIGD
            requestSubjects();
        }

        // ANIMATIE LOGO HSLEIDEN
        ImageView logo_hsleiden = (ImageView) findViewById(R.id.logo_hsleiden);
        Animation myFadeInAnimation_1500 = AnimationUtils.loadAnimation(this, R.anim.fadein_1500);
        assert logo_hsleiden != null;
        logo_hsleiden.startAnimation(myFadeInAnimation_1500);

        // CONTROLEER ALS DE GEBRUIKER INGELOGD IS
        int SPLASH_SCREEN_TIME = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // CHECK ALS GEBRUIKER INGELOGD IS
                if (ingelogd.equals("ja")) {
                    // UITVOEREN ALS INGELOGD --> DAN GA JE DIRECT DOOR NAAR DE MAIN
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // UITVOEREN ALS DE GEBRUIKER NIET INGELOGD IS --> DAN GA JE NAAR HET INLOGSCHERM
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                //FINISHEN ZODAT DE SPALSHSCREEN INTENT WORDT BEEINDIGD EN JE DEZE NIET MEER TE ZIEN KRIJGT ALS VANUIT EEN VOLGENDE INTENT OP BACK KLIKT.
                finish();
            }
        }, SPLASH_SCREEN_TIME);
    }

    @Override
    public void onBackPressed()
    {
        //KILLED DE HELE APPLICATIE ALS ER OP BACK WORDT GEKLIKT
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void requestSubjects(){

        Type type = new TypeToken<List<Course>>(){}.getType();
        GsonRequest<List<Course>> request = new GsonRequest<List<Course>>(
                "http://fuujokan.nl/subject_lijst.json", type, null,
                new Response.Listener<List<Course>>() {
                    @Override
                    public void onResponse(List<Course> response) {
                        processRequestsSucces(response);
                    }
                }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){ processRequestsError(error);     }
        }
        );

        VolleyHelper.getInstance(this).addToRequestQueue(request);
    }

    private void processRequestsSucces(List<Course> subjects ){
        // First, get the already existing if so
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(this);

        // Set a list array for checking wether we added this news id already or not
        List<String> listedCourseArray = new ArrayList<String>();

        /* FETCHER FOR ALL ITEMS */
        // Set the cursor (items fetcher)
        Cursor rsCourse = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, null, null, null, null, null);

        rsCourse.moveToFirst();

        // For all the items we get in the return
        while (!rsCourse.isAfterLast()) {
            String name       = rsCourse.getString(rsCourse.getColumnIndex("name"));
            listedCourseArray.add(name);
            rsCourse.moveToNext();
        }

        rsCourse.moveToFirst();
        /* END FETCHER FOR ALL ITEMS */

        // iterate via "for loop"
        for (int i = 0; i < subjects.size(); i++) {
            //DE NAAM, ECTS, CIJFER EN DE PERIODE  PER COURSE UIT DE DATABASE OPHALEN
            String name = String.valueOf(subjects.get(i).getName());
            String ects = String.valueOf(subjects.get(i).getEcts());
            String grade = "Voer een cijfer in";
            String period = String.valueOf(subjects.get(i).getPeriod());


            // Now check if this id already exists in the array
            if (listedCourseArray.contains(name)) {
                // true
            } else {

                // Set values to insert into the database
                ContentValues values = new ContentValues();
                values.put(DatabaseInfo.CourseColumn.NAME, name);
                values.put(DatabaseInfo.CourseColumn.PERIOD, period);
                values.put(DatabaseInfo.CourseColumn.GRADE, grade);
                values.put(DatabaseInfo.CourseColumn.ECTS, ects);

                // The insert itself by the dbhelper
                dbHelper.insert(DatabaseInfo.CourseTables.COURSE, null, values);
            }
        }
    }

    private String getSharedPref() {
        // HAAL LOGIN SHAREDPREFENCES OP
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);

        //EEN KEY NEERZETTEN DIE ALS DEFAULT FUNGEERT
        String key_check = getString(R.string.sign_in);

        //DE WAARDE VAN DE SHAREDPREF OPHALEN
        return sharedPref.getString(getString(R.string.sign_in), key_check);
    }

    private void processRequestsError(VolleyError error){
        //DEFIENEREN VAN EEN STRING VOOR DE MESSAGE
        String errorString = "De vakken konden niet ingeladen worden.";

        //VINDEN VAN DE LAYOUT VOOR DE SNACKBAR
        CoordinatorLayout coLayout = (CoordinatorLayout)findViewById(R.id.splashCoordinatorLayout);

        //OPROEPEN VAN DE SNACKBAR OP HET MOMENT VAN EEN FOUTMELDING
        showSnackbar(errorString, coLayout);
    }

    private void showSnackbar(String errorString, View layout) {
        //Maakt een snackbar aan op het moment dat het inladen van de JSON fout gaat
        Snackbar snackbar = Snackbar.make(layout, errorString, Snackbar.LENGTH_LONG);

        //Snackbar wordt getoond
        snackbar.show();
    }
}
