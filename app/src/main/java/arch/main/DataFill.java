package arch.main;

public class DataFill implements  Comparable<DataFill> {
    public int START_HOUR, END_HOUR, START_MINS, END_MINS, YEAR, MONTH, DAY;
    public String CONTEXT;
    public int TIME_ID;
    @Override
    public int compareTo(DataFill dataFill) {
        return this.START_HOUR- dataFill.START_HOUR;
    }
}
