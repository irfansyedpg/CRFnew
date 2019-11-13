package edu.aku.hassannaqvi.template.validation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * created by irfan syed 11/11/2019

 */

public abstract class UIirfan {

    public  static SharedPreferences prefs;


    public static void findViews(View v,Context mcn) {

        prefs=mcn.getSharedPreferences("desing", MODE_PRIVATE);

        int Qlabel = prefs.getInt("qtxt", 18); //0 is the default value.

        int rblabel = prefs.getInt("rbtxt", 18); //0 is the default value.

        int cblable = prefs.getInt("cbtxt", 18); //0 is the default value.
        int edlable = prefs.getInt("edtxt", 18); //0 is the default value.

        int txtType = prefs.getInt("type", 1); //0 is the default value.

        // change language things......................





        if(txtType==2) {
            String lang = "ur";
            String country = "PK";

            Locale locale = new Locale(lang, country);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            mcn.getResources().updateConfiguration(config, mcn.getResources().getDisplayMetrics());
        }
        else
        {
            String lang = "en";
            String country = "US";
            Locale locale = new Locale(lang, country);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            mcn.getResources().updateConfiguration(config, mcn.getResources().getDisplayMetrics());
        }






        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    // recursively call this method
                    findViews(child,mcn);
                }
            }

            else if(v instanceof RadioButton)
            {
                RadioButton rb = (RadioButton) v;
                rb.setTextSize(rblabel);
            }

            else if(v instanceof CheckBox)
            {
                CheckBox cb = (CheckBox) v;
                cb.setTextSize(cblable);
            }

            else if(v instanceof EditText)
            {
                EditText ed = (EditText) v;
                ed.setTextSize(edlable);
            }
                else if (v instanceof TextView)
            {
                //do whatever you want ...

                TextView tx = (TextView) v;
                tx.setTextSize(Qlabel);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







}
