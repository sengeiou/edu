package com.ubt.alpha1e.data.model;

import java.util.List;

public class _RemoteInfo extends BaseModel {

	private static enum Default_Actions {
		Do_up, Do_down, Do_left, Do_right, Do_to_left, Do_to_right
	}

	private static List<String> mActionsInRobot;

	private _RemoteInfo thiz;

	public String do_up = "";
	public String do_down = "";
	public String do_left = "";
	public String do_right = "";
	public String do_to_left = "";
	public String do_to_right = "";
	public String do_middle_up = "";
	public String do_middle_down = "";
	public String do_1 = "";
	public String do_2 = "";
	public String do_3 = "";
	public String do_4 = "";
	public String do_5 = "";
	public String do_6 = "";

	public _RemoteInfo() {
		thiz = this;
	}

	public static void setRobotInfo(List<String> _actionsInRobot) {
		mActionsInRobot = _actionsInRobot;
	}

	public _RemoteInfo addDefaultRule(String language) {

		if (thiz.do_up.equals(""))
			thiz.do_up = getDefaultActionName(Default_Actions.Do_up, language);
		if (thiz.do_down.equals(""))
			thiz.do_down = getDefaultActionName(Default_Actions.Do_down,
					language);
		if (thiz.do_left.equals(""))
			thiz.do_left = getDefaultActionName(Default_Actions.Do_left,
					language);
		if (thiz.do_right.equals(""))
			thiz.do_right = getDefaultActionName(Default_Actions.Do_right,
					language);
		if (thiz.do_to_left.equals(""))
			thiz.do_to_left = getDefaultActionName(Default_Actions.Do_to_left,
					language);
		if (thiz.do_to_right.equals(""))
			thiz.do_to_right = getDefaultActionName(
					Default_Actions.Do_to_right, language);

		return thiz;
	}

	@Override
	public _RemoteInfo getThiz(String json) {

		try {
			thiz = mMapper.readValue(json, _RemoteInfo.class);

		} catch (Exception e) {
			thiz = new _RemoteInfo();
		}
		return thiz;
	}

	private String getDefaultActionName(Default_Actions action_index,
			String language) {

		if (mActionsInRobot == null)
			return "";
		String name = "";
		String name_standard = "";
		String name_cn_standard = "";
		if (action_index == Default_Actions.Do_up) {
			name_standard = "Move forward";
			name_cn_standard = "前进";
		} else if (action_index == Default_Actions.Do_down) {
			name_standard = "Move back";
			name_cn_standard = "后退";
		} else if (action_index == Default_Actions.Do_left) {
			name_standard = "Move Leftward";
			name_cn_standard = "左移";
		} else if (action_index == Default_Actions.Do_right) {
			name_standard = "Move Rightward";
			name_cn_standard = "右移";
		} else if (action_index == Default_Actions.Do_to_left) {
			name_standard = "Turn Left";
			name_cn_standard = "左转";
		} else if (action_index == Default_Actions.Do_to_right) {
			name_standard = "Turn Right";
			name_cn_standard = "右转";
		}
		for (int i = 0; i < mActionsInRobot.size(); i++) {
			if (mActionsInRobot.get(i).toLowerCase()
					.contains(name_standard.toLowerCase()))
				name = mActionsInRobot.get(i);
		}
		if (language.equals("CN")) {
			for (int i = 0; i < mActionsInRobot.size(); i++) {
				if (mActionsInRobot.get(i).contains(name_cn_standard))
					name = mActionsInRobot.get(i);
			}
		}
		return name;
	}
}
