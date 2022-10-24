package com.example.calendardateselect;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@SuppressLint("NonConstantResourceId")
public class ModalCalendar extends BottomSheetDialogFragment {

    public static final String TAG = BottomSheetDialogFragment.class.getName();

    public static class Builder {

        private final ModalCalendar dialog;

        public Builder() {
            this.dialog = new ModalCalendar();
        }

        public void dismiss() {
            this.dialog.dismiss();
        }

        public Builder setColorDefaultRange(String color) {
            this.dialog.colorDefault = color;
            return this;
        }

        public Builder setRangeDays(List<CalendarDay> days) {
            this.dialog.calendarDays.addAll(days);
            this.dialog.calendarDaysCopy.addAll(days);
            return this;
        }

        public Builder onDateSelected(onDateCalendarListener onClickListener) {
            this.dialog.listener = onClickListener;
            return this;
        }

        public Builder onChangeDate(onDateCalendarChangeListener onChangeListener) {
            this.dialog.listenerChange = onChangeListener;
            return this;
        }

        public Builder setSelectionColor(String color){
            this.dialog.selectionColor = color;
            return this;
        }

        public ModalCalendar show(FragmentManager fragmentManager) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //Can not perform this action after onSaveInstanceState
            fragmentTransaction.add(this.dialog, ModalCalendar.TAG).commitAllowingStateLoss();
            //this.dialog.show(fragmentManager, tag);
            return dialog;
        }
    }

    private MaterialCalendarView mCalendarView;
    private final List<CalendarDay> calendarDays = new ArrayList<>();
    private final List<CalendarDay> calendarDaysCopy = new ArrayList<>();
    private String colorDefault,selectionColor;
    private onDateCalendarListener listener;
    private onDateCalendarChangeListener listenerChange;

    private CalendarDay dateSelected;

    public interface onDateCalendarListener {
        void onDateSelected(CalendarDay value);
    }
    public interface onDateCalendarChangeListener {
        void onDateChange(CalendarDay value);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            BottomSheetBehavior<FrameLayout> bottomSheetBehavior = BottomSheetBehavior.from(Objects.requireNonNull(bottomSheet));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int state) {
                    if (state == BottomSheetBehavior.STATE_COLLAPSED)
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                    if (state == BottomSheetBehavior.STATE_HIDDEN)
                        dismissAllowingStateLoss();
                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                }
            });
        });

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_calendar,
                container, false);



        mCalendarView = v.findViewById(R.id.calendarView);
        View buttonSelectedDate = v.findViewById(R.id.buttonSelectedDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        mCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(calendar)
                .setMaximumDate(CalendarDay.from(2045, 5, 12))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        dateSelected = new CalendarDay(calendar.getTime());
        mCalendarView.setCurrentDate(calendar.getTime());
        mCalendarView.setDateSelected(calendar.getTime(), true);
        mCalendarView.setSelectionColor(Color.parseColor(selectionColor));

        //set decorators
        if (calendarDays.size() > 0) {
            mCalendarView.removeDecorators();
            mCalendarView.addDecorator(new Decorator(calendarDays));
        }

        mCalendarView.setOnDateChangedListener((widget, date, selected) -> {
            dateSelected = date;

            if (listenerChange != null) {
                listenerChange.onDateChange(dateSelected);
            }

            if (calendarDays.contains(date)) {
                calendarDays.clear();
                calendarDays.addAll(calendarDaysCopy);
                calendarDays.remove(date);
                mCalendarView.removeDecorators();
                mCalendarView.addDecorator(new Decorator(calendarDays));
            } else {
                //add decorator
                calendarDays.clear();
                calendarDays.addAll(calendarDaysCopy);
                mCalendarView.addDecorator(new Decorator(calendarDays));
            }
        });

        buttonSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDateSelected(dateSelected);
                    dismiss();
                }
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public class Decorator implements DayViewDecorator {
        private final HashSet<CalendarDay> dates;

        public Decorator(Collection<CalendarDay> dates) {
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return this.dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            Drawable drawable = ContextCompat.getDrawable(mCalendarView.getContext(), R.drawable.circle);

            if(colorDefault == null || colorDefault.isEmpty()){
                colorDefault = "#FFEB3B";
            }

            assert drawable != null;
            drawable.setTint(Color.parseColor(colorDefault));
            view.setBackgroundDrawable(drawable);
        }
    }
}
