package nl.barov.www.barometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import nl.barov.www.barometer.list.CourseListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button contentButton = (Button) findViewById(R.id.content_button);

        // HAAL SHAREDPREFENCES OP EN SET NAAM
        TextView showText = (TextView) findViewById(R.id.gebruiker);
        SharedPreferences sharedpref = getSharedPreferences(getString(R.string.gebruikers_naam), Context.MODE_PRIVATE);
        String name = sharedpref.getString(getString(R.string.gebruikers_naam), "");
        showText.setText(name);

        // TOON PERIODE & JAAR
        TextView periode = (TextView) findViewById(R.id.periode);
        TextView jaar = (TextView) findViewById(R.id.schooljaar);

        Calendar cal = Calendar.getInstance();
        int week =  cal.get(Calendar.WEEK_OF_YEAR);
        int year =  cal.get(Calendar.YEAR);

        if ((week >= 36) && ( week <= 46)){periode.setText("Periode 1");}
        else if((week >= 47) && ( week <= 5)){periode.setText("Periode 2");}
        else if((week >= 6) && ( week <= 16)){periode.setText("Periode 3");}
        else if((week >= 17) && ( week <= 28)){periode.setText("Periode 4");}
        else if((week >= 29) && ( week <= 35)){periode.setText("Zomervakantie");}

        if((week >= 36 && (week <= 53))){jaar.setText(String.valueOf(year) + " / " +String.valueOf(year + 1));}
        else if ((week >= 1 && (week <= 36 ))){jaar.setText(String.valueOf(year - 1) + " / " +String.valueOf(year));}

        contentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCourseListActivity();
            }
        });
    }

    private void launchCourseListActivity() {
        Intent intent = new Intent(MainActivity.this, CourseListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, InstellingenActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
