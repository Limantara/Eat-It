package me.limantara.eatit.Helper;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class TimeHelper {

    private TimeHelper() {

    }

    /**
     * Return a java.util.Calendar object that has been
     * initialized with the current date and time
     *
     * @return
     */
    public static Calendar now() {
        return GregorianCalendar.getInstance();
    }

    /**
     * Return a java.util.Calendar object that has been initialized
     * with breakfast time (5:00AM the current day or tomorrow).
     *
     * @return
     */
    public static Calendar breakfast() {
        return createTodayAt(5, 0, 0, 0); // 5:00AM
    }

    /**
     * Return a java.util.Calendar object that has been initialized
     * with lunch time at noon of the current day.
     *
     * @return
     */
    public static Calendar lunch() {
        return createTodayAt(12, 0, 0, 0); // 12:00PM
    }

    /**
     * Return a java.util.Calendar object that has been initialized
     * with dinner time 5:00PM of the current day.
     *
     * @return
     */
    public static Calendar dinner() {
        return createTodayAt(17, 0, 0, 0); // 5:00PM
    }

    /**
     * Return the String representation of the current eat time.
     *
     * @return
     */
    public static String getEatTime() {
        String text = "";

        if(TimeHelper.now().after(TimeHelper.dinner()) ||
                TimeHelper.now().before(TimeHelper.breakfast())) {
            text = "dinner";
        }
        else if(TimeHelper.now().after(TimeHelper.lunch())) {
            text = "lunch";
        }
        else if(TimeHelper.now().after(TimeHelper.breakfast())){
            text = "breakfast";
        }

        return text;
    }

    /**
     * Return the current time in a Calendar object representation
     *
     * @return
     */
    public static Calendar getEatTimeObject() {
        switch(getEatTime()) {
            case "dinner":
                return dinner();
            case "lunch":
                return lunch();
            case "breakfast":
                return breakfast();
            default:
                return dinner();
        }
    }

    /**
     * Return the String representation of the next eat time.
     *
     * @return
     */
    public static String getNextEatTime() {
        switch(getEatTime()) {
            case "dinner":
                return "breakfast";
            case "lunch":
                return "dinner";
            case "breakfast":
                return "lunch";
            default:
                return "Unknown";
        }
    }

    /**
     * Return the hour's String representation  of the next eat time.
     *
     * @return
     */
    public static String getNextEatTimeHour() {
        switch(getNextEatTime()) {
            case "breakfast":
                return "5:00AM";
            case "dinner":
                return "5:00PM";
            case "lunch":
                return "12:00PM";
            default:
                return "Unknown";
        }
    }

    /**
     * Get time left in millisecond until the next eat time.
     *
     * @return
     */
    public static long timeLeft() {
        Calendar nextEatTime;

        switch(getEatTime()) {
            case "dinner":
                nextEatTime = breakfast();
                if(TimeHelper.now().after(TimeHelper.breakfast()))
                    nextEatTime.add(Calendar.DATE, 1);
                break;
            case "breakfast":
                nextEatTime = lunch();
                break;
            case "lunch":
                nextEatTime = dinner();
                break;
            default:
                nextEatTime = null;
        }

        if(nextEatTime == null)
            return Long.MAX_VALUE;
        else
            return nextEatTime.getTimeInMillis() - now().getTimeInMillis();
    }

    /**
     * Convert java.util.Calendar instance to a human readable String
     *
     * @param cal
     * @return
     */
    public static String toString(Calendar cal) {
        return DateFormat.getDateTimeInstance().format(cal.getTime());
    }

    /**
     * Helper method to create a java.util.Calendar object initialized
     * with today's date and user input time.
     *
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     * @return
     */
    private static Calendar createTodayAt(int hour, int minute, int second, int millisecond) {
        Calendar dinner = now();
        dinner.set(Calendar.HOUR_OF_DAY, hour);
        dinner.set(Calendar.MINUTE, minute);
        dinner.set(Calendar.SECOND, second);
        dinner.set(Calendar.MILLISECOND, millisecond);
        return dinner;
    }
}