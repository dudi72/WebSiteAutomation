package com.dgsoft.websiteautomation;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver
{
    private static final String TAG = "com.example.readsmsmessage.SmsReceiver";

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null)
            {
                for (Object pdu : pdus)
                {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();
                    Log.d(TAG, "Received SMS from " + sender + ": " + messageBody);
//                    Toast.makeText(context,
//                                "Received SMS from " + sender + ": " + messageBody,
//                                Toast.LENGTH_LONG).show();

                    if (sender.equals("0768888600") || messageBody.contains("0768888600")) // Rami Levi Cellular
                    {
                        HandleSmsMessage(context, messageBody);
                    }
                }
            }
        }
    }

    private void HandleSmsMessage(Context context, String messageBody)
    {
        if (context instanceof MainActivity)
        {
            MainActivity activity = (MainActivity) context;

            // Get 6 digit number from message
            String regex = "\\b\\d{6}\\b";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(messageBody);

            if (matcher.find())
            {
                System.out.println("Found 6-digit number: " + matcher.group());
                activity.Authenticate(matcher.group());
            } else
            {
                System.out.println("No 6-digit number found.");
            }
        }
    }
}