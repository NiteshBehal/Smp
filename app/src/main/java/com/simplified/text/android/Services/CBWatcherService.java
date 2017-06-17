package com.simplified.text.android.Services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.simplified.text.android.Receivers.TextCopyReceiver;
import com.simplified.text.android.utils.HtmlUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class CBWatcherService extends Service {
    private boolean isFirstTime = true;

    private OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };

    @Override
    public void onCreate() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb.hasPrimaryClip()) {
            ClipData cd = cb.getPrimaryClip();
            if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                if (cd != null && cd.getItemAt(0) != null && !TextUtils.isEmpty(cd.getItemAt(0).getText()) && isFirstTime) {
                    handleDuplicity();
                    sendBroadcast(cd.getItemAt(0).getText().toString(), "false");
                }
            } else if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {
                if (cd != null && cd.getItemAt(0) != null && !TextUtils.isEmpty(cd.getItemAt(0).getHtmlText()) && isFirstTime) {
                    handleDuplicity();
//                    sendBroadcast(HtmlUtil.fromHtml(cd.getItemAt(0).getHtmlText()).toString());
                    sendBroadcast(cd.getItemAt(0).getHtmlText().toString(), "true");

                }
            }
        }
    }

    private void handleDuplicity() {
        isFirstTime = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    isFirstTime = true;
                } catch (Exception ex) {
                }
            }
        }, 500);
    }


    private void sendBroadcast(String text, String isHtml) {
        Intent i = new Intent();
        i.putExtra(TextCopyReceiver.TEXT_COPIED_KEY, text);
        i.putExtra(TextCopyReceiver.TEXT_IS_HTML_KEY, isHtml);
        i.setAction(TextCopyReceiver.CUSTOM_INTENT);
        sendBroadcast(i);

//        sendToLocalServer(text);

    }

 /*   private void sendToLocalServer(final String text) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    InetAddress serverAddr = InetAddress.getByName("192.168.43.174");
                    Socket socket = new Socket("192.168.1.15", 8998);
                    PrintWriter outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    outStream.println(text);
                    outStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


    }*/
}