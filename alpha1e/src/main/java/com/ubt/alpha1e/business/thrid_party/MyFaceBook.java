package com.ubt.alpha1e.business.thrid_party;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.facebook.share.widget.ShareDialog.Mode;
import com.ubt.alpha1e.data.model.ActionInfo;
import com.ubt.alpha1e.net.http.basic.HttpAddress;
import com.ubt.alpha1e.ui.helper.IPrivateInfoUI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Set;

public class MyFaceBook {
    private static CallbackManager callbackManager;
    private static AccessToken accessToken;
    public static boolean isNeedOnResualt = false;
    public static String url = null;

    public static void initMyFaceBook(Context context) {
        FacebookSdk.sdkInitialize(context);
        if (callbackManager == null)
            callbackManager = CallbackManager.Factory.create();
    }

    public static void doLogin(Activity activity,
                               final IFaceBookLoginListener listener) {

        initMyFaceBook(activity.getApplicationContext());
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        accessToken = loginResult.getAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    // 當RESPONSE回來的時候
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {
                                        // 讀出姓名 ID FB個人頁面連結
                                        Log.d("FB", "complete");
                                        System.out.println(object.toString());
                                        try {
                                            url = response.getJSONObject()
                                                    .getJSONObject("picture")
                                                    .getJSONObject("data")
                                                    .getString("url");
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        listener.onLoginComplete(object);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,link,gender,picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("Login onCancel");
                    }

                    @Override
                    public void onError(FacebookException e) {
                        System.out.println("Login onError:" + e.getMessage());
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(activity,
                Arrays.asList("public_profile"));
    }

    public static void onGetUserProfile(IPrivateInfoUI listener) {
        Profile profile = Profile.getCurrentProfile();
        listener.onFaceBookProfileInfo(profile, url);

    }

    public static void onActivityResult(int requestCode, int resultCode,
                                        Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static boolean canShare() {
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            return false;
        }
        final Set<String> permissions = accessToken.getPermissions();
        if (permissions == null) {
            return false;
        }
        return (permissions.contains("publish_actions"));

    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
    public static void doShareFaceBook(Activity activity, ActionInfo action,
                                       String url) {
        initMyFaceBook(activity);
        ShareDialog shareDialog = new ShareDialog(activity);
        Uri shareUri = Uri.parse(url);


        String title = "";
        if(!TextUtils.isEmpty(action.actionName) && !action.actionName.equals("")){
            title = action.actionName;
        }else{
            title = "Alpha";
        }

        String imagePath = "";
        if(!TextUtils.isEmpty(action.actionImagePath) && !action.actionImagePath.equals("")){
            imagePath = action.actionImagePath;
        }else{
            if(!action.actionHeadUrl.equals("") && !TextUtils.isEmpty(action.actionHeadUrl)){
                imagePath = action.actionHeadUrl;
            }else{
                imagePath = HttpAddress.WebDefaultAppLauncherAddress;
            }
        }

//        String imagePath = action.actionImagePath;
//        if(TextUtils.isEmpty(imagePath))
//            imagePath = action.actionHeadUrl;
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(title)
                .setContentDescription(action.actionDesciber)
                .setImageUrl(Uri.parse(imagePath))
                .setContentUrl(shareUri).build();
        shareDialog.registerCallback(callbackManager,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }

                    @Override
                    public void onSuccess(Sharer.Result result) {
                    }
                });
        shareDialog.show(content, Mode.AUTOMATIC);
    }

    public static void doShareFaceBook(Activity activity, String url) {

        initMyFaceBook(activity.getApplicationContext());
        ShareDialog shareDialog = new ShareDialog(activity);
        Uri uri = Uri.parse(url);
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(uri).build();
        shareDialog.registerCallback(callbackManager,
                new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onCancel() {
                        System.out.println("HelloFacebook onCancel!");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        System.out.println("HelloFacebook onerror："
                                + String.format("Error: %s", error.toString()));
                    }

                    @Override
                    public void onSuccess(Sharer.Result result) {
                        System.out.println("HelloFacebook Success!");
                        if (result.getPostId() != null) {
                        }
                    }
                });
        shareDialog.show(content, Mode.AUTOMATIC);

    }
}
