package com.ubt.alpha1e.edu.update;

import android.content.Context;
import android.content.pm.PackageInfo;

public class UpdateTools {

    // true表示远程版本大，即需要升级
    public static boolean compareVersion(String remote_version, Context mContext) {

        String packageName = mContext.getPackageName();
        PackageInfo info = null;
        try {
            info = mContext.getPackageManager().getPackageInfo(packageName, 0);
            String verLocal = info.versionName;
            String[] lVersion = verLocal.split("\\.");
            String[] rVersion = remote_version.split("\\.");

            for (int i = 0; i < lVersion.length; i++) {
                if (Integer.parseInt(rVersion[i]) > Integer
                        .parseInt(lVersion[i])) {
                    return true;
                } else if (Integer.parseInt(rVersion[i]) == Integer
                        .parseInt(lVersion[i])) {
                    continue;
                } else {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    public static boolean compareVersion(String remote_version, String local_version) {
        try {
            String[] lVersion = local_version.split("\\.");
            String[] rVersion = remote_version.split("\\.");
            for (int i = 0; i < lVersion.length; i++) {
                if (Integer.parseInt(rVersion[i]) > Integer
                        .parseInt(lVersion[i])) {
                    return true;
                } else if (Integer.parseInt(rVersion[i]) == Integer
                        .parseInt(lVersion[i])) {
                    continue;
                } else {
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

}
