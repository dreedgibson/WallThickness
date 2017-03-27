package gibson.wallthickness;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.HashMap;
import java.util.Objects;
import static gibson.wallthickness.F101.parameters;
import static java.lang.String.valueOf;

public class F101_4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f101_4);
        prePopulate(parameters);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void prePopulate(HashMap parameters){
        /**
         * function to prepopulate the fields based on the values in the parameters hashmap
         */
        EditText wdens = (EditText) findViewById(R.id.wDens);
        EditText WDmin =(EditText) findViewById(R.id.WDmin);
        EditText WDmax = (EditText) findViewById(R.id.WDmax);
        EditText hTide =(EditText) findViewById(R.id.hTide);
        EditText hMax = (EditText) findViewById(R.id.hMax);

        Spinner wdensUnit = (Spinner) findViewById(R.id.wDensSpin);
        Spinner WDminUnit = (Spinner) findViewById(R.id.minWDSpin);
        Spinner WDmaxUnit = (Spinner) findViewById(R.id.maxWDSpin);
        Spinner hTideUnit = (Spinner) findViewById(R.id.tideSpin);
        Spinner hMaxUnit = (Spinner) findViewById(R.id.waveHeightSpin);

        Spinner fluidClass = (Spinner) findViewById(R.id.FCSpin);
        Spinner locationClass = (Spinner) findViewById(R.id.LCSpin);

        if(parameters.get("wdens") == null){
            wdens.setText("");
            WDmin.setText("");
            WDmax.setText("");
            hTide.setText("");
            hMax.setText("");
        } else {
            wdens.setText(valueOf(parameters.get("wdens")));
            WDmin.setText(valueOf(parameters.get("WDmin")));
            WDmax.setText(valueOf(parameters.get("WDmax")));
            hTide.setText(valueOf(parameters.get("hTide")));
            hMax.setText(valueOf(parameters.get("hMax")));
            wdensUnit.setSelection((int) (Math.round((Double) parameters.get("wdensUnitSpin")) - 1));
            WDminUnit.setSelection((int) (Math.round((Double) parameters.get("WDminUnitSpin")) - 1));
            WDmaxUnit.setSelection((int) (Math.round((Double) parameters.get("WDmaxUnitSpin")) - 1));
            hTideUnit.setSelection((int) (Math.round((Double) parameters.get("hTideUnitSpin")) - 1));
            hMaxUnit.setSelection((int) (Math.round((Double) parameters.get("hMaxUnitSpin")) - 1));
            fluidClass.setSelection((int) (Math.round((Double) parameters.get("fluidClass")) - 1));
            locationClass.setSelection((int) (Math.round((Double) parameters.get("locationClass")) - 1));
        }
    }

    int errorCheck() {
        /**
         * this function performs all error checking on the values input into
         * this activity.  It takes no input but will set errors on invalid entries.
         */
        EditText wDens = (EditText) findViewById(R.id.wDens);
        EditText WDmin =(EditText) findViewById(R.id.WDmin);
        EditText WDmax = (EditText) findViewById(R.id.WDmax);
        EditText hTide =(EditText) findViewById(R.id.hTide);
        EditText hMax = (EditText) findViewById(R.id.hMax);

        // Throw errors if the user has not input any values into the EditText fields
        if(TextUtils.isEmpty(wDens.getText().toString())) {
            wDens.setError("Water Density cannot be blank");
            return 1;
        }else if(TextUtils.isEmpty(WDmin.getText().toString())) {
            WDmin.setError("Minimum water depth cannot be blank.");
            return 2;
        }else if(TextUtils.isEmpty(WDmax.getText().toString())) {
            WDmax.setError("Maximum water depth cannot be blank");
            return 3;
        }else if(TextUtils.isEmpty(hTide.getText().toString())) {
            hTide.setError("Tidal height cannot be blank.");
            return 4;
        }else if(TextUtils.isEmpty(hMax.getText().toString())) {
            hMax.setError("Maximum wave height cannot be blank");
            return 5;
        }else if(Double.parseDouble(hTide.getText().toString()) < 0) {
            hTide.setError("Maximum tidal height cannot be negative");
            return 6;
        }else if(Double.parseDouble(wDens.getText().toString()) < 0) {
            wDens.setError("Water density cannot be negative");
            return 7;
        }else if(Double.parseDouble(WDmin.getText().toString()) < 0) {
            WDmin.setError("Minimum water depth cannot be negative");
            return 8;
        }else if(Double.parseDouble(WDmax.getText().toString()) < 0) {
            WDmax.setError("Maximum water depth cannot be negative");
            return 9;
        }else if(Double.parseDouble(hMax.getText().toString()) < 0) {
            hMax.setError("Maximum wave height cannot be negative");
            return 10;
        } else return 0;
    }

    void storeVals() {
        /**
         * This function is called after errorCheck and is used to store the
         * validated values into the hashMap 'parameters'.
         */
        EditText wdens = (EditText) findViewById(R.id.wDens);
        EditText WDmin = (EditText) findViewById(R.id.WDmin);
        EditText WDmax = (EditText) findViewById(R.id.WDmax);
        EditText hTide = (EditText) findViewById(R.id.hTide);
        EditText hMax = (EditText) findViewById(R.id.hMax);

        // read in the units from spinners
        Spinner wdensSpin = (Spinner) findViewById(R.id.wDensSpin);
        Spinner WDminSpin = (Spinner) findViewById(R.id.minWDSpin);
        Spinner WDmaxSpin = (Spinner) findViewById(R.id.maxWDSpin);
        Spinner hTideSpin = (Spinner) findViewById(R.id.tideSpin);
        Spinner hMaxSpin = (Spinner) findViewById(R.id.waveHeightSpin);

        // read in class spinners
        Spinner fluidClass = (Spinner) findViewById(R.id.FCSpin);
        Spinner locationClass = (Spinner) findViewById(R.id.LCSpin);

        // store fields into hashmap
        parameters.put("wdens", Double.parseDouble(valueOf(wdens.getText())));
        parameters.put("WDmin", Double.parseDouble(valueOf(WDmin.getText())));
        parameters.put("WDmax", Double.parseDouble(valueOf(WDmax.getText())));
        parameters.put("hTide", Double.parseDouble(valueOf(hTide.getText())));
        parameters.put("hMax", Double.parseDouble(valueOf(hMax.getText())));

        // store values used in calculation of wall thickness in hashmap
        storeCalcVals(wdens, WDmin, WDmax, hTide, hMax, wdensSpin, WDminSpin, WDmaxSpin, hTideSpin, hMaxSpin);

        // store class specific values
        storeSpinVals(fluidClass, locationClass);
    }

    void storeCalcVals(EditText wdens, EditText WDmin, EditText WDmax, EditText hTide, EditText hMax, Spinner wdensSpin, Spinner WDminSpin, Spinner WDmaxSpin, Spinner hTideSpin, Spinner hMaxSpin) {
        // water density
        if (Objects.equals(wdensSpin.getSelectedItem().toString(), "lb/ft^3")) {
            parameters.put("wdensUnitSpin", 2.0);
            parameters.put("wdensCalc", 16.0185 * Double.parseDouble(valueOf(wdens.getText())));
        } else {
            parameters.put("wdensUnitSpin", 1.0);
            parameters.put("wdensCalc", Double.parseDouble(valueOf(wdens.getText())));
        }

        // minimum water depth
        if (Objects.equals(WDminSpin.getSelectedItem().toString(), "ft")) {
            parameters.put("WDminCalc", 0.3048 * Double.parseDouble(valueOf(WDmin.getText())));
            parameters.put("WDminUnitSpin", 2.0);
        } else {
            parameters.put("WDminUnitSpin", 1.0);
            parameters.put("WDminCalc", Double.parseDouble(valueOf(WDmin.getText())));
        }

        // maximum water depth
        if (Objects.equals(WDmaxSpin.getSelectedItem().toString(), "ft")) {
            parameters.put("WDmaxCalc", 0.3048 * Double.parseDouble(valueOf(WDmax.getText())));
            parameters.put("WDmaxUnitSpin", 2.0);
        } else {
            parameters.put("WDmaxUnitSpin", 1.0);
            parameters.put("WDmaxCalc", Double.parseDouble(valueOf(WDmax.getText())));
        }

        // tidal height
        if (Objects.equals(hTideSpin.getSelectedItem().toString(), "ft")) {
            parameters.put("hTideCalc", 0.3048 * Double.parseDouble(valueOf(hTide.getText())));
            parameters.put("hTideUnitSpin", 2.0);
        } else {
            parameters.put("hTideUnitSpin", 1.0);
            parameters.put("hTideCalc", Double.parseDouble(valueOf(hTide.getText())));
        }

        // maximum wave height
        if (Objects.equals(hMaxSpin.getSelectedItem().toString(), "ft")) {
            parameters.put("hMaxCalc", 0.3048 * Double.parseDouble(valueOf(hMax.getText())));
            parameters.put("hMaxUnitSpin", 2.0);
        } else {
            parameters.put("hMaxUnitSpin", 1.0);
            parameters.put("hMaxCalc", Double.parseDouble(valueOf(hMax.getText())));
        }
    }

    void storeSpinVals(Spinner fluidClass, Spinner locationClass) {
        // Fluid Class
        if ((Objects.equals(fluidClass.getSelectedItem().toString(), "A - non-flammable"))) {
            parameters.put("fluidClass", 1.0);
        } else if (Objects.equals(fluidClass.getSelectedItem().toString(), "B - production fluids")) {
            parameters.put("fluidClass", 2.0);
        } else if (Objects.equals(fluidClass.getSelectedItem().toString(), "C - Non-flammable Gas")) {
            parameters.put("fluidClass", 3.0);
        } else if (Objects.equals(fluidClass.getSelectedItem().toString(), "D - single phase Gas")) {
            parameters.put("fluidClass", 4.0);
        } else parameters.put("fluidClass", 5.0);

        // Location Class
        if ((Objects.equals(locationClass.getSelectedItem().toString(), "1 - no frequent human activity"))) {
            parameters.put("locationClass", 1.0);
        } else if (Objects.equals(locationClass.getSelectedItem().toString(), "2a - pipeline near platform")) {
            parameters.put("locationClass", 2.0);
        } else parameters.put("locationClass", 3.0);
    }

    public void onEnvButtonClick(View view) {
        // call errorCheck to determine validity of input
        if(errorCheck()!= 0) {
            return;
        }

        // store values in HashMap
        storeVals();

        // send to next activity.
        Intent intent = new Intent(F101_4.this, F101_5.class);
        startActivity(intent);
    }
}
