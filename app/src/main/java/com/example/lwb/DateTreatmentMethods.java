package com.example.lwb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTreatmentMethods {
    public static boolean checkRelevanceOfTime(Date dateOfEvent) {

        if (dateOfEvent.before(new Date()))
            return false;
        else return true;
    }

    public static Date getDateFromStrings(String time, String date){
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy HH:mm");
        Date docDate= null;
        String s=date+" "+time;
        try {
            docDate = format.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return docDate;
    }
}
