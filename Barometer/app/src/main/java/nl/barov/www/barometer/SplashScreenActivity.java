package nl.barov.www.barometer;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    private static int SPLASH_SCREEN_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FULLSCREEN SPLASHSCREEN ALTIJD VOOR SETCONTENTVIEW
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        //Request the courses
        requestSubjects();

        // ANIMATIE LOGO HSLEIDEN
        ImageView logo_hsleiden = (ImageView) findViewById(R.id.logo_hsleiden);
        Animation myFadeInAnimation_1500 = AnimationUtils.loadAnimation(this, R.anim.fadein_1500);
        logo_hsleiden.startAnimation(myFadeInAnimation_1500);

        // CONTROLEER ALS DE GEBRUIKER INGELOGD IS
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // HAAL LOGIN SHAREDPREFENCES OP
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);

                // HAAL DE STATUS OP
                String key_check = getString(R.string.sign_in);
                String ingelogd = sharedPref.getString(getString(R.string.sign_in), key_check);
                Log.d("Ingelogd", ingelogd);

                // CHECK ALS GEBRUIKER INGELOGD IS
                if (ingelogd.equals("ja")) {
                    // UITVOEREN ALS INGELOGD
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // UITVOEREN ALS DE GEBRUIKER NIET INGELOGD IS
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, SPLASH_SCREEN_TIME);
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

        // Get the amount of return
        String array[] = new String[rsCourse.getCount()];
        int j = 0;

        rsCourse.moveToFirst();

        // For all the items we get in the return
        while (!rsCourse.isAfterLast()) {
            String name       = rsCourse.getString(rsCourse.getColumnIndex("name"));
            listedCourseArray.add(name);
            j++;
            rsCourse.moveToNext();
        }

        rsCourse.moveToFirst();
        /* END FETCHER FOR ALL ITEMS */

        // iterate via "for loop"
        for (int i = 0; i < subjects.size(); i++) {

            String name = String.valueOf(subjects.get(i).getName());
            String ects = String.valueOf(subjects.get(i).getEcts());
            String grade = String.valueOf(subjects.get(i).getGrade());
            String period = String.valueOf(subjects.get(i).getPeriod());


            // Now check if this id already exists in the array
            if (listedCourseArray.contains(name)) {
                // true
            } else {

                // Set values to insert into the database
                ContentValues values = new ContentValues();
                values.put(DatabaseInfo.CourseColumn.NAME, name);
                values.put(DatabaseInfo.CourseColumn.PERIOD, period);
                values.put(DatabaseInfo.CourseColumn.ECTS, ects);
                values.put(DatabaseInfo.CourseColumn.GRADE, grade);

                // The insert itself by the dbhelper
                dbHelper.insert(DatabaseInfo.CourseTables.COURSE, null, values);

                // Add the id to the array so we won't add it twice
                listedCourseArray.add(name);
                Log.d("MULAMETHOD-NEWS", listedCourseArray.toString());
            }
        }
    }

    private void processRequestsError(VolleyError error){
        Log.d("MULAMETHOD", "News JSON request failed. Internet Connection Aviable? Webserver Aviable?");
    }
}
