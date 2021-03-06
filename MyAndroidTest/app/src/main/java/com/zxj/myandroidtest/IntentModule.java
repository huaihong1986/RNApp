package com.zxj.myandroidtest;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class IntentModule extends ReactContextBaseJavaModule {
    public IntentModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "IntentModule";
    }
    /**
     * 从JS页面跳转到原生activity   同时也可以从JS传递相关数据到原生
     * @param name  需要打开的Activity的class
     * @param params
     */
    @ReactMethod
    public void startActivityFromJS(String name, String params){
        try{
            Activity currentActivity = getCurrentActivity();
            if(null!=currentActivity){
                Class toActivity = Class.forName(name);
                Intent intent = new Intent(currentActivity,toActivity);
                intent.putExtra("params", params);
                currentActivity.startActivity(intent);
            }
        }catch(Exception e){
            throw new JSApplicationIllegalArgumentException(
                    "不能打开Activity : "+e.getMessage());
        }
    }

    /**
     * 从JS页面跳转到Activity界面，并且等待从Activity返回的数据给JS
     * @param className
     * @param successBack
     * @param errorBack
     */
    @ReactMethod
    public void startActivityFromJSGetResult(String className,int requestCode,Callback successBack, Callback errorBack){
        try {
            Activity currentActivity=getCurrentActivity();
            if(currentActivity!=null) {
                Class toActivity = Class.forName(className);
                Intent intent = new Intent(currentActivity,toActivity);
                currentActivity.startActivityForResult(intent,requestCode);
                //进行回调数据
                successBack.invoke(ReactPageActivity.mQueue.take());
            }
        } catch (Exception e) {
            errorBack.invoke(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Activtiy跳转到JS页面，传输数据
     * @param successBack
     * @param errorBack
     */
    @ReactMethod
    public void dataToJS(Callback successBack, Callback errorBack){
        try{
            Activity currentActivity = getCurrentActivity();
            String result = currentActivity.getIntent().getStringExtra("data");
            if (TextUtils.isEmpty(result)){
                result = "Activtiy跳转到JS页面，传输数据";
            }
            successBack.invoke(result);
        }catch (Exception e){
            errorBack.invoke(e.getMessage());
        }
    }

    /**
     * 使用Promise机制向RN页面传数据
     * @param msg
     * @param promise
     */
    @ReactMethod
    public void rnCallNativePromiss(String msg,Promise promise){
        try {
            Toast.makeText(getCurrentActivity(), msg, Toast.LENGTH_SHORT).show();
            String componentName = getCurrentActivity().getComponentName().toString();
            promise.resolve(componentName);
        }catch (Exception e){
            promise.reject("500",e.getMessage());
        }
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String, Object> constants = new HashMap<>();
        constants.put("name","小杰");
        constants.put("msg","原生的数据");
        return constants;
    }
}