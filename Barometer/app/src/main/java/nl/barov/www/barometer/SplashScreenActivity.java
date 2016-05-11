package nl.barov.www.barometer;

import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.List;

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

    // ALLES WAT JE NODGI HEBT OM EEN REQUEST TE MAKEN
    private void requestSubjects() {
        Log.d("MIYVREY", "Private requestSubjects started");
        Type type = new TypeToken<List<Course>>() {
        }.getType();
        GsonRequest<List<Course>> request = new GsonRequest<List<Course>>(
                "http://fuujokan.nl/subject_lijst.json", type, null,
                new Response.Listener<List<Course>>() {
                    @Override
                    public void onResponse(List<Course> response) {
                        processRequestSucces(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                processRequestError(error);
            }
        }
        );
        Log.d("MIYVREY", "Private requestSubjects ended");
        VolleyHelper.getInstance(this).addToRequestQueue(request);
    }

    private void processRequestSucces(List<Course> subjects) {
        Log.d("MIYVREY", "try to return subjects");
        Log.d("MIYVREY", String.valueOf(subjects));

        // iterate via "for loop"
        System.out.println("==> For Loop Example.");
        for (int i = 0; i < subjects.size(); i++) {
            Log.d("MIYVREY-LIST", String.valueOf(subjects.get(i).getName()));
        }
    }

    private void processRequestError(VolleyError error) {
        Log.d("MIYVREY", "try to return errors");
    }
}
