package org.ofbiz.widget.poi;

import org.ofbiz.base.util.Debug;

public class MethodCallTracer {

    private final int logLevel;
    private final String module;
    private final String prefix;
    private final String indentString;
    private final int stackIndex;
    private int indentLevel = 0;
    private int lastStackTraceLen = 0;

    protected MethodCallTracer(int logLevel, String module, String prefix, String indentString, int stackOffset) {
        if (prefix == null)
            prefix = "";
        if (indentString == null || indentString.length() < 1)
            indentString = "\t";
        if (stackOffset < 0)
            stackOffset = 0;
        this.logLevel = Debug.isOn(logLevel) ? logLevel : -1;
        this.module = module;
        this.prefix = prefix;
        this.indentString = indentString;
        this.stackIndex = 2 + stackOffset;
    }

    public void traceStack() {
        if (logLevel < 0) {
            return;
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > lastStackTraceLen) {
            lastStackTraceLen = stack.length;
            trace(1);
        } else if (stack.length < lastStackTraceLen) {
            lastStackTraceLen = stack.length;
            trace(-1);
        } else {
            trace(0);
        }
    }

    public void trace(int indentIncrement) {
        if (logLevel < 0) {
            return;
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        trace(stack, indentIncrement);
    }

    private void trace(StackTraceElement[] stack, int indentIncrement) {
        if (indentIncrement < 0)
            indentLevel += indentIncrement;
        StringBuilder msg = new StringBuilder(this.prefix);
        int i = indentLevel;
        while (--i >= 0)
            msg.append(this.indentString);
        msg.append(stack[this.stackIndex].getMethodName());
        Debug.log(this.logLevel, null, msg.toString(), this.module);
        if (indentIncrement > 0)
            indentLevel += indentIncrement;
    }
}
