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
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        updateDatabase();
        displayStats();
        displayRecharges();
        viewFavourites();

    }

    public void viewFavourites(){
        Spinner state1 = (Spinner)findViewById(R.id.state1);
        Spinner state2 = (Spinner)findViewById(R.id.state2);
        Spinner state3 = (Spinner)findViewById(R.id.state3);
        Spinner state4 = (Spinner)findViewById(R.id.state4);
        Spinner state5 = (Spinner)findViewById(R.id.state5);
        Spinner operator1 = (Spinner)findViewById(R.id.operator1);
        Spinner operator2 = (Spinner)findViewById(R.id.operator2);
        Spinner operator3 = (Spinner)findViewById(R.id.operator3);
        Spinner operator4 = (Spinner)findViewById(R.id.operator4);
        Spinner operator5 = (Spinner)findViewById(R.id.operator5);
        TextView name1 = (TextView)findViewById(R.id.name1);
        TextView name2 = (TextView)findViewById(R.id.name2);
        TextView name3 = (TextView)findViewById(R.id.name3);
        TextView name4 = (TextView)findViewById(R.id.name4);
        TextView name5 = (TextView)findViewById(R.id.name5);
        TextView num1 = (TextView)findViewById(R.id.num1);
        TextView num2 = (TextView)findViewById(R.id.num2);
        TextView num3 = (TextView)findViewById(R.id.num3);
        TextView num4 = (TextView)findViewById(R.id.num4);
        TextView num5 = (TextView)findViewById(R.id.num5);

        List<Contact> favouriteContacts = getFavouriteContacts();
        String[] states = getStates();
        String[] operators = getOperators();
        for (int i = 0; i < 5; i++){
            Contact contact = favouriteContacts.get(i);
            switch (i){
                case 0:
                    name1.setText(contact.getName());
                    num1.setText(contact.getNumber());
                    state1.setSelection(getStateIndex(states,contact.getState()));
                    operator1.setSelection(getOperatorIndex(operators,contact.getOperator()));
                    break;
                case 1:
                    name2.setText(contact.getName());
                    num2.setText(contact.getNumber());
                    state2.setSelection(getStateIndex(states,contact.getState()));
                    operator2.setSelection(getOperatorIndex(operators,contact.getOperator()));
                    break;
                case 2:
                    name3.setText(contact.getName());
                    num3.setText(contact.getNumber());
                    state3.setSelection(getStateIndex(states,contact.getState()));
                    operator3.setSelection(getOperatorIndex(operators,contact.getOperator()));
                    break;
                case 3:
                    name4.setText(contact.getName());
                    num4.setText(contact.getNumber());
                    state4.setSelection(getStateIndex(states,contact.getState()));
                    operator4.setSelection(getOperatorIndex(operators,contact.getOperator()));
                    break;
                case 4:
                    name5.setText(contact.getName());
                    num5.setText(contact.getNumber());
                    state5.setSelection(getStateIndex(states,contact.getState()));
                    operator5.setSelection(getOperatorIndex(operators,contact.getOperator()));
                    break;
            }
        }
    }

    public String[] getStates(){
        String[] states = {"AP", "AS", "BR", "CH", "DL", "GJ", "HP", "HR", "JK", "KL",
                "KN", "KO", "MH", "MP", "MU", "NE", "OR", "PB", "RJ", "TN", "UE", "UW", "WB"};
        return states;
    }

    public int getStateIndex(String[] states, String queryState){
        int i = 0;
        for (; i < states.length; i++){
            if (states[i].equals(queryState))break;
        }
        return i;
    }

    public String[] getOperators(){
        String[] operators = {"A", "B", "B", "C", "I", "R", "T", "Y", "U", "V"};
        return operators;
    }

    public int getOperatorIndex(String[] operators, String queryOperator){
        int i = 0;
        for (; i < operators.length; i++){
            if (operators[i].equals(queryOperator))break;
        }
        return i;
    }

    public List<Contact> getFavouriteContacts(){
        List<Contact> favouriteContacts;
        DatabaseHelper dbh = new DatabaseHelper(this);
        favouriteContacts = dbh.getAllEntries();
        favouriteContacts = favouriteContacts.subList(0,5);
        return favouriteContacts;
    }

    public void displayRecharges(){
        List<Recharge> rechargeList = getRecharges();
        Stats stats = new Stats(this,getUserOperatorAndState());
        for (Recharge recharge:rechargeList)
            recharge.calculateMonthlyCost(stats);
        Collections.sort(rechargeList);
        StringBuffer sb = new StringBuffer();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        for (Recharge recharge:rechargeList){
            sb.append("----------\n");
            sb.append("Recharge with Rs.").append(recharge.getRechargeCost()).append("\n");
            sb.append("And get below benifits for ").append(recharge.getValidity()).append(" days\n\n");
            sb.append("Monthly Cost Rs.");
            sb.append(df.format(recharge.getMonthlyCost()/100.0));
            sb.append("\n\n");
            sb.append(recharge.getDescription());
            sb.append("\n");
        }
        TextView rechargesView = (TextView)findViewById(R.id.rechargesView);
        if (rechargesView != null) {
            rechargesView.setText(sb);
        }
    }

    public List<Recharge> getRecharges(){
        AssetManager assetManager = getBaseContext().getAssets();
        List<Recharge> rechargeList = new ArrayList<>();
        try {
            String userOperatorAndState = getUserOperatorAndState();
            String userOperator = userOperatorAndState.substring(0,1);
            String userState = userOperatorAndState.substring(1,3);
            InputStream inputStream = assetManager.open(userOperator+"_"+userState+".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null){
                String[] s = line.split("\\|");
                Double rechargeCost = Double.valueOf(s[0]+".0");
                Double validity = Double.valueOf(s[1]+".0");
                String location = s[2];
                String[] tariffDetails = s[3].split("-");
                if (tariffDetails.length == 1){
                    rechargeList.add(new Recharge(rechargeCost, validity, location, tariffDetails[0].substring(0, 1),
                            Double.valueOf(tariffDetails[0].substring(1) + ".0"), s[4]));
                }else if (tariffDetails.length == 2){
                    String pulseType = tariffDetails[1];
                    Double costPerPulse = Double.valueOf(tariffDetails[0] + ".0");
                    rechargeList.add(new Recharge(rechargeCost, validity, location, pulseType, costPerPulse,0.0,0.0,s[4]));
                }else if (tariffDetails.length == 4){
                    String pulseType = tariffDetails[1];
                    Double costPerPulse = Double.valueOf(tariffDetails[0] + ".0");
                    Double firstXPulses = Double.valueOf(tariffDetails[2] + ".0");
                    Double costPerFirstXPulses = Double.valueOf(tariffDetails[3] + ".0");
                    rechargeList.add(new Recharge(rechargeCost,validity,location,pulseType,
                            costPerPulse,firstXPulses,costPerFirstXPulses,s[4]));
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rechargeList;
    }

    public void displayStats() {
        TextView display = (TextView)findViewById(R.id.resultView);
        String userOperatorAndState = getUserOperatorAndState();
        String userOperator = userOperatorAndState.substring(0,1);
        String userState = userOperatorAndState.substring(1,3);

        Stats stats = new Stats(this, userOperatorAndState);

        StringBuffer sb = new StringBuffer();
        sb.append("User Operator: ").append(userOperator).append("\n");
        sb.append("User State: ").append(userState).append("\n");
        sb.append("Total Days: ").append(Long.toString(stats.getDays())).append("\n");
        sb.append("----------\n");
        sb.append("LocalSameSeconds: ").append(stats.getLocalSameSeconds()).append("\n");
        sb.append("LocalSameMinutes: ").append(stats.getLocalSameMinutes()).append("\n");
        sb.append("LocalOtherSeconds: ").append(stats.getLocalOtherSeconds()).append("\n");
        sb.append("LocalOtherMinutes: ").append(stats.getLocalOtherMinutes()).append("\n");
        sb.append("LocalTotalSeconds: ").append(stats.getLocalTotalSeconds()).append("\n");
        sb.append("LocalTotalMinutes: ").append(stats.getLocalTotalMinutes()).append("\n");
        sb.append("STDSeconds: ").append(stats.getStdSeconds()).append("\n");
        sb.append("STDMinutes: ").append(stats.getStdMinutes()).append("\n");
        sb.append("----------\n");
        if (display != null) {
            display.setText(sb);
        }

    }

    public String getUserOperatorAndState(){
        TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mncCode = tel.getNetworkOperator();
        String operatorName = tel.getNetworkOperatorName();
        String state = "", operator = "";
        AssetManager assetManager = getBaseContext().getAssets();
        InputStream inputStream;
        String userOperatorAndState = "";

        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userOperatorAndState;

    }

    public void updateDatabase (){

        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI,null,null,null,null);
        int numberColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int nameColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int durationColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int typeColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int dateColumnIndex = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        long recentCallDate = 0;
        long oldestCallDate = 99999999999999L;

        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        //Note: Storing recentCallDate in zero.name and oldestCallDate in zero.state
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
        databaseHelper.updateEntry(zero);
    }

    public HashMap<Integer,String> getOperatorsAndStates() {

        HashMap<Integer,String> operatorsAndStates = new HashMap<>();

        AssetManager assetManager = getBaseContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("finalsorted.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while (line != null){
                String[] s = line.split(" ");
                operatorsAndStates.put(Integer.parseInt(s[0]), s[1].concat(s[2]));
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operatorsAndStates;
    }
}