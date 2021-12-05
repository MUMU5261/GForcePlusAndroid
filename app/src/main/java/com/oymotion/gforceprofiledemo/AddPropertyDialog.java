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
 * Date: 02/12/2021
 * Time: 21:30
 * Description:
 */
public class AddPropertyDialog extends DialogFragment{
    private static final String TAG = "AddPropertyDialog";

    Activity context;

    //    @BindView(R.id.et_participant_id)
    private TextView tv_prompt;
    private EditText et_property;
    private EditText et_polar_low;
    private EditText et_polar_high;


    GForceDatabaseOpenHelper dbHelper;
    SQLiteDatabase db;

    int prj_id = -1;

    public AddPropertyDialog() {
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
    AddPropertyDialog.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddParticipantDialog
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddPropertyDialog.NoticeDialogListener) context;
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
        View rootView = inflater.inflate(R.layout.dialog_add_property, null);

        et_property = (EditText)rootView.findViewById(R.id.et_property);
        et_polar_low = (EditText)rootView.findViewById(R.id.et_polar_low);
        et_polar_high = (EditText)rootView.findViewById(R.id.et_polar_high);
        tv_prompt = (TextView) rootView.findViewById(R.id.tv_prompt);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout\

        builder.setView(rootView)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        String property_name = et_property.getText().toString();
                        String polarLow = et_polar_low.getText().toString();
                        String polarHigh = et_polar_high.getText().toString();
                        if (property_name.isEmpty() || polarLow.isEmpty() || polarHigh.isEmpty()) {
                                Toast.makeText(context, "Fields can't be empty", Toast.LENGTH_LONG).show();
                            }else{
                                Property property = new Property(prj_id,property_name,polarLow,polarHigh);
                                int id_new = property.insertProperty(db);
                                Log.i(TAG, "onClick: "+ id_new);
                                listener.onDialogPositiveClick(AddPropertyDialog.this);
                                AddPropertyDialog.this.getDialog().dismiss();
                            }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogNegativeClick(AddPropertyDialog.this);
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
