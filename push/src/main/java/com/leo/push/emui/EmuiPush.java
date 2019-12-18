package com.leo.push.emui;

import android.app.Activity;
import com.leo.push.emui.agent.HMSAgent;

/**
 * @author Leo
 * @date 2019-06-16
 */
public class EmuiPush {

    public static void init(Activity activity) {
        HMSAgent.init(activity);
        HMSAgent.connect(activity, rst -> {
        });
        HMSAgent.Push.getToken(rst -> {
        });
    }
}
