package com.ubtechinc.base;

/**
 * [???????]
 * 
 * @author zengdengyi
 * @version 1.0
 * @date 2015-2-10 ????10:30:59
 * 
 **/

public class AlarmInfo {
	public boolean isOpen;// ????????
	public boolean isRepaet;// ???
	public byte hh;//
	public byte mm;
	public byte ss;
	public String actionName;// ????????

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isRepaet() {
		return isRepaet;
	}

	public void setRepaet(boolean isRepaet) {
		this.isRepaet = isRepaet;
	}

	public byte getHh() {
		return hh;
	}

	public void setHh(byte hh) {
		this.hh = hh;
	}

	public byte getMm() {
		return mm;
	}

	public void setMm(byte mm) {
		this.mm = mm;
	}

	public byte getSs() {
		return ss;
	}

	public void setSs(byte ss) {
		this.ss = ss;
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
}
