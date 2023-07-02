package com.example.letswritetogether.Utilities;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class SignalGenerator {
    private static SignalGenerator instance = null;
    private Context context;
    private static Vibrator vibrator;

    private SignalGenerator(Context context) {
        this.context = context;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SignalGenerator(context);
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
    public static SignalGenerator getInstance() {
        return instance;
    }

    public void toast(String text, int length) {
        Toast
                .makeText(context, text, length)
                .show();
    }
    public void alertDialog(String title, String text, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton(button, null)
                .show();
    }
    public void vibrate(long length){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(length);
        }
    }
}
