package khaja.reopti1;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
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

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        TextView resultOutput = (TextView)findViewById(R.id.resultView);

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
                if (s[0].equals(operatorName)){
                    operator = s[1];
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        userOperatorAndState = operator.concat(state);
        return userOperatorAndState;
    }
}
