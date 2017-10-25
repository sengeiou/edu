package com.ubt.alpha1e.ui.helper;

import com.ubtechinc.base.AlarmInfo;

public interface IAlarmUI {
	public void onNoteNoAlarm();

	public void onReadAlarmFinish(AlarmInfo alarm_info);

	public void onNoteEdit();

	public void onNoteWriteAlarmStart();

	public void onNoteWriteAlarmFinish(boolean is_success);

	public void onNoteEditAlarmCancel();

	public void onReadAlarmName(String obj);
}
