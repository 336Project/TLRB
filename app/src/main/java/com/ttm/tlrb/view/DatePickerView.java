package com.ttm.tlrb.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.ttm.tlrb.R;

public class DatePickerView extends AlertDialog implements
		OnDateChangedListener {

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";

	private final DatePicker mDatePicker;
	private final OnDateSetListener mCallBack;
	private View view;

	/**
	 * The callback used to indicate the user is done filling in the date.
	 */
	public interface OnDateSetListener {
		void onDateSet(DatePicker view, int year, int monthOfYear,
					   int dayOfMonth);
	}

	public DatePickerView(Context context, OnDateSetListener callBack,
						  int year, int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth);
	}

	public DatePickerView(Context context, int theme,
						  OnDateSetListener callBack, int year, int monthOfYear,
						  int dayOfMonth) {
		super(context, theme);
		mCallBack = callBack;
		Context themeContext = getContext();
		LayoutInflater inflater = (LayoutInflater) themeContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.view_date_picker_dialog, null);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);

		setTitle("选择日期");
		setButton();
	}

	public void myShow() {
		show();
		setContentView(view);
	}

	private void setTitle(String title) {
		((TextView) view.findViewById(R.id.date_picker_title)).setText(title);
	}

	private void setButton() {
		view.findViewById(R.id.date_picker_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mCallBack != null) {
							mDatePicker.clearFocus();
							mCallBack.onDateSet(mDatePicker,
									mDatePicker.getYear(),
									mDatePicker.getMonth()+1,
									mDatePicker.getDayOfMonth());
						}
						dismiss();
					}
				});
		view.findViewById(R.id.btn_neg).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dismiss();
					}
				});
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		mDatePicker.init(year, month, day, null);
	}

	/**
	 * Gets the {@link DatePicker} contained in this dialog.
	 * 
	 * @return The calendar view.
	 */
	public DatePicker getDatePicker() {
		return mDatePicker;
	}

	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);
	}
}