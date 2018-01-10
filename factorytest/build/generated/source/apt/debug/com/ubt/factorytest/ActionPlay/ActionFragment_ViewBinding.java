// Generated code from Butter Knife. Do not modify!
package com.ubt.factorytest.ActionPlay;

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

public class ActionFragment_ViewBinding implements Unbinder {
  private ActionFragment target;

  @UiThread
  public ActionFragment_ViewBinding(ActionFragment target, View source) {
    this.target = target;

    target.pgBar = Utils.findRequiredViewAsType(source, R.id.pg_bar, "field 'pgBar'", ProgressBar.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.rv_test, "field 'mRecyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ActionFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.pgBar = null;
    target.toolbar = null;
    target.mRecyclerView = null;
  }
}
