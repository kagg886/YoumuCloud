package com.kagg886.youmucloud.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static String appendStack(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter p = new PrintWriter(w);
        e.printStackTrace(p);
        return w.toString();
    }
}
