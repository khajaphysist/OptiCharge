package khaja.reopti1;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        TextView resultOutput = (TextView)findViewById(R.id.resultView);

    }

    public String getUserOperatorAndState() throws IOException{
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mncCode = tel.getNetworkOperator();
        String operatorName = tel.getNetworkOperatorName();
        String state = "", operator = "";
        AssetManager assetManager = getBaseContext().getAssets();
        InputStream inputStream;
        String userOperatorAndState = "";

        inputStream = assetManager.open("mnccodes.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null){
            String[] s = line.split("-");
            if (s[0].equals(mncCode)){
                state = s[1];
            }
            line = reader.readLine();
        }
        inputStream = assetManager.open("operators.txt");
        inputStreamReader = new InputStreamReader(inputStream);
        reader = new BufferedReader(inputStreamReader);
        line = reader.readLine();
        while (line != null){
            String[] s = line.split("-");
            if (s[0].equalsIgnoreCase(operatorName)){
                operator = s[1];
            }
            line = reader.readLine();
        }

        userOperatorAndState = operator.concat(state);
        return userOperatorAndState;
    }

    public void updateDatabase () throws IOException{

        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI,null,null,null,null);
        int numberColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int nameColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int durationColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int typeColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int dateColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.DATE);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Contact zero = databaseHelper.getContact("0");
        if (zero.getName().equals("x")){
            zero.setName("0");
            zero.setNumber("0");
            zero.setState("0");
            zero.setOperator("0");
            zero.setMinutes(0);
            zero.setSeconds(0);
        }

        HashMap<Integer,String> operatorsAndStates = getOperatorsAndStates();


        managedCursor.moveToLast();
        do {
            String number = managedCursor.getString(numberColumnIndex);
            String name = managedCursor.getString(nameColumnIndex);
            int duration = Integer.parseInt(managedCursor.getString(durationColumnIndex));
            String type = managedCursor.getString(typeColumnIndex);
            long date = Long.parseLong(managedCursor.getString(dateColumnIndex));

            if (number.length()>=10)number = number.substring(number.length()-10);
            if (!type.equals("2"))continue;
            if (duration==0)continue;

            Contact contact = databaseHelper.getContact(number);
            if (contact.getName().equals("x")){

                String operatorAndState = operatorsAndStates.get(Integer.parseInt(number.substring(0,5)));
                if (operatorAndState == null)
                    operatorAndState = operatorsAndStates.get(Integer.parseInt(number.substring(0,4)));

                contact.setName(name);
                contact.setNumber(number);
                contact.setOperator(operatorAndState.substring(0,1));
                contact.setState(operatorAndState.substring(1,3));
                contact.setSeconds(duration);
                contact.setMinutes(duration/60);
                databaseHelper.addEntry(contact);
            }

            else{
                if (Long.parseLong(zero.getName())<date){
                    contact.setSeconds(duration+contact.getSeconds());
                    contact.setMinutes((duration/60)+contact.getMinutes());
                    databaseHelper.updateEntry(contact);
                }else break;
            }

        }while (managedCursor.moveToPrevious());

        managedCursor.moveToLast();
        zero.setName(managedCursor.getString(dateColumnIndex));
        databaseHelper.updateEntry(zero);
    }

    public HashMap<Integer,String> getOperatorsAndStates() throws IOException {

        HashMap<Integer,String> operatorsAndStates = new HashMap<>();

        AssetManager assetManager = getBaseContext().getAssets();
        InputStream inputStream = assetManager.open("finalsorted.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = bufferedReader.readLine();
        while (line != null){
            String[] s = line.split(" ");
            operatorsAndStates.put(Integer.parseInt(s[0]), s[1].concat(s[2]));
            line = bufferedReader.readLine();
        }

        return operatorsAndStates;
    }
}


















