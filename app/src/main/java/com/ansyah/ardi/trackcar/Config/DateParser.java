package com.ansyah.ardi.trackcar.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ardi on 08/05/18.
 */

public class DateParser {
    public static String parseDateToDayDateMonthYear(String date){
        SimpleDateFormat sourcePatternDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat targetPatternDate = new SimpleDateFormat("EEE, dd MMMM yyyy HH.mm.ss");

        String targetDate;

        Date sourceDate = parseToDate(date);
        targetDate = targetPatternDate.format(sourceDate);
        return targetDate;
    }

    public static Date parseToDate(String date){
        SimpleDateFormat sourcePatternDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date sourceDate;
        try {
            sourceDate = sourcePatternDate.parse(date);
            sourceDate.setTime(sourceDate.getTime()+25200000);
        } catch (ParseException e){
            e.printStackTrace();
            sourceDate = null;
        }
        return sourceDate;
    }
}
