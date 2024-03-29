package com.leo.push.emui.agent.common;

import java.io.Closeable;
import java.io.IOException;

/**
 * 工具类
 */
public final class IOUtils {
    public static void close(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (IOException e) {
                HMSAgentLog.d("close fail");
            }
        }
    }
}
