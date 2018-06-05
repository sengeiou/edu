package com.ubt.alpha1e_edu.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ubt.alpha1e_edu.data.FileTools;
import com.ubt.alpha1e_edu.utils.log.UbtLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 签名工具
 * Created by Administrator on 2017/8/23.
 */
public class SignaturesUtil {

    private static final String TAG = "SignaturesUtil";

    /**
     * 从APK中读取签名
     * @param file
     * @return
     * @throws IOException
     */
    public static String getSignaturesFromApk(File file) {
        List<String> signatures = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry je=jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer=new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if(certs != null) {
                for(Certificate c: certs) {
                    String sig = toCharsString(c.getEncoded());
                    signatures.add(sig);
                }
            }
        } catch(Exception ex) {
            UbtLog.e(TAG,ex.getMessage());
            return "";
        }
        if(signatures.size() > 0){
            return signatures.get(0);
        }
        return "";
    }

    /**
     * 加载签名
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while(is.read(readBuffer, 0, readBuffer.length) != -1) {

            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch(IOException e) {
            UbtLog.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * 将签名转成转成可见字符串
     * @param sigBytes
     * @return
     */
    private static String toCharsString(byte[] sigBytes) {
        byte[] sig=sigBytes;
        final int N=sig.length;
        final int N2=N * 2;
        char[] text=new char[N2];
        for(int j=0; j < N; j++) {
            byte v=sig[j];
            int d=(v >> 4) & 0xf;
            text[j * 2]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
            d=v & 0xf;
            text[j * 2 + 1]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));
        }
        return new String(text);
    }

    //这是获取apk包的签名信息

    /**
     * 获取已经安装app的签名
     * @param context
     * @return
     */
    public static String getSign(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
            Iterator<PackageInfo> iter = apps.iterator();
            while(iter.hasNext()) {
                PackageInfo packageinfo = iter.next();
                String packageName = packageinfo.packageName;
                if(packageName.equals(FileTools.current_app_package)){
                    UbtLog.d(TAG,"packageName = " + packageName + "     signatures = " + packageinfo.signatures[0].toCharsString());
                    return packageinfo.signatures[0].toCharsString();
                }
            }
        }catch (Exception ex){
            UbtLog.e(TAG,ex.getMessage());
            return "";
        }
        return "";
    }
}
