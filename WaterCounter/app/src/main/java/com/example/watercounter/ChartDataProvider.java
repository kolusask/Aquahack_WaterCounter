package com.example.watercounter;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;

public class ChartDataProvider {
    enum TimePeriod { YEAR, MONTH, WEEK };

    private static String callMethod(String method) {
        String https_url = "https://aquahackwatercounter.azurewebsites.net/api/" + method;

        URL url;

        try {

            url = new URL(https_url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
            con.setRequestProperty("x-functions-key", "lZ6DfwIn49VQjd8bDTZQo7SUdgauWEOBMoVHdLmqUeYEdcDAUcBaFQ==");
            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(con.getInputStream()));

            String input = br.readLine();
            br.close();

            return input;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static ArrayList collectData(String method) {
        String data = callMethod(method);

        ArrayList valueSet = new ArrayList();

        int i = 0;
        for (String datum : data.split(" ")) {
            Integer value = Integer.valueOf(datum);
            valueSet.add(new BarEntry(value, i));
            i++;
        }

        return valueSet;
    }

    public static ArrayList getDataSet(TimePeriod tp) {
        String method = "";

        switch (tp) {
            case YEAR:
                method = "getLastYear";
                break;
            case MONTH:
                method = "getLastMonth";
                break;
            case WEEK:
                method = "getLastWeek";
                break;
        }

        return collectData(method);
    }

    private static String monthName(int m) {
        String[] monthName = {"January", "February",
                "March", "April", "May", "June", "July",
                "August", "September", "October", "November",
                "December"};

        return monthName[m];
    }

    private static String dayName(int d) {
        String[] dayName = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        return dayName[d];
    }

    public static ArrayList getXAxisValues(int nColumns) {
        ArrayList xAxis = new ArrayList();

        for (int i = 0; i < nColumns; i++)
            xAxis.add(Integer.valueOf(i).toString());
        return xAxis;
//        switch(nColumns) {
//            case 7:
//                int d = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//                for (int i = (d + 1) % 7; i + 1 != d; i = (i + 1) % 7)
//                    xAxis.add(monthName(i));
//                xAxis.add(monthName(d));
//                break;
//            case 12:
//                int m = Calendar.getInstance().get(Calendar.MONTH);
//                xAxis.add(monthName(m));
//                for (int i = (m + 1) % 12; i + 1 != m; i = (i + 1) % 12)
//                    xAxis.add(monthName(i));
//                break;
//            default:
//                for (int i = 0; i < nColumns; i++)
//                    xAxis.add("Week " + Integer.valueOf(i + 1).toString());
//        }
//        return xAxis;
    }
}
