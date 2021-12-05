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

import butterknife.BindView;

/**
 * Created by Android Studio.
 * User: lilil
 * Date: 01/12/2021
 * Time: 02:39
 * Description:
 */
public class AddParticipantDialog extends DialogFragment {
    private static final String TAG = "AddParticipantDialog";

    Activity context;

//    @BindView(R.id.et_participant_id)
    private EditText et_p_id;
    private TextView tv_prompt;

    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;

    public AddParticipantDialog() {
        super();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int p_id);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddParticipantDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
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
        View rootView = inflater.inflate(R.layout.dialog_add_participant, null);
        et_p_id = (EditText)rootView.findViewById(R.id.et_participant_id);
        tv_prompt = (TextView)rootView.findViewById(R.id.tv_prompt);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout\

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String id_str = et_p_id.getText().toString();
                        if (!id_str.isEmpty()) {
                            int p_id = Integer.valueOf(id_str);
                            if(Participant.isIDExist(db,p_id)){
                                Toast.makeText(context, "Participant ID Exist.", Toast.LENGTH_LONG).show();
                                AddParticipantDialog.this.getDialog().show();
                            }else{
                                Participant participant = new Participant(p_id,0);
                                Log.i(TAG, "onClick: "+p_id);
                                participant.insertParticipant(db);
                                listener.onDialogPositiveClick(AddParticipantDialog.this, p_id);
                                AddParticipantDialog.this.getDialog().dismiss();
                            }
                        } else {
                            Toast.makeText(context, "Please enter Participant ID with correct format.", Toast.LENGTH_LONG).show();
                            AddParticipantDialog.this.getDialog().show();
                        }
                        Log.i(TAG, "onClick: "+id_str);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddParticipantDialog.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
//        getDialog().setCanceledOnTouchOutside(false);
    }
}