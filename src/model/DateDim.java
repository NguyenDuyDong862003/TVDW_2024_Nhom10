package model;

import java.time.LocalDate;

public class DateDim {
    private int dateSK;
    private LocalDate fullDate;
    private int daySince2005;
    private int monthSince2005;
    private String dayOfWeek;
    private String calendarMonth;
    private int calendarYear;
    private String calendarYearMonth;
    private int dayOfMonth;
    private int dayOfYear;
    private int weekOfYearSunday;
    private String yearWeekSunday;
    private LocalDate weekSundayStart;
    private int weekOfYearMonday;
    private String yearWeekMonday;
    private LocalDate weekMondayStart;
    private String holiday;
    private String dayType;

    public DateDim() {};

    public DateDim(int dateSK, LocalDate fullDate, int daySince2005, int monthSince2005, String dayOfWeek, String calendarMonth, int calendarYear, String calendarYearMonth, int dayOfMonth, int dayOfYear, int weekOfYearSunday, String yearWeekSunday, LocalDate weekSundayStart, int weekOfYearMonday, String yearWeekMonday, LocalDate weekMondayStart, String holiday, String dayType) {
        this.dateSK = dateSK;
        this.fullDate = fullDate;
        this.daySince2005 = daySince2005;
        this.monthSince2005 = monthSince2005;
        this.dayOfWeek = dayOfWeek;
        this.calendarMonth = calendarMonth;
        this.calendarYear = calendarYear;
        this.calendarYearMonth = calendarYearMonth;
        this.dayOfMonth = dayOfMonth;
        this.dayOfYear = dayOfYear;
        this.weekOfYearSunday = weekOfYearSunday;
        this.yearWeekSunday = yearWeekSunday;
        this.weekSundayStart = weekSundayStart;
        this.weekOfYearMonday = weekOfYearMonday;
        this.yearWeekMonday = yearWeekMonday;
        this.weekMondayStart = weekMondayStart;
        this.holiday = holiday;
        this.dayType = dayType;
    }

    public int getDateSK() {
        return dateSK;
    }

    public void setDateSK(int dateSK) {
        this.dateSK = dateSK;
    }

    public LocalDate getFullDate() {
        return fullDate;
    }

    public void setFullDate(LocalDate fullDate) {
        this.fullDate = fullDate;
    }

    public int getDaySince2005() {
        return daySince2005;
    }

    public void setDaySince2005(int daySince2005) {
        this.daySince2005 = daySince2005;
    }

    public int getMonthSince2005() {
        return monthSince2005;
    }

    public void setMonthSince2005(int monthSince2005) {
        this.monthSince2005 = monthSince2005;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getCalendarMonth() {
        return calendarMonth;
    }

    public void setCalendarMonth(String calendarMonth) {
        this.calendarMonth = calendarMonth;
    }

    public int getCalendarYear() {
        return calendarYear;
    }

    public void setCalendarYear(int calendarYear) {
        this.calendarYear = calendarYear;
    }

    public String getCalendarYearMonth() {
        return calendarYearMonth;
    }

    public void setCalendarYearMonth(String calendarYearMonth) {
        this.calendarYearMonth = calendarYearMonth;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public int getWeekOfYearSunday() {
        return weekOfYearSunday;
    }

    public void setWeekOfYearSunday(int weekOfYearSunday) {
        this.weekOfYearSunday = weekOfYearSunday;
    }

    public String getYearWeekSunday() {
        return yearWeekSunday;
    }

    public void setYearWeekSunday(String yearWeekSunday) {
        this.yearWeekSunday = yearWeekSunday;
    }

    public LocalDate getWeekSundayStart() {
        return weekSundayStart;
    }

    public void setWeekSundayStart(LocalDate weekSundayStart) {
        this.weekSundayStart = weekSundayStart;
    }

    public int getWeekOfYearMonday() {
        return weekOfYearMonday;
    }

    public void setWeekOfYearMonday(int weekOfYearMonday) {
        this.weekOfYearMonday = weekOfYearMonday;
    }

    public String getYearWeekMonday() {
        return yearWeekMonday;
    }

    public void setYearWeekMonday(String yearWeekMonday) {
        this.yearWeekMonday = yearWeekMonday;
    }

    public LocalDate getWeekMondayStart() {
        return weekMondayStart;
    }

    public void setWeekMondayStart(LocalDate weekMondayStart) {
        this.weekMondayStart = weekMondayStart;
    }

    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    @Override
    public String toString() {
        return "DateDim{" +
                "dateSK=" + dateSK +
                ", fullDate=" + fullDate +
                ", daySince2005=" + daySince2005 +
                ", monthSince2005=" + monthSince2005 +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", calendarMonth='" + calendarMonth + '\'' +
                ", calendarYear=" + calendarYear +
                ", calendarYearMonth='" + calendarYearMonth + '\'' +
                ", dayOfMonth=" + dayOfMonth +
                ", dayOfYear=" + dayOfYear +
                ", weekOfYearSunday=" + weekOfYearSunday +
                ", yearWeekSunday='" + yearWeekSunday + '\'' +
                ", weekSundayStart=" + weekSundayStart +
                ", weekOfYearMonday=" + weekOfYearMonday +
                ", yearWeekMonday='" + yearWeekMonday + '\'' +
                ", weekMondayStart=" + weekMondayStart +
                ", holiday='" + holiday + '\'' +
                ", dayType='" + dayType + '\'' +
                '}';
    }
}
