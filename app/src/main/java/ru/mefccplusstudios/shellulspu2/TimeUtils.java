package ru.mefccplusstudios.shellulspu2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    private static Calendar finalcd;
    private static final Calendar dyncd = Calendar.getInstance();
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static int TOTAL_YEAR = 0, TOTAL_MONTH = 0, TOTAL_DAY=0, TOTAL_HOURS=0, TOTAL_MINS=0, TOTAL_SECS=0;

    public static int FOCUS_YEAR = 0, FOCUS_MONTH = 0;
    public static final int HOURS_STEP = 4;
    public static final ArrayList<DateRange> drangers = new ArrayList<>();

    public static int SAVED_YEAR, SAVED_MONTH, SAVED_WEEK;

    public static void init() {
        finalcd = Calendar.getInstance();
        TOTAL_YEAR = finalcd.get(Calendar.YEAR);
        TOTAL_MONTH = finalcd.get(Calendar.MONTH);
        TOTAL_DAY = finalcd.get(Calendar.DAY_OF_MONTH);
        TOTAL_HOURS = finalcd.get(Calendar.HOUR_OF_DAY);
        TOTAL_MINS = finalcd.get(Calendar.MINUTE);
        TOTAL_SECS = finalcd.get(Calendar.SECOND);
        FOCUS_YEAR = TOTAL_YEAR; FOCUS_MONTH = TOTAL_MONTH;

        //if(TOTAL_MONTH<9)
        //cd.set(TOTAL_YEAR, 8, 1);
        //int NW1 = cd.get(Calendar.WEEK_OF_YEAR);
       // System.out.println(NW1);
    }
    public static String getNamedCurrentDayOfWeek() {
        if(finalcd==null) return "TU_ERROR";
        String nday = "NOT_ATTRIBUTE";
        switch(finalcd.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                nday = "Понедельник";
                break;
            case Calendar.TUESDAY:
                nday = "Вторник";
                break;
            case Calendar.WEDNESDAY:
                nday = "Среда";
                break;
            case Calendar.THURSDAY:
                nday = "Четверг";
                break;
            case Calendar.FRIDAY:
                nday = "Пятница";
                break;
            case Calendar.SATURDAY:
                nday = "Суббота";
                break;
            case Calendar.SUNDAY:
                nday = "Воскресенье";
                break;
        }
        return nday;
    }

    public static String getNamedDayOfWeekBy(int year, int month, int day) {
        if(dyncd==null) return "TU_ERROR";
        String nday = "NOT_ATTRIBUTE";
        dyncd.set(year, month, day);
        switch(dyncd.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                nday = "Понедельник";
                break;
            case Calendar.TUESDAY:
                nday = "Вторник";
                break;
            case Calendar.WEDNESDAY:
                nday = "Среда";
                break;
            case Calendar.THURSDAY:
                nday = "Четверг";
                break;
            case Calendar.FRIDAY:
                nday = "Пятница";
                break;
            case Calendar.SATURDAY:
                nday = "Суббота";
                break;
            case Calendar.SUNDAY:
                nday = "Воскресенье";
                break;
        }
        return nday;
    }

    public static String getCurrentData() {
        String sa = "";
        if(TOTAL_DAY<10) sa = "0"+TOTAL_DAY;
        else sa = ""+TOTAL_DAY;
        if(TOTAL_MONTH<10) sa = sa+"/0"+TOTAL_MONTH;
        else sa = sa+"/"+TOTAL_MONTH;
        sa = sa+"/"+TOTAL_YEAR;
        return sa;
    }
    public static void getYMApplyBy(int month_amount) {
        if(FOCUS_YEAR == 0) { FOCUS_YEAR = TOTAL_YEAR; FOCUS_MONTH = TOTAL_MONTH;}
        dyncd.set(FOCUS_YEAR, FOCUS_MONTH, 1);
        dyncd.add(Calendar.MONTH, month_amount);
        FOCUS_YEAR = dyncd.get(Calendar.YEAR);
        FOCUS_MONTH = dyncd.get(Calendar.MONTH);
    }
    public static String getNamedMonth(int month) {
        String data = "N/A";
        switch(month) {
            case Calendar.JANUARY: data = "Январь"; break;
            case Calendar.FEBRUARY: data = "Февраль"; break;
            case Calendar.MARCH: data = "Март"; break;
            case Calendar.MAY: data = "Май"; break;
            case Calendar.APRIL: data = "Апрель"; break;
            case Calendar.JUNE: data = "Июнь"; break;
            case Calendar.JULY: data = "Июль"; break;
            case Calendar.SEPTEMBER: data = "Сентябрь"; break;
            case Calendar.OCTOBER: data = "Октябрь"; break;
            case Calendar.NOVEMBER: data = "Ноябрь"; break;
            case Calendar.DECEMBER: data = "Декабрь"; break;
            case Calendar.AUGUST: data = "Август"; break;
        }
        return data;
    }
    public static void pullDateRangers(int MONTH, int YEAR) {
        drangers.clear();
       // dyncd.clear();
        dyncd.set(YEAR, MONTH, 1);
        int startday = dyncd.get(Calendar.DAY_OF_WEEK);
        if(startday==Calendar.SUNDAY) startday = 6;
        else startday -= 2;
        dyncd.add(Calendar.DATE, -startday);
        int PREVIOSLY_MONTH = MONTH;
        while(true) {
            DateRange dr = new DateRange();
            dr.BEGIN_DAY = dyncd.get(Calendar.DAY_OF_MONTH);
            dr.BEGIN_MONTH = dyncd.get(Calendar.MONTH);
            dr.BEGIN_YEAR = dyncd.get(Calendar.YEAR);
            dyncd.add(Calendar.DATE, 6);
            dr.END_DAY = dyncd.get(Calendar.DAY_OF_MONTH);
            dr.END_MONTH = dyncd.get(Calendar.MONTH);
            dr.END_YEAR = dyncd.get(Calendar.YEAR);
            drangers.add(dr);
            dyncd.add(Calendar.DATE, 1);
            if(dr.END_MONTH!=PREVIOSLY_MONTH) break;
            PREVIOSLY_MONTH = dr.END_MONTH;
        }
    }
    public static String getNormalNumber(int number) {
        if(number<10) return "0"+number;
        else return  ""+number;
    }
    public static String buildFromSaved() {
        if(drangers.size()>SAVED_WEEK) {
            DateRange dr = TimeUtils.drangers.get(SAVED_WEEK);
            return getNormalNumber(dr.BEGIN_DAY)+"."+getNormalNumber(dr.BEGIN_MONTH+1)
                    +" - "+getNormalNumber(dr.END_DAY)+"."+getNormalNumber(dr.END_MONTH+1);

        }else return "internal_error";
    }
    public static String buildFromScratch(int position) {
        if(drangers.size()>position) {
            DateRange dr = TimeUtils.drangers.get(position);
            return getNormalNumber(dr.BEGIN_DAY)+"."+getNormalNumber(dr.BEGIN_MONTH+1)
                    +" - "+getNormalNumber(dr.END_DAY)+"."+getNormalNumber(dr.END_MONTH+1);
        }else return "internal_error";
    }
}
