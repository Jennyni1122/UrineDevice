package com.leo.push.emui.agent.common;

import com.leo.push.emui.agent.common.handler.ICallbackCode;

/**
 * 回调线程
 */
public class CallbackCodeRunnable implements Runnable {

    private ICallbackCode handlerInner;
    private int rtnCodeInner;

    public CallbackCodeRunnable(ICallbackCode handler, int rtnCode) {
        handlerInner = handler;
        rtnCodeInner = rtnCode;
    }

    @Override
    public void run() {
        if (handlerInner != null) {
            handlerInner.onResult(rtnCodeInner);
        }
    }
}