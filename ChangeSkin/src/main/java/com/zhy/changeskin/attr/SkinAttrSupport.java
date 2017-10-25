package com.zhy.changeskin.attr;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.zhy.changeskin.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhy on 15/9/23.
 */
public class SkinAttrSupport {
    public static List<SkinAttr> getSkinAttrs(AttributeSet attrs, Context context, Map<String,String> skinMap) {
        List<SkinAttr> skinAttrs = new ArrayList<SkinAttr>();
        SkinAttr skinAttr = null;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {

            String attrName = attrs.getAttributeName(i);
            String attrValue = attrs.getAttributeValue(i);

            SkinAttrType attrType = getSupprotAttrType(attrName);
            if (attrType == null) continue;

            if (attrValue.startsWith("@")) {
                int id = Integer.parseInt(attrValue.substring(1));
                String entryName = "";
                try {
                    entryName = context.getResources().getResourceEntryName(id);
                } catch (Exception e) {
                    continue;
                }
                //L.e("entryName = " + entryName + "    attrType: " + attrType);
                if (attrType.equals(SkinAttrType.TEXT) && !entryName.equalsIgnoreCase("hello_world")) {
                    //L.e("string entryName = " + entryName);
                    skinAttr = new SkinAttr(attrType, entryName);
                    skinAttrs.add(skinAttr);
                }else if((attrType.equals(SkinAttrType.SRC) || attrType.equals(SkinAttrType.BACKGROUND)) && skinMap.containsKey(entryName)){
                    //L.e("drawable entryName = " + entryName);
                    skinAttr = new SkinAttr(attrType, entryName);
                    skinAttrs.add(skinAttr);
                }else if(attrType.equals(SkinAttrType.COLOR) && skinMap.containsKey(entryName)){
                    //L.e("color entryName = " + entryName);
                    skinAttr = new SkinAttr(attrType, entryName);
                    skinAttrs.add(skinAttr);
                }
            }
        }
        return skinAttrs;

    }

    private static SkinAttrType getSupprotAttrType(String attrName) {
        for (SkinAttrType attrType : SkinAttrType.values()) {
            if (attrType.getAttrType().equals(attrName))
                return attrType;
        }
        return null;
    }

}
