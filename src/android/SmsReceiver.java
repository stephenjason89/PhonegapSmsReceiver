package com.blessanmathew.smsreceiver;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends CordovaPlugin {
	
	public static final String ACTION_REGISTER_FOR_SMS_RECEIVE = "registerSMSListener";
	public static final String ACTION_UNREGISTER_FOR_SMS_RECEIVE = "unregisterSMSListener";
	private SmsBroadcastReceiver receiver;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if (ACTION_REGISTER_FOR_SMS_RECEIVE.equals(action)) {
            	public static final String phoneNumber = args.getString(0);
                receiver = new SmsBroadcastReceiver(callbackContext);
                //this.cordova.getActivity().registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                cordova.getThreadPool().execute(new Runnable() {
                	public void run() {
                		cordova.getActivity().registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
                	}
                });
 				return true;
            }
            
            if (ACTION_UNREGISTER_FOR_SMS_RECEIVE.equals(action)) {
            	receiver = new SmsBroadcastReceiver(callbackContext);
            	this.cordova.getActivity().unregisterReceiver(receiver);
            	return true;
            }
        } catch(Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
				
				callbackContext.error("Invalid action");
        return true;
    }
    
    public class SmsBroadcastReceiver extends BroadcastReceiver {
    	
    	private CallbackContext ctx;
    	
    	public SmsBroadcastReceiver(CallbackContext context) {
    		super();
    		ctx = context;    
    		//JSONObject arg_object = args.getJSONObject(0);
    		//cpnum = arg_object.getString("cpnum");
    	}

    	@Override
    	public void onReceive(Context context, Intent intent) {
    	    //JSONObject arg_object = args.getJSONObject(0);
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;

            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");
                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    
                    String msgBody = messages[i].getMessageBody();
                    String msgFromAddress =  messages[i].getOriginatingAddress();
                    Long msgTimestamp =  messages[i].getTimestampMillis();
                    
                    Log.e("SmsReceiver: " + msgBody, msgFromAddress);
                    
                    JSONObject obj = new JSONObject();
                    try {
						obj.put("msg", msgBody);
						obj.put("sender", msgFromAddress);
						obj.put("time", msgTimestamp);
						ctx.success(obj);
					} catch (JSONException e) {
						e.printStackTrace();
					} 
               
                }
                 //if(arg_object.getString("cpnum")==msgFromAddress) abortBroadcast();

            }

    	}

    }
}
