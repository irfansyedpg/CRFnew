package edu.aku.dmu.kmc_screening.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.aku.dmu.kmc_screening.JSONModels.JSONModelCRFA;
import edu.aku.dmu.kmc_screening.JsonUtils.JSONUtils;
import edu.aku.dmu.kmc_screening.R;
import edu.aku.dmu.kmc_screening.contracts.FormsContract;
import edu.aku.dmu.kmc_screening.core.DatabaseHelper;
import edu.aku.dmu.kmc_screening.core.MainApp;
import edu.aku.dmu.kmc_screening.databinding.ActivityBBinding;
import edu.aku.dmu.kmc_screening.other.DiseaseCode;
import edu.aku.dmu.kmc_screening.util.Util;
import edu.aku.dmu.kmc_screening.validation.UIirfan;
import edu.aku.dmu.kmc_screening.validation.ValidatorClass;

import static edu.aku.dmu.kmc_screening.core.MainApp.fc;

public class CRFBActivity extends AppCompatActivity {

    private static final String TAG = "CRFBActivity";
    ActivityBBinding bi;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        bi = DataBindingUtil.setContentView(this, R.layout.activity_b);
        bi.setCallback(this);

        db = new DatabaseHelper(this);

//        setTitle(R.string.f9aHeading);
        List<String> Dieascodelist = new ArrayList<>(DiseaseCode.HmDiseaseCode.keySet());

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, Dieascodelist);
        bi.crb12.setThreshold(1); //will start working from first character
        bi.crb12.setAdapter(adapter);

        bi.crb12b.setThreshold(1); //will start working from first character
        bi.crb12b.setAdapter(adapter);


        setupViews();

        UIirfan.findViews(bi.GrpCRFB,this);
    }

    private void setupViews() {

        bi.crb01.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().equals("")) {
                    bi.checkDataLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        bi.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bi.crb01.getText().toString().equals("")) {
                     FormsContract fc = db.getsFormContract(bi.crb01.getText().toString());
                    if (fc != null) {
                        JSONModelCRFA crfa = JSONUtils.getModelFromJSON(fc.getCRFA(), JSONModelCRFA.class);







                        try
                        {
                            if(!crfa.getCra12().equals("2"))
                            {

                                Toast.makeText(CRFBActivity.this,"No record found",Toast.LENGTH_LONG).show();
                                return;
                            }

                            bi.crb02.setText(crfa.getCra02());
                            bi.crb05.setText(crfa.getCra04());
                            bi.crb06.setText(crfa.getCra05());

                            bi.crb07a.setText(crfa.getCra06a());
                            bi.crb07b.setText(crfa.getCra06b());
                            bi.crb07c.setText(crfa.getCra06c());
                            bi.crb07d.setText(crfa.getCra06d());
                            bi.crb07e.setText(crfa.getCra06e());

                            bi.crb08.setText(crfa.getCra07());

                            bi.crb09a.setText(crfa.getCra08a());
                            bi.crb09b.setText(crfa.getCra08b());
                            bi.crb09c.setText(crfa.getCra08c());

                            if (crfa.getCra09().equals("1")) {
                                bi.crb10a.setChecked(true);
                            } else {
                                bi.crb10b.setChecked(true);
                            }

                            bi.checkDataLayout.setVisibility(View.VISIBLE);

                        }
                        catch (Exception e)
                        {
                            bi.crb01.setError("No record Avilable for this Study ID");
                            bi.crb01.requestFocus();

                            bi.checkDataLayout.setVisibility(View.GONE);

                        }



                    }
                    else {
                        bi.crb01.setError("No record Avilable for this Study ID");
                        bi.crb01.requestFocus();



                    }

                }

            }
        });
    }



    public void BtnContinue() {
        if (formValidation()) {



            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date PresentationDate = null;
            Date DischargeDate = null;
            try {
                PresentationDate = sdf.parse(bi.crb04a.getText()+"/"+ bi.crb04b.getText()+"/" +bi.crb04c.getText());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() < PresentationDate.getTime()) {

                bi.crb04a.setError("Can not be greater then current date");
                bi.crb04a.requestFocus();
                return;
            }




            try {
                DischargeDate = sdf.parse(bi.crb14a.getText()+"/"+ bi.crb14b.getText()+"/" +bi.crb14c.getText());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (DischargeDate.getTime() < PresentationDate.getTime()) {

                bi.crb14a.setError("Can not be less then Presentation  date");
                bi.crb14a.requestFocus();
                return;
            }




            try {
                SaveDraft();
                if (UpdateDB()) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), EndingActivity.class).putExtra("complete", true));
                } else {
                    Toast.makeText(this, "Error in updating db!!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean UpdateDB() {

        DatabaseHelper db = new DatabaseHelper(this);

        // 2. insert form
        Long rowId;
        rowId = db.addForm(fc);
        if (rowId > 0) {
            fc.set_ID(String.valueOf(rowId));
            fc.setUID((fc.getDeviceID() + fc.get_ID()));
            db.updateFormID(fc);
            return true;
        } else {
            Toast.makeText(this, "Updating Database... ERROR!", Toast.LENGTH_SHORT).show();
            return false;

        }
    }

    private void SaveDraft() throws JSONException {


        fc = new FormsContract();
        SharedPreferences sharedPref = getSharedPreferences("tagName", MODE_PRIVATE);
        fc.setTagID(sharedPref.getString("tagName", null));
        fc.setFormDate((DateFormat.format("dd-MM-yyyy HH:mm", new Date())).toString());
        fc.setDeviceID(MainApp.deviceId);
        fc.setUser(MainApp.userName);

        fc.setAppversion(MainApp.versionName + "." + MainApp.versionCode);

        JSONObject f1 = new JSONObject();
        Util.setGPS(this);
        fc.setF1(String.valueOf(f1));


        JSONObject CRFA = new JSONObject();


        CRFA.put("crb01", bi.crb01.getText().toString());

        CRFA.put("crb02", bi.crb02.getText().toString());
        CRFA.put("crb03", bi.crb03.getText().toString());

        CRFA.put("crb04a", bi.crb04a.getText().toString());
        CRFA.put("crb04b", bi.crb04b.getText().toString());
        CRFA.put("crb04c", bi.crb04c.getText().toString());

        CRFA.put("crb05", bi.crb05.getText().toString());

        CRFA.put("crb06", bi.crb06.getText().toString());

        CRFA.put("crb07a", bi.crb07a.getText().toString());
        CRFA.put("crb07e", bi.crb07e.getText().toString());
        CRFA.put("crb07b", bi.crb07b.getText().toString());
        CRFA.put("crb07c", bi.crb07c.getText().toString());
        CRFA.put("crb07d", bi.crb07d.getText().toString());

        CRFA.put("crb08", bi.crb08.getText().toString());

        CRFA.put("crb09a", bi.crb09a.getText().toString());
        CRFA.put("crb09b", bi.crb09b.getText().toString());
        CRFA.put("crb09c", bi.crb09c.getText().toString());

        CRFA.put("crb10",
                bi.crb10a.isChecked() ? "1"
                        : bi.crb10b.isChecked() ? "2"
                        : "0");

        CRFA.put("crb11", bi.crb11.getText().toString());

        CRFA.put("crb12", DiseaseCode.HmDiseaseCode.get(bi.crb12.getText().toString()));
        CRFA.put("crb12b", DiseaseCode.HmDiseaseCode.get(bi.crb12b.getText().toString()));


        CRFA.put("crb13",
                bi.crb13a.isChecked() ? "1"
                        : bi.crb13b.isChecked() ? "2"
                        : bi.crb13c.isChecked() ? "3"
                        : bi.crb13d.isChecked() ? "4"
                        : "0");

        CRFA.put("crb14a", bi.crb14a.getText().toString());
        CRFA.put("crb14b", bi.crb14b.getText().toString());
        CRFA.put("crb14c", bi.crb14c.getText().toString());


        fc.setCRFA(String.valueOf(CRFA));

        fc.setFormType("CRFB");
        fc.setstudyid(bi.crb01.getText().toString());


    }

    private boolean formValidation() {

        return ValidatorClass.EmptyCheckingContainer(this, bi.GrpCRFB);


    }


    public void BtnEnd() {

       this.finish();

        // MainApp.endActivity(this, this);
    }


    /*
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "You can't go back.", Toast.LENGTH_SHORT).show();
    }


     */
}
