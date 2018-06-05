package com.ubt.alpha1e.edu.utils;

import com.ubt.alpha1e.edu.R;
import com.ubt.alpha1e.edu.ui.BaseActivity;

/**
 * Created by Administrator on 2016/6/17.
 */
public class ResourceUtils {

    public static String getActionType(int type, BaseActivity baseActivity)
    {
        String str = "";
        String[] acitonTypes = baseActivity.getResources().getStringArray(R.array.action_type_key);
        switch (type)
        {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                str = baseActivity.getStringResources(acitonTypes[type]);
                        break;
            default:
                str = baseActivity.getStringResources(acitonTypes[6]);
                break;

        }
       return str;
    }

    public static int getActionTypeImage(int type, BaseActivity baseActivity)
    {
        int resId = 0;
        switch (type)
        {
            case 1:
                resId = R.drawable.actions_item_music;
                break;
            case 2:
                resId = R.drawable.actions_item_story;
                break;
            case 3:
                resId = R.drawable.actions_item_sport;
                break;
            case 4:
                resId = R.drawable.actions_item_child;
                break;
            case 5:
                resId = R.drawable.actions_item_science;
                break;
            default:
                resId = R.drawable.actions_item_unkown;
                break;

        }
    return resId;
    }
}
