package com.leo.push.miui;

import android.content.Context;
import android.content.Intent;
import com.blankj.utilcode.util.LogUtils;
import com.leo.push.Push;
import com.xiaomi.mipush.sdk.*;

import java.util.List;

/**
 * @author Leo
 * @date 2019-06-16
 */
public class MiuiReceiver extends PushMessageReceiver {
    private String mRegId;
    private long mResultCode = -1;
    private String mReason;
    private String mCommand;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        LogUtils.i("MiuiReceiver", "onReceivePassThroughMessage");
        Intent intent = new Intent();
        intent.setAction("com.leo.device.dataService");
        context.sendBroadcast(intent);
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        LogUtils.i("MiuiReceiver", "onNotificationMessageClicked");
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        LogUtils.i("MiuiReceiver", "onNotificationMessageArrived");
        Intent intent = new Intent();
        intent.setAction("com.leo.device.dataService");
        context.sendBroadcast(intent);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        LogUtils.i("MiuiReceiver", "onCommandResult");
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                Push.setToken(mRegId);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }
}
