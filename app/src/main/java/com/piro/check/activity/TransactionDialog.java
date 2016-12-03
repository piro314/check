package com.piro.check.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.piro.check.R;
import com.piro.check.persistance.DBHelper;
import com.piro.check.persistance.Transaction;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by piro on 18.10.2016 г..
 */

public class TransactionDialog extends DialogFragment {


    private Transaction transaction;

    private static DecimalFormat FORMATTER = new DecimalFormat("0.##");
    private static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        transaction = (Transaction)savedInstanceState.get("transaction");

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_transaction, null);

        TextView dialogGiver = (TextView)dialogView.findViewById(R.id.dialog_giver);
        TextView dialogAmount = (TextView)dialogView.findViewById(R.id.dialog_amount);
        TextView dialogReceivers = (TextView)dialogView.findViewById(R.id.dialog_receivers);
        TextView dialogDescription = (TextView)dialogView.findViewById(R.id.dialog_description);
        TextView dialogDate = (TextView)dialogView.findViewById(R.id.dialog_date);

        dialogGiver.setText(transaction.getGiverName());
        dialogAmount.setText(FORMATTER.format(transaction.getAmount()));
        dialogReceivers.setText(transaction.getReceiverNames());
        dialogDescription.setText(transaction.getDescription());
        dialogDate.setText(DATEFORMAT.format(transaction.getDate()));

        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.button_delete_transaction, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TransactionDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }
}