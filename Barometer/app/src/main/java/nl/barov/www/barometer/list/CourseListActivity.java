package nl.barov.www.barometer.list;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nl.barov.www.barometer.R;
import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;
import nl.barov.www.barometer.models.Course;

public class CourseListActivity extends AppCompatActivity {

    private ListView mListView;
    private CourseListAdapter mAdapter;
    private List<Course> courseModels = new ArrayList<>();    // NEED A METHOD TO FILL THIS. RETRIEVE THE DATA FROM JSON

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courselist);

        mListView = (ListView) findViewById(R.id.my_list_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                             @Override
                                             public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                                 Toast t = Toast.makeText(CourseListActivity.this,"Click" + position, Toast.LENGTH_LONG);
                                                 t.show();
                                             }
                                         }
        );

        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getApplicationContext());

        // Set the cursor (items fetcher)
        Cursor rsCourse = dbHelper.query(DatabaseInfo.CourseTables.COURSE, new String[]{"*"}, null, null, null, null,  DatabaseInfo.CourseColumn.PERIOD + " DESC");

        // Get the amount of return
        String array[] = new String[rsCourse.getCount()];
        int i = 0;

        rsCourse.moveToFirst();

        // For all the items we get in the return
        while (!rsCourse.isAfterLast()) {
            String name = rsCourse.getString(rsCourse.getColumnIndex("name"));
            String ects = rsCourse.getString(rsCourse.getColumnIndex("grade"));
            String grade = rsCourse.getString(rsCourse.getColumnIndex("grade"));
            String period = rsCourse.getString(rsCourse.getColumnIndex("period"));

            // Add to the listview
            courseModels.add(new Course(name, ects, grade, period));
            array[i] = rsCourse.getString(0);
            i++;
            rsCourse.moveToNext();
        }

        mAdapter = new CourseListAdapter(CourseListActivity.this, 0, courseModels);
        mListView.setAdapter(mAdapter);
    }
}

