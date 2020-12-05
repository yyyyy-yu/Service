package com.example.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {
    private DownloadTask downloadTask;
    private String downloadUrl;
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("Downloading...",progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Paused", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_LONG).show();
        }
    };
    private DownloadBinder mBinder = new DownloadBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return  mBinder;
    }
    class DownloadBinder extends Binder{
        public void startDownload(String url) {
            if (downloadTask == null) {
                downloadUrl = url;
                downloadTask.execute(downloadUrl);
                startForeground(1, getNotification("Downloading...", 0));
                Toast.makeText(DownloadService.this,"Downloading...",Toast.LENGTH_LONG).show();
            }
        }
        public void pauseDownload() {
        if (downloadTask == null) {
            downloadTask.pauseDownload();
        }
    }
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancelDownload();
        }
        if(downloadUrl != null) {
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directoy = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file = new File(directoy + fileName);
            if (file.exists()) {
                file.delete();
            }
            getNotificationManager().cancel(1);
            stopForeground(true);
            Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_LONG).show();
        }
    }
    }
private NotificationManager getNotificationManager() {
    return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
}
    private Notification getNotification(String title,int progress) {
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pi = PendingIntent.getActivities(this,0, new Intent[]{intent},0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(progress >= 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}
