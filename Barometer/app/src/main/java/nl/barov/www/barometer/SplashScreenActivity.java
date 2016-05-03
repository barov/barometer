package nl.barov.www.barometer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        // ANIMATIE LOGO HSLEIDEN
        ImageView logo_hsleiden = (ImageView) findViewById(R.id.logo_hsleiden);
        Animation myFadeInAnimation_1500 = AnimationUtils.loadAnimation(this, R.anim.fadein_1500);
        logo_hsleiden.startAnimation(myFadeInAnimation_1500);

    }
}
