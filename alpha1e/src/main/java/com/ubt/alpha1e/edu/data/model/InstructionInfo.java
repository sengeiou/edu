package com.ubt.alpha1e.edu.data.model;

import com.baoyz.pg.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

@Parcelable
public class InstructionInfo extends BaseModel {


	public String type; 		//指令类型 0为语言 1为传感器
	public String typeDes;      //指令类型描述
	public String typeSub;		//指令子类型 0 为执行指令 1 为机器人相关
	public String typeSubDes;   //指令子类型描述
	public String name;			//指令响应名称
	public String reply;		//指令回答内容
	public int replyType;       //指令回答类型 0 为文本  1为动作
	public int replySubType;    //指令回答子类型 replyType = 1 为动作时，分为 0 内置动作  1 下载动作 2 创建动作
	public String time;			//创建时间
	public boolean selected = false; //是否选中

	//指令类型
	public enum InstructionType{
		voice, 	//语音
		sensor  //传感器
	}

	public InstructionInfo thiz;

	@Override
	public InstructionInfo getThiz(String json) {
		try {
			thiz = mMapper.readValue(json, InstructionInfo.class);
			return thiz;
		} catch (Exception e) {
			thiz = null;
			return null;
		}
	}

	public static ArrayList<InstructionInfo> getModelList(String json) {
		ArrayList<InstructionInfo> result = new ArrayList<InstructionInfo>();
		try {
			JSONArray j_list = new JSONArray(json);
			for (int i = 0; i < j_list.length(); i++) {
				result.add(new InstructionInfo().getThiz(j_list.get(i).toString()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getModeslStr(ArrayList<InstructionInfo> infos) {

		try {
			return mMapper.writeValueAsString(infos);
		} catch (Exception e) {
			String error = e.getMessage();
			return Convert_fail;
		}
	}

}
