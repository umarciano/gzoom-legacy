package com.mapsengineering.base.util.closeable;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import org.ofbiz.base.util.Debug;

public final class Closeables {

    private static final String MODULE = Closeables.class.getName();

    private static final ThreadLocal<CloseablesStack> TL = new ThreadLocal<CloseablesStack>();

    private Closeables() {
    }

    /**
     * Check if the object is closed
     * @param obj
     * @return true if null or closed
     */
    public static <T extends Object> boolean isClosed(T obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Iterator<?>) {
            Iterator<?> it = (Iterator<?>)obj;
            return !it.hasNext();
        }
        return false;
    }

    /**
     * Closes a closeable object or a WeakReference to it
     * @param obj the object to close
     * @param warning if true enable logging
     * @return always null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T close(final T obj, boolean warning) {
        T currObj = obj;
        if (currObj instanceof WeakReference<?>) {
            WeakReference<T> ref = (WeakReference<T>)currObj;
            currObj = ref.get();
            ref.clear();
        }
        if (currObj instanceof Closeable) {
            try {
                if (warning) {
                    Debug.logWarning("Closing object: " + currObj, MODULE);
                }
                ((Closeable)currObj).close();
            } catch (IOException e) {
                if (Debug.verboseOn()) {
                    Debug.logVerbose(e, "Error closing: " + currObj, MODULE);
                }
            }
        }
        return null;
    }

    /**
     * Closes a closeable object or a WeakReference to it
     * @param obj the object to close
     * @return always null
     */
    public static <T extends Object> T close(T obj) {
        return close(obj, false);
    }

    /**
     * Registers an object to be closed 
     * @param obj
     */
    public static <T extends Object> void add(T obj) {
        CloseablesStack closeables = TL.get();
        if (closeables == null) {
            closeables = new CloseablesStack();
            TL.set(closeables);
        }
        closeables.add(obj);
    }

    /**
     * Unregister an object to be closed
     * @param obj
     * @param closeNext if true closes all objects after obj
     */
    public static <T extends Object> void remove(T obj, boolean closeNext) {
        CloseablesStack closeables = TL.get();
        if (closeables != null) {
            closeables.remove(obj, closeNext);
        }
    }

    /**
     * Unregister an object to be closed
     * @param obj
     */
    public static <T extends Object> void remove(T obj) {
        remove(obj, false);
    }
}
