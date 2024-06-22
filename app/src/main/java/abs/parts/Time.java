package abs.parts;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ru.mefccplusstudios.shellulspu2.DateRange;
import ru.mefccplusstudios.shellulspu2.R;

public class Time {
    private final Calendar cal = Calendar.getInstance();
    public int TOTAL_YEAR, TOTAL_MONTH, TOTAL_DAY, TOTAL_HOURS, TOTAL_MINS, TOTAL_SECS;
    private final Context context;
    public final int HOURS_STEP = 4;
    public final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public Time(Context context) { this.context = context; }
    public String getDayName(int year, int month, int day) {
        cal.set(year, month, day);
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY: return context.getString(R.string.monday);
            case Calendar.TUESDAY: return context.getString(R.string.tueday);
            case Calendar.WEDNESDAY: return context.getString(R.string.weday);
            case Calendar.THURSDAY: return context.getString(R.string.thurday);
            case Calendar.FRIDAY: return context.getString(R.string.friday);
            case Calendar.SATURDAY: return context.getString(R.string.satday);
            case Calendar.SUNDAY: return context.getString(R.string.sunday);
            default: return "Time::Internal Error";
        }
    }
    public String getMonthName(int month) {
        switch(month) {
            case Calendar.JANUARY: return context.getString(R.string.jan);
            case Calendar.FEBRUARY: return context.getString(R.string.feb);
            case Calendar.MARCH: return context.getString(R.string.mar);
            case Calendar.MAY: return context.getString(R.string.may);
            case Calendar.APRIL: return context.getString(R.string.apr);
            case Calendar.JUNE: return context.getString(R.string.jun);
            case Calendar.JULY: return context.getString(R.string.jul);
            case Calendar.AUGUST: return context.getString(R.string.aug);
            case Calendar.SEPTEMBER: return context.getString(R.string.sep);
            case Calendar.OCTOBER: return context.getString(R.string.oct);
            case Calendar.NOVEMBER: return context.getString(R.string.nov);
            case Calendar.DECEMBER: return context.getString(R.string.dec);
            default: return "Time::Internal Error";
        }
    }
    public void actual() {
        Calendar finalcd = Calendar.getInstance();
        TOTAL_YEAR = finalcd.get(Calendar.YEAR);
        TOTAL_MONTH = finalcd.get(Calendar.MONTH);
        TOTAL_DAY = finalcd.get(Calendar.DAY_OF_MONTH);
        TOTAL_HOURS = finalcd.get(Calendar.HOUR_OF_DAY);
        TOTAL_MINS = finalcd.get(Calendar.MINUTE);
        TOTAL_SECS = finalcd.get(Calendar.SECOND);
    }
    public String getNormalNumber(int number) {
        if(number<10) return "0"+number;
        else return  ""+number;
    }
    public DateRange getWeekSlice(int year, int month, int day) {
        DateRange dr = new DateRange();
        cal.set(year, month, day);
        int nday = cal.get(Calendar.DAY_OF_WEEK);
        if(nday == Calendar.SUNDAY) nday = 7;
        else nday--;
        cal.add(Calendar.DAY_OF_MONTH, 1-nday);
        dr.BEGIN_DAY = cal.get(Calendar.DAY_OF_MONTH);
        dr.BEGIN_MONTH = cal.get(Calendar.MONTH);
        dr.BEGIN_YEAR = cal.get(Calendar.YEAR);
        cal.add(Calendar.DAY_OF_MONTH, 6);
        dr.END_DAY = cal.get(Calendar.DAY_OF_MONTH);
        dr.END_MONTH = cal.get(Calendar.MONTH);
        dr.END_YEAR = cal.get(Calendar.YEAR);
        return dr;
    }

    public DateRange[] getMonthSlice(int year, int month) {
        cal.set(year, month, 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        DateRange[] weeks = new DateRange[cal.get(Calendar.WEEK_OF_MONTH)];
        for(int i=0; i<weeks.length; i++) weeks[i] = getWeekSlice(year, month, 1+i*7);
        return weeks;
    }
    public String getSavedWeek() {
        return Bus.time.getNormalNumber(Bus.data.DAY_SBEGIN)+"."+Bus.time.getNormalNumber(Bus.data.MONTH_SBEGIN+1)+" - "+
                Bus.time.getNormalNumber(Bus.data.DAY_SEND)+"."+Bus.time.getNormalNumber(Bus.data.MONTH_SEND+1);
    }

    public String packWithSlash(int year, int month, int day) {
        return getNormalNumber(day)+"/"+getNormalNumber(month+1)+"/"+year;
    }
}
