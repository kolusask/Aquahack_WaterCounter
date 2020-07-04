package com.example.watercounter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    double balance;
    BarChart chart;
    ChartDataProvider.TimePeriod timePeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (BarChart) findViewById(R.id.chart);

        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.rbLastYear: updateChart(ChartDataProvider.TimePeriod.YEAR);
                    break;
                    case R.id.rbLastMonth: updateChart(ChartDataProvider.TimePeriod.MONTH);
                    break;
                    case R.id.rbLastWeek: updateChart(ChartDataProvider.TimePeriod.WEEK);
                    break;
                }
            }
        };

        RadioButton rb = (RadioButton) findViewById(R.id.rbLastYear);
        rb.setOnClickListener(ocl);

        rb = (RadioButton) findViewById(R.id.rbLastMonth);
        rb.setOnClickListener(ocl);

        rb = (RadioButton) findViewById(R.id.rbLastWeek);
        rb.setOnClickListener(ocl);

        rb.callOnClick();

        loadText();

        Button btnCharge = findViewById(R.id.btnCharge);
        Button btnClear = findViewById(R.id.btnClear);

        btnCharge.setOnClickListener(this);
        btnClear.setOnClickListener(this);

    }

    private class ATask extends AsyncTask<BarChart, String, ArrayList> {
        BarChart chart;

        @Override
        protected ArrayList doInBackground(BarChart... barCharts) {
            chart = barCharts[0];
            return ChartDataProvider.getDataSet(timePeriod);
        }

        @Override
        protected void onPostExecute(ArrayList valueSet) {
            super.onPostExecute(valueSet);
            BarDataSet barDataSet = new BarDataSet(valueSet, "");
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

            ArrayList dataSets = new ArrayList();
            dataSets.add(barDataSet);
            chart.setData(new BarData(ChartDataProvider.getXAxisValues(valueSet.size()), dataSets));

        }
    }

    protected void updateChart(ChartDataProvider.TimePeriod tp) {
        timePeriod = tp;

        new ATask().execute(chart);
        chart.animateXY(2000, 2000);
        chart.invalidate();

        switch (tp) {
            case YEAR:
                chart.setDescription("Monthly water consumption for last year [m^3]");
                break;
            case MONTH:
                chart.setDescription("Weekly water consumption for last year [m^3]");
                break;
            case WEEK:
                chart.setDescription("Daily water consumption for last year [m^3]");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void changeBalance(double by) {
        balance += by;
        TextView twBalance = (TextView)findViewById(R.id.textViewBalance);
        twBalance.setText(Double.valueOf(balance).toString());
        saveText();
    }

    void changeBalance(String by) {
        try {
            changeBalance(Double.valueOf(by).doubleValue());
        } catch (Exception e) {
            changeBalance(0);
        }
    }

    void saveText() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = preferences.edit();
        String text = Double.valueOf(balance).toString();
        ed.putString("BALANCE", text);
        ed.commit();
    }

    void loadText() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String savedText = preferences.getString("BALANCE", "0");
        if(!savedText.isEmpty())
            changeBalance(savedText);
    }



    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnClear:
                changeBalance(-balance);
                saveText();
                break;
            case R.id.btnCharge:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Charge by:");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeBalance(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            default: break;
        }
    }
}