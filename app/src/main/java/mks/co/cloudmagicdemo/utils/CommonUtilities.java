package mks.co.cloudmagicdemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Mahesh on 7/9/16.
 */
public class CommonUtilities {

    public static boolean isConnectedToInternet(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message,
                Toast.LENGTH_SHORT).show();
    }

    public static String getCommaSeperatedString(ArrayList<String> stringArrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        String sep = ", ";
        for (int i = 0; i < stringArrayList.size(); i++) {
            stringBuilder.append(stringArrayList.get(i));
            if (i != stringArrayList.size() - 1) {
                stringBuilder.append(sep);
            }
        }
        return stringBuilder.toString();
    }

    public static String getDateFromTimeStamp(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp*1000);
        return simpleDateFormat.format(calendar.getTime());
    }
}
