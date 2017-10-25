package com.ubt.alpha1e.utils.connect;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.ubt.alpha1e.AlphaApplication;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

public class ConnectClientUtil {

    private static String TAG = "ConnectClientUtil";
    private static SSLContext sslContext = null;

    private static ConnectClientUtil connectClient = null;
    private static Context mContext = null;

    public static ConnectClientUtil getInstance(){
        if (connectClient == null){
            connectClient = new ConnectClientUtil();
            mContext = AlphaApplication.mContext;
        }
        return connectClient;
    }

    public void init(){
        initHostName();
        initCertificate();
        initOkHttps();

    }

    /**
     * 注册校验主机域名
     */
    public void initHostName(){
        //初始化 HttpsURLConnection
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                UbtLog.d(TAG,"hostname = " + hostname);
                boolean isVerigySuccess = false;
                for(String hostNameStr : HttpAddress.WebHostnames){
                    if(hostNameStr.equals(hostname)){
                        isVerigySuccess = true;
                        break;
                    }
                }
                return isVerigySuccess;
            }
        });

    }

    /**
     *  初始化 OkHttpUtils 注册证书
     */
    private void initOkHttps(){

        String certificatePath = getCertificatePath();
        InputStream caInput = null;
        InputStream[] caInputs = null;
        if(!TextUtils.isEmpty(certificatePath)){
            try {
                caInput = new BufferedInputStream(mContext.getAssets().open(certificatePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(caInput != null){
                caInputs = new InputStream[]{caInput};
            }
        }

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(caInputs, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectTimeout(5,TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        UbtLog.d(TAG,"hostname = " + hostname);
                        boolean isVerigySuccess = false;
                        for(String hostNameStr : HttpAddress.WebHostnames){
                            if(hostNameStr.equals(hostname)){
                                isVerigySuccess = true;
                                break;
                            }
                        }
                        return isVerigySuccess;
                    }
                })
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //初始化 initGlide 注册证书
        Glide.get(mContext).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(okHttpClient));
    }

    /**
     * 初始化证书（非正式环境）
     */
    public void initCertificate(){

        try {
            String certificatePath = getCertificatePath();
            if(TextUtils.isEmpty(certificatePath)){
                return;
            }

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(mContext.getAssets().open(certificatePath));
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
                //UbtLog.d(TAG, "ca = " + ((X509Certificate) ca).getSubjectDN());
                //UbtLog.d(TAG, "key =" + ((X509Certificate) ca).getPublicKey());
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLSv1","AndroidOpenSSL");
            context.init(null, tmf.getTrustManagers(), null);
            sslContext = context;
            UbtLog.d(TAG, "证书初始化成功" );

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Https SocketFactory
     * @return
     */
    public static SSLSocketFactory getSocketFactory(){
        if(sslContext != null){
            return sslContext.getSocketFactory();
        }
        return null;
    }

    private String getCertificatePath(){
        String certificatePath = "";
        if(HttpAddress.WebServiceAdderss.contains(HttpAddress.WebAddressDevelop)){
            //研发服务器
            certificatePath = "certificate/14_sds.crt";
        }else if(HttpAddress.WebServiceAdderss.contains(HttpAddress.WebAddressTest)){
            //certificatePath = "certificate/14_sds.crt";
        }

        return certificatePath;
    }

}
