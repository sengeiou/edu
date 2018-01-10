// Generated code from Butter Knife. Do not modify!
package com.ubt.factorytest.bluetooth.netconnect;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ubt.factorytest.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NetconnectFragment_ViewBinding implements Unbinder {
  private NetconnectFragment target;

  @UiThread
  public NetconnectFragment_ViewBinding(NetconnectFragment target, View source) {
    this.target = target;

    target.ib_close = Utils.findRequiredViewAsType(source, R.id.ib_close, "field 'ib_close'", ImageButton.class);
    target.ib_return = Utils.findRequiredViewAsType(source, R.id.ib_return, "field 'ib_return'", ImageButton.class);
    target.ed_wifi_name = Utils.findRequiredViewAsType(source, R.id.ed_wifi_name, "field 'ed_wifi_name'", EditText.class);
    target.ig_get_wifi_name = Utils.findRequiredViewAsType(source, R.id.ig_get_wifi_name, "field 'ig_get_wifi_name'", ImageView.class);
    target.ed_wifi_pwd = Utils.findRequiredViewAsType(source, R.id.ed_wifi_pwd, "field 'ed_wifi_pwd'", EditText.class);
    target.ig_see_wifi_pwd = Utils.findRequiredViewAsType(source, R.id.ig_see_wifi_pwd, "field 'ig_see_wifi_pwd'", ImageView.class);
    target.btn_send_wifi_pwd = Utils.findRequiredViewAsType(source, R.id.btn_send_wifi_pwd, "field 'btn_send_wifi_pwd'", Button.class);
    target.rl_net_list = Utils.findRequiredViewAsType(source, R.id.rl_net_list, "field 'rl_net_list'", RelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NetconnectFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ib_close = null;
    target.ib_return = null;
    target.ed_wifi_name = null;
    target.ig_get_wifi_name = null;
    target.ed_wifi_pwd = null;
    target.ig_see_wifi_pwd = null;
    target.btn_send_wifi_pwd = null;
    target.rl_net_list = null;
  }
}
