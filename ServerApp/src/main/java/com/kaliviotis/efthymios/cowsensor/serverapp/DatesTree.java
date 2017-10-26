package com.kaliviotis.efthymios.cowsensor.serverapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Efthymios on 10/24/2017.
 */

public class DatesTree {
    public interface CallbackInterface{
        void IteratorCallback(int year, int month, int day, int hour, int minute, int stepsNum);
    }


    private class MinutesHashmap extends HashMap<Integer, Integer> { }
    private class HoursHashmap extends HashMap<Integer, MinutesHashmap> { }
    private class DaysHashmap extends HashMap<Integer, HoursHashmap> { }
    private class MonthsHashmap extends HashMap<Integer, DaysHashmap> { }
    private HashMap<Integer, MonthsHashmap> years;

    public DatesTree() {
        years = new HashMap<Integer, MonthsHashmap>();
    }

    public void AddSteps(int year, int month, int day, int hour, int minute, int stepsNum) {
        MonthsHashmap _months;
        DaysHashmap _days;
        HoursHashmap _hours;
        MinutesHashmap _minutes;

        if (years.containsKey(year)) {
            _months = years.get(year);
        } else {
            _months = new MonthsHashmap();
            years.put(year, _months);
        }

        if (_months.containsKey(month)) {
            _days = _months.get(month);
        } else {
            _days = new DaysHashmap();
            _months.put(month, _days);
        }

        if (_days.containsKey(day)) {
            _hours = _days.get(day);
        } else {
            _hours = new HoursHashmap();
            _days.put(day, _hours);
        }

        if (_hours.containsKey(hour)) {
            _minutes = _hours.get(hour);
        } else {
            _minutes = new MinutesHashmap();
            _hours.put(hour, _minutes);
        }

        if (!_minutes.containsKey(minute))
            _minutes.put(minute, stepsNum);
    }
    
    public void IterateItems(CallbackInterface callback) {
        for(Map.Entry<Integer, MonthsHashmap> yearEntry : years.entrySet()) {
            int year = yearEntry.getKey();
            MonthsHashmap months = yearEntry.getValue();

            for(Map.Entry<Integer, DaysHashmap> monthEntry : months.entrySet()) {
                int month = monthEntry.getKey();
                DaysHashmap days = monthEntry.getValue();

                for(Map.Entry<Integer, HoursHashmap> dayEntry : days.entrySet()) {
                    int day = dayEntry.getKey();
                    HoursHashmap hours = dayEntry.getValue();

                    for(Map.Entry<Integer, MinutesHashmap> hourEntry : hours.entrySet()) {
                        int hour = hourEntry.getKey();
                        MinutesHashmap minutes = hourEntry.getValue();

                        for(Map.Entry<Integer, Integer> minuteEntry : minutes.entrySet()) {
                            int minute = minuteEntry.getKey();
                            int stepsNum = minuteEntry.getValue();

                            callback.IteratorCallback(year, month, day, hour, minute, stepsNum);
                        }
                    }
                }
            }

            // do what you have to do here
            // In your case, another loop.
        }
        //callback.IteratorCallback();
    }
}
