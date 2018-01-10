// Generated code from Butter Knife. Do not modify!
package com.ubt.factorytest.test;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ubt.factorytest.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TestFragment_ViewBinding implements Unbinder {
  private TestFragment target;

  @UiThread
  public TestFragment_ViewBinding(TestFragment target, View source) {
    this.target = target;

    target.mRecyclerView = Utils.findRequiredViewAsType(source, R.id.rv_test, "field 'mRecyclerView'", RecyclerView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TestFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRecyclerView = null;
    target.toolbar = null;
  }
}
