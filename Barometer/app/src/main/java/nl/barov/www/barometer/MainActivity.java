package nl.barov.www.barometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;
import nl.barov.www.barometer.list.CourseListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfSignedOut();

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

        assert contentButton != null;
        contentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCourseListActivity();
            }
        });

        // PIECHARTACTIVITY
        Button overzichtButton = (Button) findViewById(R.id.overzicht);

        assert overzichtButton != null;
        overzichtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PieChartActivity.class));
            }
        });

        //AANTAL STUDIEPUNTEN TEXTVIEW
        TextView studiepunten = (TextView) findViewById(R.id.punten);

        //BEREKENEN AANTAL STUDIEPUNTEN
        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());

        // Set the cursor (items fetcher)
        Cursor rsCourse = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, "grade>=?", new String[]{"5.5"}, null, null, null);

        int count = 0;

        rsCourse.moveToFirst();

        // For all the items we get in the return
        while (!rsCourse.isAfterLast()) {
            int ects = rsCourse.getInt(rsCourse.getColumnIndex("ects"));
            count = count + ects;
            // Add to the listview
            rsCourse.moveToNext();
        }

        //SETTING THE STUDYPOINTSS
        studiepunten.setText(String.valueOf(count));
    }

    private void checkIfSignedOut() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sign_in), MODE_PRIVATE);

        // HAAL DE STATUS OP
        String key_check = getString(R.string.sign_in);
        String ingelogd = sharedPref.getString(getString(R.string.sign_in), key_check);
        if(ingelogd.equals("nee")) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
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
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
