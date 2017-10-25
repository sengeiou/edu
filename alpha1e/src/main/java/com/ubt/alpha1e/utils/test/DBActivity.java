package com.ubt.alpha1e.utils.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.DB.RemoteRecordOperater;
import com.ubt.alpha1e.data.FileTools;
import com.ubt.alpha1e.data.RemoteItem;
import com.ubt.alpha1e.data.model.RemoteInfo;

import java.util.ArrayList;
import java.util.List;

public class DBActivity extends Activity {

    private Button btn_read_db;
    private TextView txt_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_db);
        btn_read_db = (Button) findViewById(R.id.btn_read_db);
        txt_result = (TextView) findViewById(R.id.txt_result);
        RemoteInfo info = RemoteRecordOperater.getInstance(this.getApplicationContext(), FileTools.db_log_cache, "UbtLogs_0001").getRemoteInfoByModel(RemoteRecordOperater.ModelType.FOOTBALL_PLAYER, true,null);
        info.do_6.hts_name = "Move back.hts";
        List<RemoteItem> items = new ArrayList<RemoteItem>();
        items.add(info.do_6);
        RemoteRecordOperater.getInstance(this.getApplicationContext(), FileTools.db_log_cache, "UbtLogs_0001").UpdateRemoteInfo(RemoteRecordOperater.ModelType.FOOTBALL_PLAYER, info, items,"");
        RemoteRecordOperater.getInstance(this.getApplicationContext(), FileTools.db_log_cache, "UbtLogs_0001").getAllActions(RemoteRecordOperater.ModelType.FOOTBALL_PLAYER, true,"");
    }

}
