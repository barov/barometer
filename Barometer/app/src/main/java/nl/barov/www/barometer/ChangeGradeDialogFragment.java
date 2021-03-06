package nl.barov.www.barometer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import nl.barov.www.barometer.database.DatabaseHelper;
import nl.barov.www.barometer.database.DatabaseInfo;

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
                        CheckBox checkBoxVoldoende = (CheckBox) getDialog().findViewById(R.id.checkBoxVoldoende);
                        CheckBox checkBoxOnvoldoende = (CheckBox) getDialog().findViewById(R.id.checkBoxOnvoldoende);
                        EditText editText = (EditText) getDialog().findViewById(R.id.grade);
                        String new_grade = editText.getText().toString();
                        if(checkBoxVoldoende.isChecked() && checkBoxOnvoldoende.isChecked()) {
                            String ongeldig = getString(R.string.insert_valid_grade);
                            showMessage(ongeldig);
                        }
                        else if(checkBoxVoldoende.isChecked()) {
                            String voldoende = getString(R.string.voldoende_string);
                            showMessage(voldoende);
                            cijferInvoeren(getString(R.string.Voldoende_V));
                        }
                        else if(checkBoxOnvoldoende.isChecked() && new_grade.isEmpty()) {
                            String onvoldoende = getString(R.string.onvoldoende_string);
                            showMessage(onvoldoende);
                            cijferInvoeren(getString(R.string.Onvoldoende_O));
                        }
                        else {
                            if (new_grade.length() == 0) {
                                String message = getString(R.string.wrong_insertion);
                                showMessage(message);
                            } else if (new_grade.length() == 1) {
                                cijferInvoeren(new_grade);
                            } else if (new_grade.length() == 2) {
                                if (new_grade.equals("10")) {
                                    cijferInvoeren(new_grade);
                                } else {
                                    String message = getString(R.string.wrong_insertion);
                                    showMessage(message);
                                }
                            } else {
                                if (new_grade.indexOf(".") > 0) {
                                    cijferInvoeren(new_grade);
                                } else {
                                    String message = getString(R.string.wrong_insertion);
                                    showMessage(message);
                                }
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeGradeDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void showMessage(String message) {
        Toast t = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        t.show();
    }

    private void cijferInvoeren(String new_grade) {
        Bundle bundle = getArguments();
        String course = bundle.getString("name");

        DatabaseHelper dbHelper = DatabaseHelper.getHelper(getActivity());
        ContentValues newValues = new ContentValues();
        newValues.put("grade", new_grade);
        dbHelper.update(DatabaseInfo.CourseTables.COURSE, newValues, "name=?", new String[]{course});
        String message = "Je hebt het cijfer voor " + course + " veranderd in een " + new_grade;
        showMessage(message);
        restartActivity();
    }

    private void restartActivity() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();

                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
    }
}
