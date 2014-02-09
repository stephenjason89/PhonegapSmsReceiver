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
import android.os.Handler;
import android.net.Uri;

public class SmsReceiver extends CordovaPlugin {
    public static final String ACTION_REGISTER_FOR_SMS_RECEIVE = "registerSMSListener";
    public static final String ACTION_UNREGISTER_FOR_SMS_RECEIVE = "unregisterSMSListener";
    public String abortnum;
    public String abortflag;
    public String msgBody;
    public String msgFromAddress;
    public Long msgTimestamp;
    private SmsBroadcastReceiver receiver;

    @
    Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if (ACTION_REGISTER_FOR_SMS_RECEIVE.equals(action)) {
                abortnum = args.getString(0);

                receiver = new SmsBroadcastReceiver(callbackContext);
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
        } catch (Exception e) {
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
        }

        @
        Override
        public void onReceive(Context context, Intent intent) {
            clearAbortBroadcast();
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;
            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");
                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    msgBody = messages[i].getMessageBody();
                    msgFromAddress = messages[i].getOriginatingAddress();
                    msgTimestamp = messages[i].getTimestampMillis();

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
                    if (abortnum.equals(msgFromAddress) && isOrderedBroadcast()) {
                    	
                        Handler handlerTimer= new Handler();
                    	handlerTimer.postDelayed(new Runnable(){
                            public void run() {
                               //do something     
                              //receiver = new SmsBroadcastReceiver(context);
                              //cordova.getActivity().unregisterReceiver(receiver);   
                              //Uri deleteUri = Uri.parse("content://sms");
                              //cordova.getActivity().getContentResolver().delete(deleteUri, "address=? and date=?", new String[] {msgFromAddress, String.valueOf(msgTimestamp)});
                            deleteSMS)(cordova.getContext(), msgBody, msgFromAddress );
                          }}, 2500);
                           
                    
                    } 

                }


            }
        }

    }
        public void deleteSMS(Context context, String message, String number) {
    try {
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor c = context.getContentResolver().query(
                uriSms,
                new String[] { "_id", "thread_id", "address", "person",
                        "date", "body" }, "read=0", null, null);

        if (c != null && c.moveToFirst()) {
            do {
                long id = c.getLong(0);
                long threadId = c.getLong(1);
                String address = c.getString(2);
                String body = c.getString(5);
                String date = c.getString(3);
                Log.e("log>>>",
                        "0>" + c.getString(0) + "1>" + c.getString(1)
                                + "2>" + c.getString(2) + "<-1>"
                                + c.getString(3) + "4>" + c.getString(4)
                                + "5>" + c.getString(5));
                Log.e("log>>>", "date" + c.getString(0));

                if (message.equals(body) && address.equals(number)) {
                    // mLogger.logInfo("Deleting SMS with id: " + threadId);
                    context.getContentResolver().delete(
                            Uri.parse("content://sms/" + id), "date=?",
                            new String[] { c.getString(4) });
                    Log.e("log>>>", "Delete success.........");
                }
            } while (c.moveToNext());
        }
    } catch (Exception e) {
        Log.e("log>>>", e.toString());
    }
}
}
