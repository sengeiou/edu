package com.ubt.alpha1e.onlineaudioplayer.DataObj;

import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangcheng on 17/4/13.
 */

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
            showItems.add(new ShowItem(strs[i].toString()));
        }

        return showItems;
    }
}
