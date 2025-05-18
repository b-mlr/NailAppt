package com.example.nailappt;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.HashSet;

public class AppointmentDecorator implements DayViewDecorator {
    private final HashSet<CalendarDay> days;
    private final Drawable highlightDrawable;

    public AppointmentDecorator(HashSet<CalendarDay> days){
        this.days = days;

        ShapeDrawable circle = new ShapeDrawable(new OvalShape());
        circle.getPaint().setColor(Color.parseColor("#7BAD83"));
        highlightDrawable = circle;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return days.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(highlightDrawable);
    }
}
