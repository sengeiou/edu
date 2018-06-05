package com.ubt.alpha1e.edu.onlineaudioplayer.DataObj;

import com.ubt.alpha1e.edu.base.Constant;
import com.ubt.alpha1e.edu.base.SPUtils;
import com.ubt.alpha1e.edu.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

public class DataConfig {

    public static List<ShowItem> getItems() {
        List<ShowItem> showItems = new ArrayList<>();
        String recentSearchKey = SPUtils.getInstance().getString(Constant.SP_RECENT_SEARCH_KEY,"");
        UbtLog.d("", "recentSearchKey:" + recentSearchKey);
        if(recentSearchKey.equals("")){
            return showItems;
        }

        String[] strs=recentSearchKey.split("####");
        int len = 0;
        if(strs.length > 5 ){
            len = 5 ;
        }else {
            len = strs.length ;
        }
        for(int i=0;i<len;i++){
            System.out.println(strs[i].toString());
            if(null == strs[i] || strs[i].toString().trim().equals("")){
                continue;
            }
            showItems.add(new ShowItem(strs[i].toString()));
        }

        return showItems;
    }
}
