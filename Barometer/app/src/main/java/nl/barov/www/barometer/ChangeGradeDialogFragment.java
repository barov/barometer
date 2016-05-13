package nl.barov.www.barometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;

/**
 * Created by Patrick on 13/05/16.
 */
public class ChangeGradeDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.cijfer_message);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_dialog_change_grade, null))
                // Add action buttons
                .setPositiveButton(R.string.oke, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = (EditText) getDialog().findViewById(R.id.grade);
                        String new_grade =  editText.getText().toString();

                        Bundle bundle = getArguments();
                        String course = bundle.getString("name");

                        Log.d("naam", course);

                        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getActivity());
                        ContentValues newValues = new ContentValues();
                        newValues.put("grade", new_grade);
                        dbHelper.update(DatabaseInfo.CourseTables.COURSE, newValues, "name=?", new String[]{course});
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeGradeDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
