package com.coppellcoders.icycle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CarbonActivity extends Fragment {

    private EditText elec;
    private EditText ng;
    private EditText heat;
    private EditText vehi;
    private Button getEmissions;
    private TextView show;
    private TextView breakdown;
    private BarChart bc;
    double elecP = 0, ngP = 0, heatP = 0, vehiP = 0;

    ArrayList<String> x = new ArrayList<>();
    ArrayList<BarEntry> y = new ArrayList<>();
    BarData data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
View mView = inflater.inflate(R.layout.carbon_layout, container, false);
        elec = mView.findViewById(R.id.elec);
        ng = mView.findViewById(R.id.ng);
        heat = mView.findViewById(R.id.heat);
        vehi = mView.findViewById(R.id.veh);
        show = mView.findViewById(R.id.show);
        breakdown = mView.findViewById(R.id.breakdown);
        bc = mView.findViewById(R.id.bargraph);
        bc.setDescription("");
        x.add("Average");
        x.add("You");
        y.add(new BarEntry(36155, 0));
        y.add(new BarEntry(0, 1));
        BarDataSet bds = new BarDataSet(y, "Usage (Lbs CO2)");
        bds.setColors(new int[]{getResources().getColor(R.color.white)});
        BarData bd = new BarData(x, bds);
        bc.setData(bd);
        YAxis lyaxis = bc.getAxisLeft();
        lyaxis.setAxisMinValue(0f);



        elec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(elec.getText().toString().length()==0){
                    elecP = 0;
                }else
                    elecP = getElectricityCarbonFootprint(Double.parseDouble(elec.getText().toString()));
                double total = elecP + ngP + heatP + vehiP;
                total *= 2.20462;
                total = Math.round(total*100)/100.0;
                y.set(y.size()-1, new BarEntry((float)total, 1));
                BarDataSet bds = new BarDataSet(y, "Usage (Lbs CO2)");
                bds.setColors(new int[]{getResources().getColor(R.color.white)});
                BarData bd = new BarData(x, bds);
                bc.setData(bd);
                show.setText("Your Carbon Footprint is: " + total +" kilos of CO2");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ng.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(ng.getText().toString().length()==0){
                    ngP = 0;
                }else
                ngP = getNaturalGasCarbonFootprint(Double.parseDouble(ng.getText().toString()));
                double total = elecP + ngP + heatP + vehiP;
                total *= 2.20462;
                total = Math.round(total*100)/100.0;
                y.set(y.size()-1, new BarEntry((float)total, 1));
                BarDataSet bds = new BarDataSet(y, "Usage (Lbs CO2)");
                bds.setColors(new int[]{getResources().getColor(R.color.white)});
                BarData bd = new BarData(x, bds);
                bc.setData(bd);
                show.setText("Your Carbon Footprint is: " + total +" kilos of CO2");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        heat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(heat.getText().toString().length()==0){
                    heatP = 0;
                }else
                heatP = getElectricityCarbonFootprint(Double.parseDouble(heat.getText().toString()));
                double total = elecP + ngP + heatP + vehiP;
                total *= 2.20462;
                total = Math.round(total*100)/100.0;
                y.set(y.size()-1, new BarEntry((float)total, 1));
                BarDataSet bds = new BarDataSet(y, "Usage (Lbs CO2)");
                bds.setColors(new int[]{getResources().getColor(R.color.white)});
                BarData bd = new BarData(x, bds);
                bc.setData(bd);
                show.setText("Your Carbon Footprint is: " + total +" kilos of CO2");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        vehi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(vehi.getText().toString().length()==0){
                    vehiP = 0;
                }else vehiP = getElectricityCarbonFootprint(Double.parseDouble(vehi.getText().toString()));
                double total = elecP + ngP + heatP + vehiP;
                total *= 2.20462;
                total = Math.round(total*100)/100.0;
                y.set(y.size()-1, new BarEntry((float)total, 1));
                BarDataSet bds = new BarDataSet(y, "Usage (Lbs CO2)");
                bds.setColors(new int[]{getResources().getColor(R.color.white)});
                BarData bd = new BarData(x, bds);
                bc.setData(bd);
                show.setText("Your Carbon Footprint is: " + total +" kilos of CO2");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        breakdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return mView;
    }
    public double getElectricityCarbonFootprint(double cost) {

        double kwhPerYear = 10776;

        double avgElectricityPrice = 1322.52;

        double emissionsFactor = 0.55;

        return (cost / avgElectricityPrice) * kwhPerYear * emissionsFactor;

    }



    // About how much money did you spend on natural gas in the past year?

    public double getNaturalGasCarbonFootprint(double cost) {

        double thermsPerYear = 950;

        double avgThermPrice = 984;

        double emissionsFactor = 5.48;

        return (cost / avgThermPrice) * thermsPerYear * emissionsFactor;

    }



    // About how much money did you spend on heating in the past year?

    public double getHeatingCarbonFootprint(double cost) {

        double litersPerYear = 747.85;

        double avgLiterPrice = 2400;

        double emissionsFactor = 2.678;

        return (cost / avgLiterPrice) * litersPerYear * emissionsFactor;

    }



    // About how much money did you spend on gas for your vehicle(s) in the past year?

    public double getVehicleCarbonFootprint(double cost) {

        double litersPerYear = 1892;

        double avgGasolinePrice = 7.98;

        double emissionsFactor = 2.35;

        return (cost / avgGasolinePrice) * litersPerYear * emissionsFactor;

    }
}
