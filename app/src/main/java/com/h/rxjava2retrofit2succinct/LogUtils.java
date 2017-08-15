package com.h.rxjava2retrofit2succinct;

import android.util.Log;

import java.util.Hashtable;

/**
 * The class for print log
 *
 * @author kesenhoo
 */
public class LogUtils {
    private final static boolean logFlag = BuildConfig.DEBUG;

//    public final static String tag = "[AppName]";
    private final static int logLevel = Log.VERBOSE;
    private static Hashtable<String, LogUtils> sLoggerTable = new Hashtable<String, LogUtils>();
    private String mClassName;

    private static LogUtils jlog;
    private static LogUtils klog;

    private static final String HAMES = "@han@ ";
    private static final String KESEN = "@kesen@ ";

    private LogUtils(String name) {
        mClassName = name;
    }

    /**
     * @param className
     * @return
     */
    @SuppressWarnings("unused")
    private static LogUtils getLogger(String className) {
        LogUtils classLogger = (LogUtils) sLoggerTable.get(className);
        if (classLogger == null) {
            classLogger = new LogUtils(className);
            sLoggerTable.put(className, classLogger);
        }
        return classLogger;
    }

    /**
     * Purpose:Mark user one
     *
     * @return
     */
    public static LogUtils kLog() {
        if (klog == null) {
            klog = new LogUtils(KESEN);
        }
        return klog;
    }

    /**
     * Purpose:Mark user two
     *
     * @return
     */
    public static LogUtils hLog() {
        if (jlog == null) {
            jlog = new LogUtils(HAMES);
        }
        return jlog;
    }

    /**
     * Get The Current Function Name
     *
     * @return
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }
            return mClassName + "[ " + Thread.currentThread().getName() + ": "
                    + st.getFileName() + ":" + st.getLineNumber() + " "
                    + st.getMethodName() + " ]";
        }
        return null;
    }
    /**
     * The Log Level:i
     *
     * @param str
     */
    public void i(Object str) {
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(mClassName, name + " - " + str);
                } else {
                    Log.i(mClassName, str.toString());
                }
            }
        }

    }

    /**
     * The Log Level:i
     *
     * @param str
     */
    public void i(String tag ,Object str) {
        tag = tag+"----";
        if (logFlag) {
            if (logLevel <= Log.INFO) {
                String name = getFunctionName();
                if (name != null) {
                    Log.i(tag, name + " - " + str);
                } else {
                    Log.i(tag, str.toString());
                }
            }
        }

    }

    /**
     * The Log Level:d
     *
     * @param str
     */
    public void d(String tag ,Object str) {
        tag = tag+"----";
        if (logFlag) {
            if (logLevel <= Log.DEBUG) {
                String name = getFunctionName();
                if (name != null) {
                    Log.d(tag, name + " - " + str);
                } else {
                    Log.d(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:V
     *
     * @param str
     */
    public void v(String tag ,Object str) {
        tag = tag+"----";
        if (logFlag) {
            if (logLevel <= Log.VERBOSE) {
                String name = getFunctionName();
                if (name != null) {
                    Log.v(tag, name + " - " + str);
                } else {
                    Log.v(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:w
     *
     * @param str
     */
    public void w(String tag ,Object str) {
        tag = tag+"----";
        if (logFlag) {
            if (logLevel <= Log.WARN) {
                String name = getFunctionName();
                if (name != null) {
                    Log.w(tag, name + " - " + str);
                } else {
                    Log.w(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param str
     */
    public void e(String tag ,Object str) {
        tag = tag+"----";
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                String name = getFunctionName();
                if (name != null) {
                    Log.e(tag, name + " - " + str);
                } else {
                    Log.e(tag, str.toString());
                }
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param ex
     */
    public void e(String tag ,Exception ex) {
        tag = tag+"----";
        if (logFlag) {
            if (logLevel <= Log.ERROR) {
                Log.e(tag, "error", ex);
            }
        }
    }

    /**
     * The Log Level:e
     *
     * @param log
     * @param tr
     */
    public void e(String tag ,String log, Throwable tr) {
        tag = tag+"----";
        if (logFlag) {
            String line = getFunctionName();
            Log.e(tag, "{Thread:" + Thread.currentThread().getName() + "}"
                    + "[" + mClassName + line + ":] " + log + "\n", tr);
        }
    }
}