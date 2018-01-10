// Generated code from Butter Knife. Do not modify!
package com.ubt.factorytest.bluetooth;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ubt.factorytest.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BluetoothFragment_ViewBinding implements Unbinder {
  private BluetoothFragment target;

  @UiThread
  public BluetoothFragment_ViewBinding(BluetoothFragment target, View source) {
    this.target = target;

    target.mPgBar = Utils.findRequiredViewAsType(source, R.id.pg_bar, "field 'mPgBar'", ProgressBar.class);
    target.mToolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'mToolbar'", Toolbar.class);
    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.rv_bluetooth, "field 'mRecyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BluetoothFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mPgBar = null;
    target.mToolbar = null;
    target.mRecyclerView = null;
  }
}
