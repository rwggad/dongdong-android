package com.example.rwgga.dongdong.decorators;


import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.example.rwgga.dongdong.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;


/**
 *
 * 작성자 : Created by (출처 : https://github.com/prolificinteractive/material-calendarview)
 *
 */
public class EventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private int color;
    private HashSet<CalendarDay> dates;

    public EventDecorator(int color, Collection<CalendarDay> dates,Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.more);
        this.color = color;
        this.dates = new HashSet<>(dates);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //view.setSelectionDrawable(drawable); // 일정이 존재하는 곳 테두리
        view.addSpan(new DotSpan(7, color)); // 날자밑에 점
    }
}