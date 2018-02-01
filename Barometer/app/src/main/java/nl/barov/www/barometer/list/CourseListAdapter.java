package nl.barov.www.barometer.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import nl.barov.www.barometer.R;
import nl.barov.www.barometer.models.Course;

public class CourseListAdapter extends ArrayAdapter<Course> {

    CourseListAdapter(Context context, int resource, List<Course> objects){
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null ) {
            vh = new ViewHolder();
            LayoutInflater li = LayoutInflater.from(getContext());
            convertView = li.inflate(R.layout.view_content_row, parent, false);
            vh.name = (TextView) convertView.findViewById(R.id.subject_name);
            vh.code = (TextView) convertView.findViewById(R.id.subject_code);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Course cm = getItem(position);
        if (cm != null) {
            vh.name.setText(cm.name);
            vh.code.setText(cm.grade);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView code;
    }
}

