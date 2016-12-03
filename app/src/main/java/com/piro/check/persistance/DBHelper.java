package com.piro.check.persistance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by piro on 10.10.2016 г..
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Check.db";

    private static final String TEXT_TYPE = " TEXT ";
    private static final String DOUBLE_TYPE = " DOUBLE ";
    private static final String INTEGER_TYPE = " INTEGER ";

    private static class User_Table implements BaseColumns {
        public static final String TABLE_NAME = "users";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BALANCE = "balance";
    }

    private static class Transactions_Table implements BaseColumns {
        public static final String TABLE_NAME = "transactions";

        public static final String COLUMN_GIVER = "giver";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_RECEIVERS = "receivers";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE = "date";


    }

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + User_Table.TABLE_NAME + " (" +
                    User_Table._ID + " INTEGER PRIMARY KEY," +
                    User_Table.COLUMN_NAME + TEXT_TYPE + "," +
                    User_Table.COLUMN_BALANCE + DOUBLE_TYPE + " )";

    private static final String SQL_CREATE_TRANSACTIONS =
            "CREATE TABLE " + Transactions_Table.TABLE_NAME + " (" +
                    User_Table._ID + " INTEGER PRIMARY KEY," +
                    Transactions_Table.COLUMN_GIVER + TEXT_TYPE + "," +
                    Transactions_Table.COLUMN_AMOUNT + DOUBLE_TYPE + "," +
                    Transactions_Table.COLUMN_RECEIVERS + TEXT_TYPE + "," +
                    Transactions_Table.COLUMN_DESCRIPTION + TEXT_TYPE + "," +
                    Transactions_Table.COLUMN_DATE + INTEGER_TYPE + " )";

    private static final String DELETE_USERS = "DELETE FROM users";

    private static final String DELETE_TRANSACTIONS = "DELETE FROM transactions";

    private static final String SQL_DELETE_USERS =
            "DROP TABLE IF EXISTS " + User_Table.TABLE_NAME;


    private static final String[] upgrades = {"","",SQL_CREATE_TRANSACTIONS};

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion+1; i <= newVersion; i++) {
            db.execSQL(upgrades[i]);
        }
    }

    //User CRUD operations

    public int addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(User_Table.COLUMN_NAME, user.getName());
        values.put(User_Table.COLUMN_BALANCE, user.getBalance());

        int id = (int)db.insert(User_Table.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(User_Table.TABLE_NAME,
                                new String[]{User_Table._ID, User_Table.COLUMN_NAME, User_Table.COLUMN_BALANCE},
                                User_Table._ID +"=?", new String[]{String.valueOf(id)},
                                null,null,null,null);

        User user = null;
        if(cursor != null && cursor.getCount()> 0){
            cursor.moveToFirst();
            user = new User(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2));
        }

        return user;
    }

    public User findByUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(User_Table.TABLE_NAME,
                new String[]{User_Table._ID, User_Table.COLUMN_NAME, User_Table.COLUMN_BALANCE},
                User_Table.COLUMN_NAME +"=?", new String[]{username},
                null,null,null,null);

        User user = null;
        if(cursor != null && cursor.getCount()> 0){
            cursor.moveToFirst();
            user = new User(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2));
        }

        return user;
    }


    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String query = "SELECT "+User_Table._ID +","+User_Table.COLUMN_NAME+","+User_Table.COLUMN_BALANCE + " FROM " + User_Table.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2));
                users.add(user);
            }
            while (cursor.moveToNext());
        }

        return users;
    }

    public int getUsersCount() {

        String query = "SELECT count(*) FROM " + User_Table.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            cursor.moveToFirst();
            return (int)cursor.getLong(0);
        }
        return 0;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(User_Table.COLUMN_NAME, user.getName());
        values.put(User_Table.COLUMN_BALANCE, user.getBalance());

        return
                db.update(User_Table.TABLE_NAME,
                 values,
                 User_Table._ID + "= ?",
                 new String[]{String.valueOf(user.getId())});
    }

    public int updateUser(User user, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(User_Table.COLUMN_NAME, user.getName());
        values.put(User_Table.COLUMN_BALANCE, user.getBalance());

        return
                db.update(User_Table.TABLE_NAME,
                        values,
                        User_Table._ID + "= ?",
                        new String[]{String.valueOf(user.getId())});
    }


    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(User_Table.TABLE_NAME,
                User_Table._ID + "= ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    //CRUD Transactions

    public void addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Transactions_Table.COLUMN_GIVER, transaction.getGiverName());
        values.put(Transactions_Table.COLUMN_AMOUNT, transaction.getAmount());
        values.put(Transactions_Table.COLUMN_RECEIVERS, transaction.getReceiverNames());
        values.put(Transactions_Table.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(Transactions_Table.COLUMN_DATE, transaction.getDate().getTime());


        db.insert(Transactions_Table.TABLE_NAME, null, values);
        db.close();
    }

    public void addTransaction(Transaction transaction, SQLiteDatabase db) {

        ContentValues values = new ContentValues();
        values.put(Transactions_Table.COLUMN_GIVER, transaction.getGiverName());
        values.put(Transactions_Table.COLUMN_AMOUNT, transaction.getAmount());
        values.put(Transactions_Table.COLUMN_RECEIVERS, transaction.getReceiverNames());
        values.put(Transactions_Table.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(Transactions_Table.COLUMN_DATE, transaction.getDate().getTime());


        db.insert(Transactions_Table.TABLE_NAME, null, values);
    }

    public Transaction getTransaction(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Transactions_Table.TABLE_NAME,
                new String[]{Transactions_Table._ID, Transactions_Table.COLUMN_GIVER, Transactions_Table.COLUMN_AMOUNT,
                        Transactions_Table.COLUMN_RECEIVERS, Transactions_Table.COLUMN_DESCRIPTION, Transactions_Table.COLUMN_DATE},
                Transactions_Table._ID +"=?", new String[]{String.valueOf(id)},
                null,null,null,null);

       Transaction transaction = null;
        if(cursor != null && cursor.getCount()> 0){
            cursor.moveToFirst();
            transaction = new Transaction(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4), new Date(cursor.getInt(5)));
        }

        return transaction;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        String query = "SELECT "+Transactions_Table._ID +","+Transactions_Table.COLUMN_GIVER+","+Transactions_Table.COLUMN_AMOUNT +"," +
                        Transactions_Table.COLUMN_RECEIVERS +","+Transactions_Table.COLUMN_DESCRIPTION+","+Transactions_Table.COLUMN_DATE +

                 " FROM " + Transactions_Table.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Transaction transaction = new Transaction(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4), new Date(cursor.getLong(5)));
                transactions.add(transaction);
            }
            while (cursor.moveToNext());
        }

        return transactions;
    }

    public int getTransactionsCount() {

        String query = "SELECT count(*) FROM " + Transactions_Table.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor != null){
            cursor.moveToFirst();
            return (int)cursor.getLong(0);
        }
        return 0;
    }

    public int updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Transactions_Table.COLUMN_GIVER, transaction.getGiverName());
        values.put(Transactions_Table.COLUMN_AMOUNT, transaction.getAmount());
        values.put(Transactions_Table.COLUMN_RECEIVERS, transaction.getReceiverNames());
        values.put(Transactions_Table.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(Transactions_Table.COLUMN_DATE, transaction.getDate().getTime());

        return
                db.update(Transactions_Table.TABLE_NAME,
                        values,
                        User_Table._ID + "= ?",
                        new String[]{String.valueOf(transaction.getId())});
    }


    public void deleteTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        User giver = this.findByUsername(transaction.getGiverName());
        giver.setBalance(giver.getBalance() - transaction.getAmount());

        List<User> receivers = new ArrayList<>();
        String[] receiversUsernames = transaction.getReceiverNames().split(",");
        for (int i = 0; i < receiversUsernames.length; i++) {
            User receiver = this.findByUsername(receiversUsernames[i].trim());
            if(receiver.getId() == giver.getId()){
                receiver.setBalance(giver.getBalance() + transaction.getAmount() / receiversUsernames.length);
            }
            else {
                receiver.setBalance(receiver.getBalance() + transaction.getAmount() / receiversUsernames.length);
            }
            receivers.add(receiver);
        }

        db.beginTransaction();
        try {
            this.updateUser(giver, db);
            for(User receiver : receivers){
                this.updateUser(receiver, db);
            }
            db.delete(Transactions_Table.TABLE_NAME,
                    Transactions_Table._ID + "= ?",
                    new String[]{String.valueOf(transaction.getId())});
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
        db.close();
    }

    public void makeTransaction(String giverUsername, Double amount, String receiversUsernames, String description){


        User giver = this.findByUsername(giverUsername);
        if(giver == null){
            // should create new user
            giver = new User(giverUsername, 0d);
            int id = this.addUser(giver);
            giver.setId(id);
        }
        Set<User> receivers = new HashSet<>();
        for (String username: receiversUsernames.split(",")) {
            User receiver = this.findByUsername(username);
            if(receiver == null){
                // should create new user
                receiver = new User(username, 0d);
                int id = this.addUser(receiver);
                receiver.setId(id);
            }
            receivers.add(receiver);
        }
        StringBuilder realReceivers = new StringBuilder();
        for(User u : receivers){
            realReceivers.append(u.getName()).append(", ");
        }
        receiversUsernames = realReceivers.substring(0, realReceivers.length()-2);

        Transaction transaction = new Transaction(giverUsername, amount, receiversUsernames, description);
        giver.setBalance(giver.getBalance() + amount);
        int count = receivers.size();

        boolean giverIsReceiver = false;
        for (User receiver: receivers) {
            if(receiver.getId() == giver.getId()){
                receiver.setBalance(giver.getBalance() - amount/count );
                giverIsReceiver = true;
            }
            else {
                receiver.setBalance(receiver.getBalance() - amount / count);
            }
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            if(!giverIsReceiver) {
                this.updateUser(giver, db);
            }
            for(User receiver : receivers){
                this.updateUser(receiver,db);
            }
            this.addTransaction(transaction, db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();

    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(DELETE_TRANSACTIONS);
            db.execSQL(DELETE_USERS);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

}
