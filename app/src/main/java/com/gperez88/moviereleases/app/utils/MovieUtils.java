package com.gperez88.moviereleases.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieUtils {

    public static Date formatDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

//    public static int roundPage(Float value){
//        float decimal = Float.parseFloat(String.format("%.20f\n",value));
//
//        if(decimal > 0) {
//            return value.intValue() + 1;
//        }
//
//        return value.intValue();
//    }
}
