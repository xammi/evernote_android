package com.company.evernote_android.sync.processor;

/**
 * Created by Zalman on 16.04.2015.
 */
public interface ProcessorCallback {

    void send(int resultCode, String requestType);

}
