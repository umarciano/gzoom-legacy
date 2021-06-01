package com.mapsengineering.base.util.closeable;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.base.util.Debug;

public class CloseablesStack implements Closeable {

    private static final String MODULE = CloseablesStack.class.getName();

    private List<WeakReference<Object>> closeables;
    private boolean autoclosing;

    public CloseablesStack() {
        autoclosing = false;
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    };

    @Override
    public void close() throws IOException {
        closeCloseables();
    }

    /**
     * 
     * @param obj
     */
    public void add(Object obj) {
        if (closeables == null) {
            closeables = new ArrayList<WeakReference<Object>>();
        }
        closeables.add(new WeakReference<Object>(obj));
        if (Debug.verboseOn()) {
            Debug.logVerbose("added: " + obj, MODULE);
        }
    }

    /**
     * Removes all occurrences of object and optionally closes objects after it.
     * @param obj the object to remove
     * @param closeNext if true closes all objects after obj
     */
    public void remove(Object obj, boolean closeNext) {
        if (!autoclosing && closeables != null) {
            int firstIndex = removeAllOf(obj);
            if (firstIndex >= 0 && closeNext) {
                closeFromLastToIndex(firstIndex);
            }
        }
    }

    public void remove(Object obj) {
        remove(obj, false);
    }

    /**
     * Removes all occurrences of object and returns its first index.
     * @param obj the object to remove
     * @return -1 if not found
     */
    protected int removeAllOf(Object obj) {
        int firstIndex = -1;
        for (int i = closeables.size() - 1; i >= 0; --i) {
            WeakReference<Object> ref = closeables.get(i);
            if (ref.get() == obj) {
                firstIndex = i;
                closeables.remove(i);
                if (Debug.verboseOn()) {
                    Debug.logVerbose("removed @" + i + ": " + obj, MODULE);
                }
            }
        }
        return firstIndex;
    }

    /**
     * Closes all objects from last to index.
     * @param index min index
     */
    protected void closeFromLastToIndex(int index) {
        try {
            autoclosing = true;
            for (int i = closeables.size() - 1; i >= index; --i) {
                Closeables.close(closeables.get(i), true);
                closeables.remove(i);
            }
        } finally {
            autoclosing = false;
        }
    }

    protected void closeCloseables() {
        if (closeables != null && !closeables.isEmpty()) {
            Debug.log("Closing closeables objects", MODULE);
            closeFromLastToIndex(0);
        }
    }
}
