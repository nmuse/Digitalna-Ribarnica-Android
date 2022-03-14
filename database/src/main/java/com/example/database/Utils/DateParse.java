package com.example.database.Utils;

import java.util.Calendar;
import java.util.Date;

public class DateParse {
    public static Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }
    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }
}
