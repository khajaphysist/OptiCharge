package khaja.reopti1;

import android.content.Context;

import java.util.List;

public class Stats {
    int localSameSeconds;
    int localSameMinutes;
    int localOtherSeconds;
    int localOtherMinutes;
    int localTotalSeconds;
    int localTotalMinutes;
    int stdSeconds;
    int stdMinutes;
    Long days;

    public int getLocalSameSeconds() {
        return localSameSeconds;
    }

    public int getLocalSameMinutes() {
        return localSameMinutes;
    }

    public int getLocalOtherSeconds() {
        return localOtherSeconds;
    }

    public int getLocalOtherMinutes() {
        return localOtherMinutes;
    }

    public int getLocalTotalSeconds() {
        return localTotalSeconds;
    }

    public int getLocalTotalMinutes() {
        return localTotalMinutes;
    }

    public int getStdSeconds() {
        return stdSeconds;
    }

    public int getStdMinutes() {
        return stdMinutes;
    }

    public Long getDays() {
        return days;
    }

    public Stats(Context context, String userOperatorAndState){
        DatabaseHelper dbh = new DatabaseHelper(context);
        String userOperator = userOperatorAndState.substring(0,1);
        String userState = userOperatorAndState.substring(1,3);
        Contact zero = dbh.getContact("1");
        List<Contact> contactList = dbh.getAllEntries();
        for (Contact contact:contactList){
            if (contact.getState().equals(userState)){
                localTotalSeconds = localTotalSeconds + contact.getSeconds();
                localTotalMinutes = localTotalMinutes + contact.getMinutes();
                if (contact.getOperator().equals(userOperator)){
                    localSameSeconds = localSameSeconds + contact.getSeconds();
                    localSameMinutes = localSameMinutes + contact.getMinutes();
                }
                else {
                    localOtherSeconds = localOtherSeconds + contact.getSeconds();
                    localOtherMinutes = localOtherMinutes + contact.getMinutes();
                }
            }
            else {
                stdSeconds = stdSeconds + contact.getSeconds();
                stdMinutes = stdMinutes + contact.getMinutes();
            }
        }
        days = (Long.parseLong(zero.getName())-Long.parseLong(zero.getState()))/(86400000L);
    }
}
