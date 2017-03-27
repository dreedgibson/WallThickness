package gibson.wallthickness;

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

public class F101_3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f101_3);
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
        EditText DP = (EditText) findViewById(R.id.DP_num);
        EditText DT =(EditText) findViewById(R.id.DT_num);
        EditText IT = (EditText) findViewById(R.id.IT_num);
        EditText LAT =(EditText) findViewById(R.id.LAT_num);
        EditText dens = (EditText) findViewById(R.id.Dens_num);
        EditText ratio =(EditText) findViewById(R.id.Ratio_num);

        Spinner DPUnit = (Spinner) findViewById(R.id.spin_pressure);
        Spinner DTUnit = (Spinner) findViewById(R.id.spin_DT);
        Spinner ITUnit = (Spinner) findViewById(R.id.spin_IT);
        Spinner LATUnit = (Spinner) findViewById(R.id.spin_LAT);
        Spinner densUnit = (Spinner) findViewById(R.id.spin_Dens);

        // populate fields with blanks if the key does not exist otherwise populate with values from hashmap
        if(parameters.get("DP") == null){
            DP.setText("");
            DT.setText("");
            IT.setText("");
            LAT.setText("");
            dens.setText("");
            ratio.setText("");
        } else {
            DP.setText(valueOf(parameters.get("DP")));
            DT.setText(valueOf(parameters.get("DT")));
            IT.setText(valueOf(parameters.get("IT")));
            LAT.setText(valueOf(parameters.get("LAT")));
            dens.setText(valueOf(parameters.get("dens")));
            ratio.setText(valueOf(parameters.get("ratio")));
            DPUnit.setSelection((int) (Math.round((Double) parameters.get("DPUnitSpin")) - 1));
            DTUnit.setSelection((int) (Math.round((Double) parameters.get("DTUnitSpin")) - 1));
            ITUnit.setSelection((int) (Math.round((Double) parameters.get("ITUnitSpin")) - 1));
            LATUnit.setSelection((int) (Math.round((Double) parameters.get("LATUnitSpin")) - 1));
            densUnit.setSelection((int) (Math.round((Double) parameters.get("densUnitSpin")) - 1));
        }
    }

    int errorCheck() {
        /**
         * this function performs all error checking on the values input into
         * this activity.  It takes no input but will set errors on invalid entries.
         */
        EditText DP = (EditText) findViewById(R.id.DP_num);
        EditText DT =(EditText) findViewById(R.id.DT_num);
        EditText IT = (EditText) findViewById(R.id.IT_num);
        EditText LAT =(EditText) findViewById(R.id.LAT_num);
        EditText dens = (EditText) findViewById(R.id.Dens_num);
        EditText ratio =(EditText) findViewById(R.id.Ratio_num);

        // Throw errors if the user has not input any values into the EditText fields
        if(TextUtils.isEmpty(DP.getText().toString())) {
            DP.setError("Design Pressure cannot be blank");
            return 1;
        }else if(TextUtils.isEmpty(DT.getText().toString())) {
            DT.setError("Design Temperature cannot be blank.");
            return 2;
        }else if(TextUtils.isEmpty(IT.getText().toString())) {
            IT.setError("Installation Temperature cannot be blank");
            return 3;
        }else if(TextUtils.isEmpty(LAT.getText().toString())) {
            LAT.setError("Pressure Reference Elevation cannot be blank.");
            return 4;
        }else if(TextUtils.isEmpty(dens.getText().toString())) {
            dens.setError("Content Density cannot be blank");
            return 5;
        }else if(TextUtils.isEmpty(ratio.getText().toString())) {
            ratio.setError("Incident to design pressure ratio cannot be blank.");
            return 6;
        }else if(Double.parseDouble(LAT.getText().toString()) > 0) {
            LAT.setError("Pressure Reference Elevation must be negative");
            return 7;
        } else return 0;
    }

    void storeVals() {
        /**
         * This function is called after errorCheck and is used to store the
         * validated values into the hashMap 'parameters'.
         */
        EditText DP = (EditText) findViewById(R.id.DP_num);
        EditText DT =(EditText) findViewById(R.id.DT_num);
        EditText IT = (EditText) findViewById(R.id.IT_num);
        EditText LAT =(EditText) findViewById(R.id.LAT_num);
        EditText dens = (EditText) findViewById(R.id.Dens_num);
        EditText ratio =(EditText) findViewById(R.id.Ratio_num);

        // read in the units from spinners
        Spinner DPSpin=(Spinner) findViewById(R.id.spin_pressure);
        Spinner DTSpin=(Spinner) findViewById(R.id.spin_DT);
        Spinner ITSpin=(Spinner) findViewById(R.id.spin_IT);
        Spinner LATSpin=(Spinner) findViewById(R.id.spin_LAT);
        Spinner densSpin=(Spinner) findViewById(R.id.spin_Dens);

        // store values into HashMap
        parameters.put("DP", Double.parseDouble(valueOf(DP.getText())));
        parameters.put("DT", Double.parseDouble(valueOf(DT.getText())));
        parameters.put("IT", Double.parseDouble(valueOf(IT.getText())));
        parameters.put("LAT", Double.parseDouble(valueOf(LAT.getText())));
        parameters.put("dens", Double.parseDouble(valueOf(dens.getText())));
        parameters.put("ratio", Double.parseDouble(valueOf(ratio.getText())));

        // store values used in calculation of wall thickness
        storeCalcVals(DP, DT, IT, LAT, dens, DPSpin, DTSpin, ITSpin, LATSpin, densSpin);

    }

    void storeCalcVals(EditText DP, EditText DT, EditText IT, EditText LAT, EditText dens, Spinner DPSpin, Spinner DTSpin, Spinner ITSpin, Spinner LATSpin, Spinner densSpin) {
        // Pressure
        if (Objects.equals(DPSpin.getSelectedItem().toString(), "barg")){
            parameters.put("DPCalc", 0.1 * Double.parseDouble(valueOf(DP.getText())));
            parameters.put("DPUnitSpin", 2.0);
        } else if(Objects.equals(DPSpin.getSelectedItem().toString(), "psi")) {
            parameters.put("DPCalc", 0.00689 * Double.parseDouble(valueOf(DP.getText())));
            parameters.put("DPUnitSpin", 3.0);
        } else {
            parameters.put("DPCalc", Double.parseDouble(valueOf(DP.getText())));
            parameters.put("DPUnitSpin", 1.0);
        }

        // Temperature
        if (Objects.equals(DTSpin.getSelectedItem().toString(), "°F")){
            parameters.put("DTCalc", 5.0 / 9.0 * (Double.parseDouble(valueOf(DT.getText()))-32.0));
            parameters.put("DTUnitSpin", 2.0);
        } else {
            parameters.put("DTUnitSpin", 1.0);
            parameters.put("DTCalc", Double.parseDouble(valueOf(DT.getText())));
        }

        // Installation Temperature
        if (Objects.equals(ITSpin.getSelectedItem().toString(), "°F")){
            parameters.put("ITCalc", 5.0 / 9.0 * (Double.parseDouble(valueOf(IT.getText()))-32.0));
            parameters.put("ITUnitSpin", 2.0);
        } else {
            parameters.put("ITUnitSpin", 1.0);
            parameters.put("ITCalc", Double.parseDouble(valueOf(IT.getText())));
        }

        // Reference Elevation
        if (Objects.equals(LATSpin.getSelectedItem().toString(), "ft")){
            parameters.put("LATUnitSpin", 2.0);
            parameters.put("LATCalc", 0.3048 * Double.parseDouble(valueOf(LAT.getText())));
        } else {
            parameters.put("LATUnitSpin", 1.0);
            parameters.put("LATCalc", Double.parseDouble(valueOf(LAT.getText())));
        }

        // Density
        if (Objects.equals(densSpin.getSelectedItem().toString(), "lb/ft^3")){
            parameters.put("densCalc", 16.0185 * Double.parseDouble(valueOf(dens.getText())));
            parameters.put("densUnitSpin", 2.0);
        } else {
            parameters.put("densUnitSpin", 1.0);
            parameters.put("densCalc", Double.parseDouble(valueOf(dens.getText())));
        }
    }

    public void onProButtonClick(View view) {
        // call errorCheck to determine validity of input
        if(errorCheck()!= 0) {
            return;
        }

        // store values in HashMap
        storeVals();

        // send to next activity.
        Intent intent = new Intent(F101_3.this, F101_4.class);
        startActivity(intent);
    }
}
