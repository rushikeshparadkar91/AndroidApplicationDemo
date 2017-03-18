package app.packman.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by sujaysudheendra on 3/18/16.
 */
public class UtilityClass {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showSnackBar(CoordinatorLayout createShipmentCoordinatorLayout, String message) {
        Snackbar snackbar = Snackbar
                .make(createShipmentCoordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static AlertDialog BuildSimpleAlert(Context context, String message, String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton("Ok", null);
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * This method is used to serialize the objects
     * so that they can be used for comparision
     *
     * @param object
     * @return
     */
    public static String serializeObject(Object object) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream so = null;
        try {
            so = new ObjectOutputStream(bo);
            so.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            so.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bo.toString();
    }
}
