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
import android.util.Log;
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
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        try {
            updateDatabase();
            displayStats();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public void displayStats() throws IOException{
        TextView display = (TextView)findViewById(R.id.resultView);
        String userOperatorAndState = getUserOperatorAndState();
        String userOperator = userOperatorAndState.substring(0,1);
        String userState = userOperatorAndState.substring(1,3);
        int localSame = 0;
        int localOthers = 0;
        int localTotal = 0;
        int std = 0;
        DatabaseHelper dbh = new DatabaseHelper(this);
        Contact zero = dbh.getContact("1");
        List<Contact> contactList = dbh.getAllEntries();
        for (Contact contact:contactList){
            if (contact.getState().equals(userState)){
                localTotal = localTotal + contact.getSeconds();
                if (contact.getOperator().equals(userOperator))
                    localSame = localSame + contact.getSeconds();
                else localOthers = localOthers + contact.getSeconds();
            }
            else std = std + contact.getSeconds();
        }
        StringBuffer sb = new StringBuffer();
        sb.append("User Operator: "+userOperator+"\n");
        sb.append("User State: "+userState+"\n");
        sb.append("Total Days: "+zero.getOperator()+"\n");
        sb.append("----------\n");
        sb.append("Local Same: "+localSame+"\n");
        sb.append("Local Others: "+localOthers+"\n");
        sb.append("Local Total: "+localTotal+"\n");
        sb.append("STD: "+std+"\n");
        sb.append("----------\n");
        display.setText(sb);

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
        long recentCallDate = 0;
        long oldestCallDate = 99999999999999L;

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Contact zero = databaseHelper.getContact("1");
        if (zero.getName().equals("x")){
            zero.setName("1");
            zero.setNumber("1");
            zero.setState("99999999999999");
            zero.setOperator("1");
            zero.setMinutes(0);
            zero.setSeconds(0);
            databaseHelper.addEntry(zero);
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

            if (date>recentCallDate)recentCallDate=date;
            if (date<oldestCallDate)oldestCallDate=date;

            Contact contact = databaseHelper.getContact(number);
            if (contact.getOperator().equals("x")){

                String operatorAndState = operatorsAndStates.get(Integer.parseInt(number.substring(0,5)));
                if (operatorAndState == null)
                    operatorAndState = operatorsAndStates.get(Integer.parseInt(number.substring(0,4)));
                if (operatorAndState == null) continue;

                contact.setName(name);
                contact.setNumber(number);
                contact.setOperator(operatorAndState.substring(0,1));
                contact.setState(operatorAndState.substring(1,3));
                contact.setSeconds(duration);
                int minutes = duration/60;
                if(duration%60 > 0) minutes = minutes + 1;
                contact.setMinutes(minutes);
                databaseHelper.addEntry(contact);
            }

            else{
                if (Long.parseLong(zero.getName())<date){
                    contact.setSeconds(duration+contact.getSeconds());
                    int minutes = duration/60;
                    if(duration%60 > 0) minutes = minutes + 1;
                    contact.setMinutes(minutes+contact.getMinutes());
                    databaseHelper.updateEntry(contact);
                }else break;
            }

        }while (managedCursor.moveToPrevious());

        zero.setName(Long.toString(recentCallDate));
        if (Long.parseLong(zero.getState())>oldestCallDate)
            zero.setState(Long.toString(oldestCallDate));
        zero.setOperator(Long.toString((recentCallDate-oldestCallDate)/(86400000L)));
        Log.d("oldest: ",Long.toString(oldestCallDate));
        Log.d("recent: ",Long.toString(recentCallDate));
        Log.d("diff: ",Long.toString(recentCallDate-oldestCallDate));
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


















