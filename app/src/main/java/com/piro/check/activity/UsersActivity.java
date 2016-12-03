package com.piro.check.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.piro.check.R;
import com.piro.check.persistance.DBHelper;
import com.piro.check.persistance.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersActivity extends AppCompatActivity {


    DBHelper db;
    private static DecimalFormat FORMATTER = new DecimalFormat("0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(db == null) {
            db = new DBHelper(this);
        }

        final List<User> users = db.getAllUsers();
        final List<String> usersNames = new ArrayList<>() ;
        for (User user : users){
            usersNames.add(user.getName());
        }


        ListView usersListView = (ListView)findViewById(R.id.usersList);
        final MyAdapter adapter = new MyAdapter(this,users);

        usersListView.setAdapter(adapter);
//        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//                final String item = (String) parent.getItemAtPosition(position);
//                view.animate().setDuration(2000).alpha(0)
//                        .withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                usersNames.remove(item);
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
//                                db.deleteUser(findByUsername(users, item));
//                            }
//                        });
//            }
//
//        });

    }

    private class MyAdapter extends ArrayAdapter<User> {

        private final Context context;
        private final List<User> itemsArrayList;

        public MyAdapter(Context context, List<User> itemsArrayList) {

            super(context, R.layout.row, itemsArrayList);

            this.context = context;
            Collections.sort(itemsArrayList);
            this.itemsArrayList = itemsArrayList ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // 1. Create inflater
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 2. Get rowView from inflater
            View rowView = inflater.inflate(R.layout.row , parent, false);

            // 3. Get the two text view from the rowView
            TextView usernameView = (TextView) rowView.findViewById(R.id.username);
            TextView balanceView = (TextView) rowView.findViewById(R.id.balance);
            Button deleteButton = (Button)rowView.findViewById(R.id.deleteButton);

            // 4. Set the text for textView
            usernameView.setText(itemsArrayList.get(position).getName());
            usernameView.setTextSize(20);

            Double balance = itemsArrayList.get(position).getBalance();
            if(position == itemsArrayList.size()-1){
                balance = balance + calculateSumZeroAddition(itemsArrayList);
            }
            if(balance < 0) {
                balanceView.setTextColor(Color.RED);

                balanceView.setText(FORMATTER.format((-1)*balance));
            }
            else{
                balanceView.setTextColor(Color.GREEN);
                balanceView.setText(FORMATTER.format(balance));
            }
            balanceView.setTextSize(20);

            if(Math.abs(itemsArrayList.get(position).getBalance()) >= 0.01){
                deleteButton.setVisibility(View.INVISIBLE);

                ViewGroup.LayoutParams params=balanceView.getLayoutParams();
                params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
                balanceView.setLayoutParams(params);
            }
            deleteButton.setTag(position);
            deleteButton.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Integer index = (Integer) view.getTag();
                    deleteUser(itemsArrayList.get(index.intValue()));
                    itemsArrayList.remove(index.intValue());

                    notifyDataSetChanged();
                }
            });
            // 5. retrn rowView
            return rowView;
        }

    }

    private double calculateSumZeroAddition(List<User> users){
        double sum = 0;
        double roundedSum = 0;
        for (User user : users) {
            sum += user.getBalance();
            roundedSum += Double.valueOf(FORMATTER.format(user.getBalance()));
        }

        return sum - roundedSum;
    }

    public void addUser(View view) {
        EditText usernameText = (EditText) findViewById(R.id.new_user);
        String username = usernameText.getText().toString().trim();
        if(username.isEmpty()){
            Toast.makeText(this, R.string.empty_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if(username.contains(",")){
            Toast.makeText(this, R.string.user_exists, Toast.LENGTH_SHORT).show();
            return;
        }
        User existing = db.findByUsername(username);
        if(existing != null){
            Toast.makeText(this, R.string.user_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, UsersActivity.class);
        User user = new User(username, new Double(0d));
        db.addUser(user);
        finish();
        startActivity(intent);

    }

    private void deleteUser(User user){
        db.deleteUser(user);
    }

    private static User findByUsername(List<User> users,String  username){
        for (User user: users) {
            if(user.getName().equals(username)){
                return user;
            }
        }
        throw new IllegalStateException("no such user");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_transactions:
            {
                Intent intent = new Intent(this, TransactionsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_users:
            {
                //we are here. do nothing
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


}
