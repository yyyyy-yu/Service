package com.example.service;

/**
 * Created by 29114 on 2020/12/4.
 */
public interface DownloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();
    void onPaused();
    void onCanceled();
}
