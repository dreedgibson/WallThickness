package gibson.wallthickness;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Objects;

import static gibson.wallthickness.F101.parameters;
import static java.lang.String.valueOf;

public class F101_5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f101_5);
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

    void prePopulate(HashMap parameters) {
        /**
         * function to prepopulate the fields based on the values in the parameters hashmap
         */
        EditText nomT = (EditText) findViewById(R.id.WTN);
        Spinner nomTUnit = (Spinner) findViewById(R.id.WTNSpin);

        if(parameters.get("nomT") == null) {
            nomT.setText("");
        } else {
            nomT.setText(valueOf(parameters.get("nomT")));
        }
    }

    int errorCheck() {
        /**
         * this function performs all error checking on the values input into
         * this activity.  It takes no input but will set errors on invalid entries.
         */
        EditText nomT = (EditText) findViewById(R.id.WTN);
        Spinner nomTUnit = (Spinner) findViewById(R.id.WTNSpin);

        if(TextUtils.isEmpty(nomT.getText().toString())) {
            nomT.setError("Selected wall thickness cannot be blank");
            return 1;
        }
        if(Objects.equals(nomTUnit.getSelectedItem().toString(), "mm")) {
            if (Double.parseDouble(nomT.getText().toString()) >= (parameters.get("ODCalc") - 2 * parameters.get("corrACalc")) / 2) {
                nomT.setError("Selected wall thickness cannot be greater than OD - corrosion allowance");
                return 2;
            }
        }
        if(Objects.equals(nomTUnit.getSelectedItem().toString(), "in")) {
            if (25.4 * Double.parseDouble(nomT.getText().toString()) >= (parameters.get("ODCalc") - 2 * parameters.get("corrACalc")) / 2) {
                nomT.setError("Selected wall thickness cannot be greater than OD - corrosion allowance");
                return 3;
            }
        }

        if (Double.parseDouble(nomT.getText().toString()) < 0) {
            nomT.setError("Selected wall thickness cannot be less than 0");
            return 4;
        }

        return 0;
    }

    void storeVals() {
        /**
         * This function is called after errorCheck and is used to store the
         * validated values into the hashMap 'parameters'.
         */
        EditText nomT = (EditText) findViewById(R.id.WTN);
        Spinner nomTUnit = (Spinner) findViewById(R.id.WTNSpin);

        parameters.put("nomT", Double.parseDouble(valueOf(nomT.getText())));

        if (Objects.equals(nomTUnit.getSelectedItem().toString(), "in")) {
            parameters.put("nomTUnit", 2.0);
            parameters.put("nomTCalc", 25.4 * Double.parseDouble(valueOf(nomT.getText())));
        } else {
            parameters.put("nomTUnit", 1.0);
            parameters.put("nomTCalc", Double.parseDouble(valueOf(nomT.getText())));
        }
    }

    void materialProperties() {
        /**
         * This function will calculate the material properties needed based on the previous
         * input screens
         */
        double[] SMYSArray = {358, 413, 448, 482, 450, 550, 550};
        double[] SMTSArray = {455, 517, 530, 565, 620, 750, 700};

        int matType = (int) (Math.round(parameters.get("matType")) - 1);

        parameters.put("SMYS", SMYSArray[matType]);
        parameters.put("SMTS", SMTSArray[matType]);

        if (matType + 1 <= 2) {
            parameters.put("suppReq", 2.0);
        } else parameters.put("suppReq", 1.0);

        int matVar = 0;

        if (matType <= 4) {
            matVar = 0;
        } else if (matType == 5) {
            matVar = 1;
        } else if (matType == 6) {
            matVar = 2;
        } else matVar = 3;

        double[] poissonArray = {0.3, 0.3, 0.3, 0.27};
        double[] steelDens = {7850, 7800, 7800, 7690};

        parameters.put("poisson", poissonArray[matVar]);
        parameters.put("steelDens", steelDens[matVar]);

        // get the SMYS and SMTS at design temp, thermal coeff of exp, and the young modulus
        deRateAlphaYM(matVar);

        double alphah_i;
        if(matVar == 0){
            alphah_i = 0.93;
        } else alphah_i = 0.92;

        parameters.put("alphah_i", alphah_i);
    }

    void manToleranceClass() {
        int pipeType = (int) (Math.round(parameters.get("pipeType")));
        double nomT = parameters.get("nomTCalc");
        double OD = parameters.get("ODCalc");
        int fluidClass = (int) (Math.round(parameters.get("fluidClass")));
        int locationClass = (int) (Math.round(parameters.get("locationClass")));

        double mTolNeg;
        if (pipeType == 1) {
            if(nomT < 4) {
                mTolNeg = 0.5;
            } else if (nomT >= 4 && nomT < 25) {
                mTolNeg = 0.125 * nomT;
            } else if (nomT >= 25 && nomT < 30) {
                mTolNeg = 3;
            } else mTolNeg = 0.1 * nomT;
        } else if(pipeType == 2){
            if (nomT <= 6) {
                mTolNeg = 0.4;
            } else if (nomT > 15) {
                mTolNeg = 1.0;
            } else mTolNeg = 0.7;
        } else {
            if (nomT <= 6) {
                mTolNeg = 0.5;
            } else if (nomT > 6 && nomT <= 10) {
                mTolNeg = 0.7;
            } else mTolNeg = 1.0;
        }
        double mtol = mTolNeg/nomT;
        double f0;
        if (OD < 610) {
            f0 = 0.015;
        } else {
            if (0.01 * OD < 10) {
                f0 = 0.01;
            } else f0 = 10 / OD;
        }
        int classOperation, classLeak, classInstall;
        if (fluidClass == 1 || fluidClass == 3){
            if (locationClass == 1){
                classOperation = 1;
            } else if (locationClass == 2) {
                classOperation = 2;
            } else classOperation = 3;
        } else {
            if (locationClass == 1){
                classOperation = 2;
            } else classOperation = 3;
        }
        classLeak = 1;
        classInstall = 1;

        parameters.put("mTol", mtol);
        parameters.put("f0", f0);
        parameters.put("classOperation", (double) classOperation);
        parameters.put("classLeak", (double) classLeak);
        parameters.put("classInstall", (double) classInstall);
    }

    void loadAndResistanceFactors() {
        /**
         * This function calculates the load and resistance factors based on DNV OS F101
         *
         */
        int process = (int) (Math.round(parameters.get("manProcess")));
        int supReq = (int) (Math.round(parameters.get("suppReq")));
        int classOperation = (int) (Math.round(parameters.get("classOperation")));
        int classOther = (int) (Math.round(parameters.get("classInstall")));
        double[] gammaT = {1.03, 1.05, 1.05};

        double gammaMFLS = 1.0;
        double gammaMOther = 1.15;

        double gammaTOp = gammaT[classOperation - 1];
        double gammaTOther = gammaT[classOther - 1];

        double alphaFab;
        double alphaU;

        if (process == 3) {
            alphaFab = 1.0;
        } else if (process == 2) {
            alphaFab = 0.93;
        } else alphaFab = 0.85;

        if (supReq == 1) {
            alphaU = 1.0;
        } else alphaU = 0.96;

        parameters.put("gammaMFLS", gammaMFLS);
        parameters.put("gammaMOther", gammaMOther);
        parameters.put("gammaTOp", gammaTOp);
        parameters.put("gammaTOther", gammaTOther);
        parameters.put("alphaFab", alphaFab);
        parameters.put("alphaU", alphaU);

    }

    void calculateProps() {

        materialProperties();

        manToleranceClass();

        loadAndResistanceFactors();
    }

    void pressureCalc() {
        /**
         * This function will calculate pressure in pipeline that wt needs to withstand
         */

        double designPressure = parameters.get("DPCalc"); // MPa
        double gravity = 9.81; // m/s^2
        double contentDensity = parameters.get("densCalc"); // kg/m^3
        double datum = parameters.get("LATCalc"); // m
        double maxWaterDepth = parameters.get("WDmaxCalc"); // m
        double minWaterDepth = parameters.get("WDminCalc"); // m
        double waterDensity = parameters.get("wdensCalc"); // kg/m^3
        double tide = parameters.get("hTideCalc"); // m
        double hMax = parameters.get("hMaxCalc"); // m
        double incToDesign = parameters.get("ratio"); // unitless
        double gammaTOp = parameters.get("gammaTOp"); // unitless

        // maximum local internal design pressure in MPa
        double PldMax = Math.max(Math.max(designPressure,designPressure + (contentDensity*gravity/1000000 * (maxWaterDepth + datum))),designPressure + (contentDensity*gravity/1000000 * (minWaterDepth + datum)));

        // maximum external pressure for buckling and collapse in MPa
        double peMax = waterDensity * gravity / 1000000 *(maxWaterDepth + tide + hMax / 2);

        // maximum local incidental pressure differential
        double delta_PLLI_max = Math.max(Math.max(designPressure*incToDesign + waterDensity * gravity / 1000000 * datum, designPressure * incToDesign + contentDensity * gravity / 1000000 * (maxWaterDepth + datum) - waterDensity * gravity / 1000000 * maxWaterDepth), designPressure * incToDesign + contentDensity * gravity / 1000000 * (minWaterDepth + datum) - waterDensity * gravity / 1000000 * minWaterDepth);

        // maximum local incidental pressure differential hydrotest
        double delta_PLT_max = Math.max(Math.max(gammaTOp * designPressure*incToDesign + waterDensity * gravity / 1000000 * datum, gammaTOp * (designPressure * incToDesign + contentDensity * gravity / 1000000 * (maxWaterDepth + datum)) - waterDensity * gravity / 1000000 * maxWaterDepth), gammaTOp * (designPressure * incToDesign + contentDensity * gravity / 1000000 * (minWaterDepth + datum)) - waterDensity * gravity / 1000000 * minWaterDepth);

        parameters.put("PldMax", PldMax);
        parameters.put("peMax", peMax);
        parameters.put("delta_PLLI", delta_PLLI_max);
        parameters.put("delta_PLT", delta_PLT_max);

    }

    double burstCalc(double fy, double fu, double deltaPLL, double gammaSC) {
        /**
         * This function calculates the minimum wall thickness required for the burst condition
         */
        double gammaM = parameters.get("gammaMOther");
        double fcb = Math.min(fu/1.15, fy);
        double OD = parameters.get("ODCalc");

        double t = 0;

        double pb = ((2*t)/(OD-t))*fcb*2/(Math.sqrt(3.0));
        double checkValue = deltaPLL * gammaSC * gammaM;

        while (Math.abs(pb - checkValue) > 0.01) {
            t += 0.001;
            pb = ((2*t)/(OD-t))*fcb*2/(Math.sqrt(3.0));
        }
        return t;
    }

    double collapseCalc(double fy, double peMax, double gammaSC) {
        /**
         * this function will calculate the minimum wall thickness required for the collapse condition
         */

        // plastic collapse pressure
        double P_c = parameters.get("gammaMOther") * gammaSC * peMax;
        double OD = parameters.get("ODCalc");

        double t = OD / 3; // need large initial guess and work down

        double P_p = fy * parameters.get("alphaFab") * 2 * t / OD;

        double P_el = ((2 * parameters.get("YM") * 1000) * Math.pow((t / OD), 3)) / (1 - Math.pow(parameters.get("poisson"),2));

        double Check1 = ((P_c - P_el) * (Math.pow(P_c, 2) - Math.pow(P_p, 2))) / Math.pow(P_c, 3);
        double Check2 = (P_c * P_el * P_p * parameters.get("f0") * (OD / t)) / Math.pow(P_c, 3);

        while (Math.abs(Check1 - Check2) > .01) {
            t -= 0.001;
            P_p = fy * parameters.get("alphaFab") * 2 * t / OD;
            P_el = ((2 * parameters.get("YM") * 1000) * Math.pow((t / OD), 3)) / (1 - Math.pow(parameters.get("poisson"),2));
            Check1 = ((P_c - P_el) * (Math.pow(P_c, 2) - Math.pow(P_p, 2))) / Math.pow(P_c, 3);
            Check2 = (P_c * P_el * P_p * parameters.get("f0") * (OD / t)) / Math.pow(P_c, 3);
        }

        return t;
    }

    void wallThicknessCalc(){
        /**
         * this function will control wall thickness calcs and store all values for the various wall thickness
         * calculations
         */
        double[] gammaSC = {1.046, 1.138, 1.308, 1.04, 1.14, 1.26};
        double fyOp = parameters.get("SMYSDT")*parameters.get("alphaU");
        double fuOp = parameters.get("SMTSDT")*parameters.get("alphaU");
        double fyIns = parameters.get("SMYS")*parameters.get("alphaU");
        double fuIns = parameters.get("SMTS")*parameters.get("alphaU");
        int classOperation = (int) (Math.round(parameters.get("classOperation")));
        int classOther = (int) (Math.round(parameters.get("classInstall")));

        // get the value for operation burst wall thickness requirement before corrosion and man tolerance
        double t_op = burstCalc(fyOp,fuOp,parameters.get("delta_PLLI"),gammaSC[classOperation-1]);

        // get the value for operation burst wall thickness requirement before man tolerance
        double t_ins = burstCalc(fyIns,fuIns,parameters.get("delta_PLT"),gammaSC[classOther-1]);

        double t_coll_ins = collapseCalc(fyIns, parameters.get("peMax"), gammaSC[classOther - 1 + 3]);

        double t_coll_depr = collapseCalc(fyOp, parameters.get("peMax"), gammaSC[classOperation - 1 + 3]);

        double t_op_1 = (t_op + parameters.get("corrACalc"))/(1 - parameters.get("mTol"));

        double t_ins_1 = t_ins / (1 - parameters.get("mTol"));

        double t_coll_depr_1 = (t_coll_depr + parameters.get("corrACalc"))/(1 - parameters.get("mTol"));

        double t_coll_ins_1 = t_coll_ins / (1 - parameters.get("mTol"));

        double t_prop_inst = parameters.get("ODCalc") * Math.pow((parameters.get("peMax") * parameters.get("gammaMOther") * gammaSC[classOther - 1 + 3]) / (35 * fyIns * parameters.get("alphaFab")),1/2.5);

        double t_prop_depr = parameters.get("ODCalc") * Math.pow((parameters.get("peMax") * parameters.get("gammaMOther") * gammaSC[classOperation - 1 + 3]) / (35 * fyOp * parameters.get("alphaFab")),1/2.5) + parameters.get("corrACalc");

        parameters.put("pContainT", Math.max(t_op_1, t_ins_1));
        parameters.put("pCollapseT", Math.max(t_coll_depr_1, t_coll_ins_1));
        parameters.put("pBuckleT", Math.max(t_prop_depr,t_prop_inst));
    }

    public void onWTButtonClick(View view) {

        if(errorCheck()!= 0) {
            return;
        }

        RelativeLayout F101 = (RelativeLayout) findViewById(R.id.activity_f101_5);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        storeVals();

        calculateProps();

        pressureCalc();

        wallThicknessCalc();

        TextView tv = new TextView(this);
        DecimalFormat df = new DecimalFormat("#.#");
        df.setMaximumFractionDigits(2);
        double s1 = parameters.get("pContainT");
        double s2 = parameters.get("pCollapseT");
        double s3 = parameters.get("pBuckleT");
        tv.setText(getString(R.string.report, s1, s2, s3));
        params1.addRule(RelativeLayout.BELOW, R.id.WTbutton);
        F101.addView(tv, params1);
    }

    void deRateAlphaYM(int matVar) {
        /**
         * stores the values of fy alpha, and Youngs modulus in the parameters hashmap
         */
        double DT = parameters.get("DTCalc");

        // define arrays for the material properties
        int[] tempTable = {0, 20, 50, 100, 150, 200};
        double[] deRating = {0, 0, 0, 30, 50, 70, 0, 0, 40, 90, 120, 140, 0, 0, 40, 90, 120, 140, 0, 0, 0, 40, 60, 75};
        double[] youngMod = {207, 207, 207, 207, 207, 207, 200, 200, 198, 194, 190, 186, 200, 200, 198, 194, 190, 186, 201, 201, 201, 199, 195.5, 192};
        double[] alphaArray = {11.7, 11.7, 11.7, 11.7, 11.7, 11.7, 13.0, 13.0, 13.0, 13.0, 13.5, 13.5, 13.5, 13.5, 13.5, 13.5, 14.0, 14.0, 10.5, 10.5, 10.5, 10.9, 10.8, 11};

        //linear interpolation numbers
        double x1 = 0;
        double x2 = 0;
        double y1de = 0;
        double y2de = 0;
        double y1ym = 0;
        double y2ym = 0;
        double y1al = 0;
        double y2al = 0;

        // get linear interpolation numbers for material type
        for(int i = 0; i < tempTable.length; i++) {
            if(DT > 200) {
                x1 = 200;
                x2 = 200;
                break;
            } else if(DT >= tempTable[i] && DT <= tempTable[i+1]){
                x1 = tempTable[i];
                x2 = tempTable[i+1];
                break;
            }
        }
        if(matVar == 0){
            for(int i = 0; i < 6; i++){
                if(DT > 200){
                    y1de = deRating[5];
                    y2de = deRating[5];
                    y1ym = youngMod[5];
                    y2ym = youngMod[5];
                    y1al = alphaArray[5];
                    y2al = alphaArray[5];
                    break;
                }
                else if(DT >= tempTable[i] && DT <= tempTable[i+1]) {
                    y1de = deRating[i];
                    y2de = deRating[i+1];
                    y1ym = youngMod[i];
                    y2ym = youngMod[i+1];
                    y1al = alphaArray[i];
                    y2al = alphaArray[i+1];
                    break;
                }
            }
        } else if(matVar == 1) {
            for(int i = 6; i < 12; i++){
                if(DT > 200){
                    y1de = deRating[11];
                    y2de = deRating[11];
                    y1ym = youngMod[11];
                    y2ym = youngMod[11];
                    y1al = alphaArray[11];
                    y2al = alphaArray[11];
                    break;
                }
                else if(DT >= tempTable[i-6] && DT <= tempTable[i+1-6]) {
                    y1de = deRating[i];
                    y2de = deRating[i+1];
                    y1ym = youngMod[i];
                    y2ym = youngMod[i+1];
                    y1al = alphaArray[i];
                    y2al = alphaArray[i+1];
                    break;
                }
            }
        } else if(matVar == 2) {
            for (int i = 12; i < 18; i++) {
                if (DT > 200) {
                    y1de = deRating[17];
                    y2de = deRating[17];
                    y1ym = youngMod[17];
                    y2ym = youngMod[17];
                    y1al = alphaArray[17];
                    y2al = alphaArray[17];
                    break;
                } else if (DT >= tempTable[i - 12] && DT <= tempTable[i + 1 - 12]) {
                    y1de = deRating[i];
                    y2de = deRating[i + 1];
                    y1ym = youngMod[i];
                    y2ym = youngMod[i+1];
                    y1al = alphaArray[i];
                    y2al = alphaArray[i+1];
                    break;
                }
            }
        } else {
            for(int i = 18; i < 24; i++){
                if(DT > 200){
                    y1de = deRating[23];
                    y2de = deRating[23];
                    y1ym = youngMod[23];
                    y2ym = youngMod[23];
                    y1al = alphaArray[23];
                    y2al = alphaArray[23];
                    break;
                }
                else if(DT >= tempTable[i -24] && DT <= tempTable[i+1 -24]) {
                    y1de = deRating[i];
                    y2de = deRating[i+1];
                    y1ym = youngMod[i];
                    y2ym = youngMod[i+1];
                    y1al = alphaArray[i];
                    y2al = alphaArray[i+1];

                    break;
                }
            }
        }
        double deRate = 0;
        double youngModulus = 0;
        double alpha = 0;

        // interpolate and store into parameters hashmap
        if(DT > 200) {
            deRate = linInterpol(200, x1, x2, y1de, y2de);
            youngModulus = linInterpol(200, x1, x2, y1ym, y2ym);
            alpha = linInterpol(200, x1, x2, y1al, y2al);
        } else {
            deRate = linInterpol(DT, x1, x2, y1de, y2de);
            youngModulus = linInterpol(DT, x1, x2, y1ym, y2ym);
            alpha = linInterpol(DT, x1, x2, y1al, y2al);
        }
        double SMYS = parameters.get("SMYS");
        double SMTS = parameters.get("SMTS");
        parameters.put("SMYSDT", SMYS - deRate);
        parameters.put("SMTSDT", SMTS - deRate);
        parameters.put("YM", youngModulus);
        parameters.put("alpha", alpha / 1000000);
    }

    double linInterpol(double x, double x1, double x2, double y1, double y2) {
        /**
         * linear interpolation
         */
        return y1 + (x - x1) * ((y2 - y1)/(x2 - x1));
    }
}
