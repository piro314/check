package com.piro.check.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.piro.check.R;
import com.piro.check.persistance.DBHelper;
import com.piro.check.persistance.Transaction;
import com.piro.check.persistance.User;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    DBHelper db;
    final Context context = this;

    private static DecimalFormat FORMATTER = new DecimalFormat("0.##");
    private static DateFormat DATEFORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        if(db == null) {
            db = new DBHelper(this);
        }

        final List<User> users = db.getAllUsers();
        final List<String> usersNames = new ArrayList<>() ;
        for (User user : users){
            usersNames.add(user.getName());
        }

        AutoCompleteTextView giverAutocomplete = (AutoCompleteTextView)findViewById(R.id.giverAutocomplete);
        giverAutocomplete.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, usersNames));

        MultiAutoCompleteTextView receiversAutocomplete= (MultiAutoCompleteTextView)findViewById(R.id.receiversAutocomplete);
        receiversAutocomplete.setAdapter(new ArrayAdapter<String>(this,  android.R.layout.simple_dropdown_item_1line, usersNames));
        receiversAutocomplete.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        final List<Transaction> transactions = db.getAllTransactions();

        ListView transactionsListView = (ListView)findViewById(R.id.transactionsList);
        final TransactionAdapter adapter = new TransactionAdapter(this,transactions);

        transactionsListView.setAdapter(adapter);

        transactionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_transaction);
                dialog.setTitle(R.string.transaction_dialog_title);


                TextView dialogGiver = (TextView)dialog.findViewById(R.id.dialog_giver);
                TextView dialogAmount = (TextView)dialog.findViewById(R.id.dialog_amount);
                TextView dialogReceivers = (TextView)dialog.findViewById(R.id.dialog_receivers);
                TextView dialogDescription = (TextView)dialog.findViewById(R.id.dialog_description);
                TextView dialogDate = (TextView)dialog.findViewById(R.id.dialog_date);


                final Transaction transaction = transactions.get(position);

                dialogGiver.setText(transaction.getGiverName());
                dialogGiver.setTextSize(20);
                dialogAmount.setText(FORMATTER.format(transaction.getAmount()));
                dialogAmount.setTextSize(20);
                dialogReceivers.setText(transaction.getReceiverNames());
                dialogReceivers.setTextSize(20);
                dialogDescription.setText(transaction.getDescription());
                dialogDescription.setTextSize(20);
                dialogDate.setText(DATEFORMAT.format(transaction.getDate()));
                dialogDate.setTextSize(20);

                Button closeButton = (Button) dialog.findViewById(R.id.dialog_button_close);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button deleteButton = (Button) dialog.findViewById(R.id.dialog_button_delete);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTransaction(transaction);
                        adapter.itemsArrayList.remove(position);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_transactions:
            {
                //we are here. do nothing
                return true;
            }
            case R.id.action_users:
            {
                Intent intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
                return true;
            }
            default:    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void createTransaction(View view){
        String giverText = ((EditText) findViewById(R.id.giverAutocomplete)).getText().toString().trim();
        String amountText = ((EditText) findViewById(R.id.amount)).getText().toString().trim();
        String receiversText = ((EditText) findViewById(R.id.receiversAutocomplete)).getText().toString().trim();
        String description = ((EditText) findViewById(R.id.description)).getText().toString().trim();

        if(!validateInput(giverText,amountText,receiversText,description)){
            return;
        }
        if(receiversText.charAt(receiversText.length()-1) == ','){
            //remove last comma
            receiversText = receiversText.substring(0,receiversText.length()-1);
        }



        List<String> nonExistingUsers = new ArrayList<>();
        if(db.findByUsername(giverText) == null){
            nonExistingUsers.add(giverText);
        }

        //split ignoring blanks
        List<String> receiverUsernames = Arrays.asList(receiversText.split("\\s*,\\s*"));

        for(String username : receiverUsernames){
            if(db.findByUsername(username) == null){
                nonExistingUsers.add(username);
            }
        }
        Double amount = Double.valueOf(amountText);
        StringBuilder receivers = new StringBuilder();
        int i = 0;
        for(String name : receiverUsernames){
            receivers.append(name);
            if(i++ < receiverUsernames.size() -1){
                receivers.append(",");
            }

        }
        if(nonExistingUsers.isEmpty()){
            doCreateTransaction(giverText, amount, receivers.toString(), description);
        }
        else {
            confirmNonExistingUsersCreation(nonExistingUsers, giverText, amount, receivers.toString(), description);
        }

    }

    private boolean validateInput(String giverText, String amountText, String receiversText, String description){

        if(giverText.isEmpty()){
            Toast.makeText(this, R.string.empty_giver, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(amountText.isEmpty()){
            Toast.makeText(this, R.string.empty_amount, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(receiversText.isEmpty()){
            Toast.makeText(this, R.string.empty_receivers, Toast.LENGTH_SHORT).show();
            return false;
        }
        if(description.isEmpty()){
            Toast.makeText(this, R.string.empty_description, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean confirmNonExistingUsersCreation(List<String> nonexisting, final String giver, final Double amount, final String receivers, final String description){
        boolean confirmed = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Resources res = getResources();
        String nonexistingMsg = String.format(res.getString(R.string.users_for_create), new HashSet<String>(nonexisting).toString());
        builder.setMessage(nonexistingMsg)
                .setPositiveButton(R.string.dialog_confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        doCreateTransaction(giver, amount, receivers, description);
                }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothing
                    }
                });

        builder.create().show();
        return confirmed;
    }

    private void doCreateTransaction( final String giver, final Double amount, final String receivers, final String description){
        db.makeTransaction(giver, amount, receivers, description);
        Intent intent = new Intent(this, TransactionsActivity.class);
        finish();
        startActivity(intent);
    }

    public void deleteAll(View view) {
        Intent intent = new Intent(this, TransactionsActivity.class);
        db.deleteAll();
        finish();
        startActivity(intent);
    }

    private class TransactionAdapter extends ArrayAdapter<Transaction> {

        private final Context context;
        private final List<Transaction> itemsArrayList;

        public TransactionAdapter(Context context, List<Transaction> itemsArrayList) {

            super(context, R.layout.row_transaction, itemsArrayList);

            this.context = context;
            this.itemsArrayList = itemsArrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.row_transaction , parent, false);

            // 3. Get the two text view from the rowView
            TextView giverView = (TextView) rowView.findViewById(R.id.giverText);
            TextView descriptionView = (TextView) rowView.findViewById(R.id.descriptionText);
            TextView amountView = (TextView) rowView.findViewById(R.id.amountText);

            // 4. Set the text for textView
            giverView.setText(itemsArrayList.get(position).getGiverName());
            giverView.setTextSize(20);

            descriptionView.setText(itemsArrayList.get(position).getDescription());
            descriptionView.setTextSize(20);

            amountView.setText(FORMATTER.format(itemsArrayList.get(position).getAmount()));
            amountView.setTextSize(20);

            // 5. retrn rowView
            return rowView;
        }

    }

    private void deleteTransaction(Transaction t){
        db.deleteTransaction(t);
    }

}
