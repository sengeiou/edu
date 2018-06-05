package com.ubt.alpha1e_edu.data.model;

import com.ubt.alpha1e_edu.data.RemoteItem;

/**
 * Created by Administrator on 2016/4/13.
 */
public class RemoteInfo {

    public RemoteItem do_up = new RemoteItem();
    public RemoteItem do_down = new RemoteItem();
    public RemoteItem do_left = new RemoteItem();
    public RemoteItem do_right = new RemoteItem();
    public RemoteItem do_to_left = new RemoteItem();
    public RemoteItem do_to_right = new RemoteItem();
    public RemoteItem do_1 = new RemoteItem();
    public RemoteItem do_2 = new RemoteItem();
    public RemoteItem do_3 = new RemoteItem();
    public RemoteItem do_4 = new RemoteItem();
    public RemoteItem do_5 = new RemoteItem();
    public RemoteItem do_6 = new RemoteItem();


    public RemoteInfo doCopy() {
        RemoteInfo result = new RemoteInfo();
        result.do_up = this.do_up.doCopy();
        result.do_down = this.do_down.doCopy();
        result.do_left = this.do_left.doCopy();
        result.do_right = this.do_right.doCopy();
        result.do_to_left = this.do_to_left.doCopy();
        result.do_to_right = this.do_to_right.doCopy();
        result.do_1 = this.do_1.doCopy();
        result.do_2 = this.do_2.doCopy();
        result.do_3 = this.do_3.doCopy();
        result.do_4 = this.do_4.doCopy();
        result.do_5 = this.do_5.doCopy();
        result.do_6 = this.do_6.doCopy();
        return result;
    }

}

