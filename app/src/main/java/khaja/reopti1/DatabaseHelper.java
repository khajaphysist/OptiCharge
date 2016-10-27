package khaja.reopti1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "contactsManager"; // db name
    private static final String TABLE_CONTACTS = "contacts"; // table name

    //column names of table "contacts"
    private static final String NAME = "name";
    private static final String NUMBER = "number";
    private static final String STATE = "state";
    private static final String OPERATOR = "operator";
    private static final String MINUTES = "minutes";
    private static final String SECONDS = "seconds";

    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + " ("
                + NUMBER + " INTEGER PRIMARY KEY ," + NAME + " TEXT, " + STATE
                + " TEXT, " + OPERATOR + " TEXT, " + MINUTES + " INTEGER, "
                + SECONDS + " INTEGER )";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(sqLiteDatabase);
    }

    public void addEntry(Contact contact){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(NUMBER,contact.getNumber());
        contentValues.put(NAME, contact.getName());
        contentValues.put(STATE,contact.getState());
        contentValues.put(OPERATOR,contact.getOperator());
        contentValues.put(MINUTES,contact.getMinutes());
        contentValues.put(SECONDS,contact.getSeconds());

        sqLiteDatabase.insert(TABLE_CONTACTS,null,contentValues);
        sqLiteDatabase.close();
    }

    public Contact getContact(String number){
        Contact x = new Contact("x","x","x","x",0,0);
        if (getCount()==0)return x;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABLE_CONTACTS, new String[] {
                NUMBER,NAME,STATE,OPERATOR,MINUTES,SECONDS}, NUMBER + " = ?", new String[]{number},null,null,null,null);
        if (cursor.moveToFirst()){
            Contact contact = new Contact(cursor.getString(1),cursor.getString(0),cursor.getString(2),cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)));
            return contact;
        }
        else return x;
    }

    public List<Contact> getAllEntries(){
        List<Contact> list = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            do {
                Contact contact = new Contact();
                contact.setNumber(cursor.getString(0));
                contact.setName(cursor.getString(1));
                contact.setState(cursor.getString(2));
                contact.setOperator(cursor.getString(3));
                contact.setMinutes(Integer.parseInt(cursor.getString(4)));
                contact.setSeconds(Integer.parseInt(cursor.getString(5)));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return list;
    }

    public int updateEntry (Contact contact){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NUMBER,contact.getNumber());
        contentValues.put(NAME, contact.getName());
        contentValues.put(STATE,contact.getState());
        contentValues.put(OPERATOR,contact.getOperator());
        contentValues.put(MINUTES,contact.getMinutes());
        contentValues.put(SECONDS,contact.getSeconds());

        return sqLiteDatabase.update(TABLE_CONTACTS,contentValues, NUMBER + " = ?", new String[] {contact.getNumber()});
    }

    public int getCount(){
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }
}
























