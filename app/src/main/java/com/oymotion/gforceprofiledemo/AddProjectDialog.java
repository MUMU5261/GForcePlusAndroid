package com.oymotion.gforceprofiledemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 05/12/2021
 * Time: 02:32
 * Description:
 */
public class AddProjectDialog extends DialogFragment {
    private static final String TAG = "AddProjectDialog";

    Activity context;
    private EditText et_prj_name;
    private EditText et_prj_id;
    private EditText et_researcher;
    private TextView tv_prompt;

    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;

    public AddProjectDialog() {
        super();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    AddProjectDialog.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddProjectDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddProjectDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        try {
            dbHelper = new GForceDatabaseOpenHelper(context, "GForce.db", null, 1);
            db = dbHelper.getWritableDatabase();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_add_project, null);
        et_prj_id = (EditText)rootView.findViewById(R.id.et_prj_id);
        et_prj_name = (EditText)rootView.findViewById(R.id.et_prj_name);
        et_researcher = (EditText)rootView.findViewById(R.id.et_researcher);

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String prj_id_str = et_prj_id.getText().toString();
                        String prj_name_str = et_prj_name.getText().toString();
                        String researcher_str = et_researcher.getText().toString();
                        if(prj_id_str.isEmpty()||prj_name_str.isEmpty() || researcher_str.isEmpty()){
                            Toast.makeText(context, "Fields can not be empty.", Toast.LENGTH_LONG).show();
                        }else{
                            int prj_id = Integer.valueOf(prj_id_str);
                            if(Participant.isIDExist(db,Integer.valueOf(prj_id))){
                                Toast.makeText(context, "Project ID Exist.", Toast.LENGTH_LONG).show();
                                AddProjectDialog.this.getDialog().show();
                            }else{
                                Project project = new Project(prj_id,prj_name_str,researcher_str);
                                Log.i(TAG, "onClick: "+prj_id_str);
                                project.insertProject(db);
                                listener.onDialogPositiveClick(AddProjectDialog.this);
                                AddProjectDialog.this.getDialog().dismiss();
                            }
                        }

                        Log.i(TAG, "onClick: "+prj_id_str);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddProjectDialog.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().setCanceledOnTouchOutside(false);
    }
}
