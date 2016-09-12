package mks.co.cloudmagicdemo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Mahesh on 7/9/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private final String TAG = NetworkChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (CommonUtilities.isConnectedToInternet(context)) {
            context.sendBroadcast(new Intent(Constants.INTENT_NETWORK_AVAILABLE));
        }
    }
}
