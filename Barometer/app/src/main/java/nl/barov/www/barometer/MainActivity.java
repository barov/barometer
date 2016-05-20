package nl.barov.www.barometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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

        //We will do the same for grades 10
        Cursor rsCourseTen = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, "grade=?", new String[]{"10"}, null, null, null);

        rsCourseTen.moveToFirst();

        while(!rsCourseTen.isAfterLast()) {
            int ects = rsCourseTen.getInt(rsCourseTen.getColumnIndex("ects"));
            count = count + ects;
            rsCourseTen.moveToNext();
        }

        //We want to substract grades with the default
        Cursor rsCourseDef = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, "grade=?", new String[]{"Voer een cijfer in"}, null, null, null);

        rsCourseDef.moveToFirst();

        while(!rsCourseDef.isAfterLast()) {
            int ects = rsCourseDef.getInt(rsCourseTen.getColumnIndex("ects"));
            count = count - ects;
            rsCourseDef.moveToNext();
        }
        //SETTING THE STUDYPOINTSS
        assert studiepunten != null;
        studiepunten.setText(String.valueOf(count));

        // ADVIES STUDIEPUNTEN HIER KOMT HET ADVIES VOOR DE STUDENT
        // CONTROLEER DE PERIODE EN STUDIEPUNTEN GEEF DAN ADVIES
        TextView advies = (TextView) findViewById(R.id.advies);

        // PERIODE 1
        if ((week >= 36) && ( week <= 46)){advies.setText("Deze periode kan je 13 studiepunten halen doe je best onderschat IARCH niet");}
        // PERIODE 2
        else if((week >= 47) && ( week <= 5) && count <= 12){advies.setText("Vorige periode heb je niet alles gehaald, je P halen we wel volgend jaar :)");}
        else if((week >= 47) && ( week <= 5) && count >= 13){advies.setText("Ga zo door!, Probeer deze periode ook alles te halen je bent een topper.");}
        // PERIODE 3
        else if((week >= 6) && ( week <= 16) && count <= 8){advies.setText("Neem contact op met je SLB, onvoldoende studiepunten");}
        else if((week >= 6) && ( week <= 16) && count >= 9 && count <= 18){advies.setText("Probeer de 40 punten te halen kom op je kan het!");}
        else if((week >= 6) && ( week <= 16) && count >= 19 && count <= 28){advies.setText("Probeer de 50 punten te halen kom op je kan het!");}
        else if((week >= 6) && ( week <= 16) && count >= 29){advies.setText("Je bent geweldig we gaan voor onze P!");}
        // PERIODE 4
        else if((week >= 17) && ( week <= 28) && count <= 22){advies.setText("Neem contact op met je SLB, onvoldoende studiepunten");}
        else if((week >= 17) && ( week <= 28) && count >= 23 && count <= 32){advies.setText("Probeer de 40 punten te halen kom op je kan het!");}
        else if((week >= 17) && ( week <= 28) && count >= 33 && count <= 42 ){advies.setText("Probeer de 50 punten te halen kom op je kan het!");}
        else if((week >= 17) && ( week <= 28) && count >= 43 && count <= 59){advies.setText("Je kan nog je P halen kom op!");}
        else if((week >= 17) && ( week <= 28) && count >= 60){advies.setText("Gefeliciteerd je P in 1 jaar je bent een topper!");}
        // ZOMER PERIODE
        else if((week >= 29) && ( week <= 35) && count <= 39){advies.setText("Zoek een andere school");}
        else if((week >= 29) && ( week <= 35) && count >= 40 && count <= 49){advies.setText("Veel succes in de Z klas");}
        else if((week >= 29) && ( week <= 35) && count >= 50 && count <= 59){advies.setText("Veel succes in het 2e jaar");}
        else if((week >= 29) && ( week <= 35) && count >= 60){advies.setText("Gefeliciteerd je P in 1 jaar je bent een topper geniet van je vakantie");}
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
        finish();
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
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
