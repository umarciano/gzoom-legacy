package com.mapsengineering.base.util;

import java.util.ArrayList;
import java.util.List;

final class OfbizServiceContextStack {

    private static final ThreadLocal<List<OfbizServiceContext>> STACK = new ThreadLocal<List<OfbizServiceContext>>();

    private OfbizServiceContextStack() {
    }

    public static OfbizServiceContext get() {
        List<OfbizServiceContext> stack = STACK.get();
        if (stack != null && !stack.isEmpty()) {
            return stack.get(stack.size() - 1);
        }
        return null;
    }

    public static void add(OfbizServiceContext ctx) {
        List<OfbizServiceContext> stack = STACK.get();
        if (stack == null) {
            stack = new ArrayList<OfbizServiceContext>();
            STACK.set(stack);
        }
        stack.add(ctx);
    }

    public static void remove(OfbizServiceContext ctx) {
        List<OfbizServiceContext> stack = STACK.get();
        if (stack != null) {
            for (int i = stack.size() - 1; i >= 0; --i) {
                if (stack.get(i) == ctx) {
                    stack.remove(i);
                }
            }
        }
    }
}
