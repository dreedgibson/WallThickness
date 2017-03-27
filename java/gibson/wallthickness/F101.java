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
import static java.lang.String.*;

public class F101 extends AppCompatActivity {

    // create the parameters hashmap that will store the values to calculate wall thickness
    public static HashMap<String, Double> parameters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f2);
        prePopulate(parameters);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
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
        EditText OD = (EditText) findViewById(R.id.OD_num);
        EditText corrNum =(EditText) findViewById(R.id.corr_num);
        Spinner ODUnit = (Spinner) findViewById(R.id.spin_OD);
        Spinner corrUnit = (Spinner) findViewById(R.id.spin_corr);
        Spinner manProcess = (Spinner) findViewById(R.id.spin_manPro);
        Spinner pipeType = (Spinner) findViewById(R.id.spin_pipeType);
        Spinner matType = (Spinner) findViewById(R.id.spin_matType);

        if(parameters.get("OD") == null){
            OD.setText("");
            corrNum.setText("");
        } else {
            OD.setText(valueOf(parameters.get("OD")));
            corrNum.setText(valueOf(parameters.get("corrA")));
            ODUnit.setSelection((int) (Math.round((Double) parameters.get("ODSpin")) - 1));
            corrUnit.setSelection((int) (Math.round((Double) parameters.get("corrSpin")) - 1));
            manProcess.setSelection((int) (Math.round((Double) parameters.get("manProcess")) - 1));
            pipeType.setSelection((int) (Math.round((Double) parameters.get("pipeType")) - 1));
            matType.setSelection((int) (Math.round((Double) parameters.get("matType")) - 1));
        }
    }

    int errorCheck() {
        /**
         * this function performs all error checking on the values input into
         * this activity.  It takes no input but will set errors on invalid entries.
         */
        EditText OD = (EditText) findViewById(R.id.OD_num);
        EditText corrNum =(EditText) findViewById(R.id.corr_num);

        // read in the value from the OD unit Spinner
        Spinner ODUnit=(Spinner) findViewById(R.id.spin_OD);

        // read in the value from the Corrosion allowance spinner
        Spinner corrAUnit=(Spinner) findViewById(R.id.spin_corr);

        // Throw errors if the user has not input any values into the two num fields
        if(TextUtils.isEmpty(OD.getText().toString())) {
            OD.setError("Outer Diameter cannot be blank.");
            return 1;
        }
        if(TextUtils.isEmpty(corrNum.getText().toString())) {
            corrNum.setError("Corrosion allowance cannot be blank.");
            return 2;
        }

        // error handling for invalid entry in the unit value
        if(Objects.equals(ODUnit.getSelectedItem().toString(), "mm") && Objects.equals(corrAUnit.getSelectedItem().toString(), "mm")) {
            if(Double.parseDouble(OD.getText().toString()) <= (2 * Double.parseDouble(corrNum.getText().toString()))) {
                corrNum.setError("Corrosion allowance cannot be greater than pipe radius");
                return 3;
            }
        } else if(Objects.equals(ODUnit.getSelectedItem().toString(), "in") && Objects.equals(corrAUnit.getSelectedItem().toString(), "mm")) {
            if(25.4 * Double.parseDouble(OD.getText().toString()) <= (2 * Double.parseDouble(corrNum.getText().toString()))) {
                corrNum.setError("Corrosion allowance cannot be greater than pipe radius");
                return 4;
            }
        } else if(Objects.equals(ODUnit.getSelectedItem().toString(), "mm") && Objects.equals(corrAUnit.getSelectedItem().toString(), "in")) {
            if(Double.parseDouble(OD.getText().toString()) <= (2 * 25.4 * Double.parseDouble(corrNum.getText().toString()))) {
                corrNum.setError("Corrosion allowance cannot be greater than pipe radius");
                return 5;
            }
        } else {
            if(Double.parseDouble(OD.getText().toString()) <= (2 * Double.parseDouble(corrNum.getText().toString()))) {
                corrNum.setError("Corrosion allowance cannot be greater than pipe radius");
                return 6;
            }
        }
        return 0;
    }

    void storeVals() {
        /**
         * This function is called after errorCheck and is used to store the
         * validated values into the hashMap 'parameters'.
         */
        EditText OD = (EditText) findViewById(R.id.OD_num);
        EditText corrNum =(EditText) findViewById(R.id.corr_num);

        // read in the Units from the spinners
        Spinner ODUnit=(Spinner) findViewById(R.id.spin_OD);
        Spinner corrAUnit=(Spinner) findViewById(R.id.spin_corr);

        // read in the value of the remaining spinners.
        Spinner manProcess = (Spinner) findViewById(R.id.spin_manPro);
        Spinner pipeType = (Spinner) findViewById(R.id.spin_pipeType);
        Spinner matType = (Spinner) findViewById(R.id.spin_matType);

        // store OD and Corrosion allowance into Hashmap
        parameters.put("OD", Double.parseDouble(valueOf(OD.getText())));
        parameters.put("corrA", Double.parseDouble(valueOf(corrNum.getText())));

        // store values used for calculation of wall thickness
        storeCalcVals(OD, corrNum, ODUnit, corrAUnit);

        // process values and assign to HashMap
        storeSpinVals(manProcess, pipeType, matType);
    }

    void storeCalcVals(EditText OD, EditText corrNum, Spinner ODUnit, Spinner corrAUnit) {

        if (Objects.equals(ODUnit.getSelectedItem().toString(), "mm")){
            parameters.put("ODSpin", 2.0);
            parameters.put("ODCalc", Double.parseDouble(valueOf(OD.getText())));
        } else {
            parameters.put("ODSpin", 1.0);
            parameters.put("ODCalc", 25.4 * Double.parseDouble(valueOf(OD.getText())));
        }
        if (Objects.equals(corrAUnit.getSelectedItem().toString(), "mm")){
            parameters.put("corrSpin", 2.0);
            parameters.put("corrACalc", Double.parseDouble(valueOf(corrNum.getText())));
        } else {
            parameters.put("corrSpin", 1.0);
            parameters.put("corrACalc", 25.4 * Double.parseDouble(valueOf(corrNum.getText())));
        }
    }

    void storeSpinVals(Spinner manProcess, Spinner pipeType, Spinner matType){
        if ((Objects.equals(manProcess.getSelectedItem().toString(), "UOE"))){
            parameters.put("manProcess", 1.0);
        } else if(Objects.equals(manProcess.getSelectedItem().toString(), "Seamless")) {
            parameters.put("manProcess", 3.0);
        } else parameters.put("manProcess", 2.0);

        if ((Objects.equals(pipeType.getSelectedItem().toString(), "SAW"))){
            parameters.put("pipeType", 3.0);
        } else if(Objects.equals(pipeType.getSelectedItem().toString(), "Seamless")) {
            parameters.put("pipeType", 1.0);
        } else parameters.put("PipeType", 2.0);

        if ((Objects.equals(matType.getSelectedItem().toString(), "CS X52"))){
            parameters.put("matType", 1.0);
        } else if(Objects.equals(matType.getSelectedItem().toString(), "CS X60")) {
            parameters.put("matType", 2.0);
        } else if(Objects.equals(matType.getSelectedItem().toString(), "CS X65")) {
            parameters.put("matType", 3.0);
        } else if(Objects.equals(matType.getSelectedItem().toString(), "CS X70")) {
            parameters.put("matType", 4.0);
        } else if(Objects.equals(matType.getSelectedItem().toString(), "22% Cr Duplex")) {
            parameters.put("matType", 5.0);
        } else if(Objects.equals(matType.getSelectedItem().toString(), "15% Cr Super Duplex")) {
            parameters.put("matType", 6.0);
        } else parameters.put("matType", 7.0);
    }

    public void onGeoButtonClick(View view) {

        // call errorCheck to determine validity of input
        if(errorCheck()!= 0) {
            return;
        }

        // store values in HashMap
        storeVals();

        Intent intent = new Intent(F101.this, F101_3.class);
        startActivity(intent);
    }
}
