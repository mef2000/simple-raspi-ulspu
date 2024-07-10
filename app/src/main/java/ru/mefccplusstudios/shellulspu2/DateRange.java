package ru.mefccplusstudios.shellulspu2;

import abs.core.Bus;

public class DateRange {
    public int BEGIN_DAY = 0, BEGIN_MONTH = 0, END_DAY = 0, END_MONTH = 0, BEGIN_YEAR = 0, END_YEAR = 0;
    public boolean isRender = true;
    @Override public String toString() {
        return Bus.time.getNormalNumber(BEGIN_DAY)+"."+Bus.time.getNormalNumber(BEGIN_MONTH+1)+" - "+
                Bus.time.getNormalNumber(END_DAY)+"."+Bus.time.getNormalNumber(END_MONTH+1);
    }
}
