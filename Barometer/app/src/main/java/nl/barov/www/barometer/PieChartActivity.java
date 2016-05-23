package nl.barov.www.barometer;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.Calendar;

import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;

public class PieChartActivity extends AppCompatActivity {
    private PieChart mChart;
    public static final int MAX_ECTS = 60;
    public static int currentEcts = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        //  Log.d("tot hier", "en niet verder");

        mChart = (PieChart) findViewById(R.id.chart);
        mChart.setDescription("");
        mChart.setTouchEnabled(false);
        mChart.setDrawSliceText(true);
        mChart.getLegend().setEnabled(false);
        mChart.setTransparentCircleColor(Color.rgb(130, 130, 130));
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        final DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());

        // Set the cursor (items fetcher)
        Cursor rsCourse = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, "grade>=?", new String[]{"5.5"}, null, null, null);

        int count = 0;

        rsCourse.moveToFirst();

        // For all the items we get in the return
        while (!rsCourse.isAfterLast()) {
            int ects = rsCourse.getInt(rsCourse.getColumnIndex("ects"));
            count = count + ects;
            // Add to the total
            rsCourse.moveToNext();
        }

        //We will do the same for grades 10
        Cursor rsCourseTen = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, "grade=?", new String[]{"10"}, null, null, null);

        rsCourseTen.moveToFirst();

        while(!rsCourseTen.isAfterLast()) {
            int ects = rsCourseTen.getInt(rsCourseTen.getColumnIndex("ects"));
            count = count + ects;
            //Add to the total
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

        Cursor rsCourseOnvoldoende = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, "grade=?", new String[]{"O"}, null, null, null);

        rsCourseOnvoldoende.moveToFirst();

        while(!rsCourseOnvoldoende.isAfterLast()) {
            int ects = rsCourseOnvoldoende.getInt(rsCourseTen.getColumnIndex("ects"));
            count = count - ects;
            rsCourseOnvoldoende.moveToNext();
        }

        setData(count);


        Calendar cal = Calendar.getInstance();
        int week =  cal.get(Calendar.WEEK_OF_YEAR);
        int year =  cal.get(Calendar.YEAR);

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

    private void setData(int aantal) {
        currentEcts = aantal;
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();

        yValues.add(new Entry(aantal, 0));
        xValues.add("Behaalde ECTS");

        yValues.add(new Entry(60 - currentEcts, 1));
        xValues.add("Resterende ECTS");

        //  http://www.materialui.co/colors
        ArrayList<Integer> colors = new ArrayList<>();
        if (currentEcts <10) {
            colors.add(Color.rgb(255,0,0));
        } else if (currentEcts < 40){
            colors.add(Color.rgb(255,150,150));
        } else if  (currentEcts < 50) {
            colors.add(Color.rgb(10,150,10));
        } else {
            colors.add(Color.rgb(10,150,10));
        }

        colors.add(Color.rgb(40,188,185));

        PieDataSet dataSet = new PieDataSet(yValues, "ECTS");
        //  dataSet.setDrawValues(false); //schrijf ook de getallen weg.
        dataSet.setColors(colors);

        PieData data = new PieData(xValues, dataSet);
        mChart.setData(data);        // bind je dataset aan de chart.
        mChart.invalidate();        // Aanroepen van een volledige redraw
        Log.d("aantal =", ""+currentEcts);
    }
}

