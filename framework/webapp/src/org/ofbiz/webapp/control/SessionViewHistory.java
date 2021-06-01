package org.ofbiz.webapp.control;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;

public class SessionViewHistory {
    public static final String module = SessionViewHistory.class.getName();

    public static final boolean SET_OLD_SESSION_ATTRIBUTES = true;
    public static final String SESSION_SUFFIX_NAME = "NAME_";
    public static final String SESSION_SUFFIX_PARAMS = "PARAMS_";
    public static final String SESSION_SUFFIX_ATTRIBS = "ATTRIBS_";
    public static final String SESSION_SUFFIX_STACK = "STACK_";

    public static class Entry {
        protected String view;
        protected Map<String, Object> paramMap;
        protected Map<String, Object> attributeMap;

        protected Entry(String view, Map<String, Object> paramMap, Map<String, Object> attributeMap) {
            this.view = view;
            this.paramMap = paramMap;
            this.attributeMap = attributeMap;
        }

        public String getView() {
            return this.view;
        }

        public Map<String, Object> getParamMap() {
            return this.paramMap;
        }

        public Map<String, Object> getAttributeMap() {
            return this.attributeMap;
        }
    }

    protected List<Entry> viewHistory = FastList.newInstance();
    protected HttpSession session;
    protected String name;

    public static SessionViewHistory get(HttpServletRequest req, String name) {
        return get(req.getSession(), name);
    }

    public static SessionViewHistory get(HttpSession session, String name) {
        if (session == null)
            return null;
        String nameStack = name + SESSION_SUFFIX_STACK;
        SessionViewHistory instance = (SessionViewHistory)session.getAttribute(nameStack);
        if (instance == null) {
            instance = new SessionViewHistory(session, name);
            session.setAttribute(nameStack, instance);
        }
        return instance;
    }

    public SessionViewHistory(HttpSession session, String name) {
        this.session = session;
        this.name = name;
    }

    public boolean isEmpty() {
        return (this.viewHistory.size() <= 0);
    }

    public void push(String view, Map<String, Object> paramMap, Map<String, Object> attributeMap) {
        Entry entry = new Entry(view, paramMap, attributeMap);
        //org.ofbiz.base.util.Debug.log("**** push " + this.name + " " + this.viewHistory.size() + " " + entry.view, module);
        this.viewHistory.add(entry);
        if (SET_OLD_SESSION_ATTRIBUTES)
            setOldSessionAttributes(entry);
    }

    public void clear() {
        int size = this.viewHistory.size();
        if (size > 0)
            this.viewHistory.clear();
    }

    public Entry peek() {
        int size = this.viewHistory.size();
        if (size <= 0)
            return null;
        return this.viewHistory.get(size - 1);
    }

    public Entry pop() {
        int size = this.viewHistory.size();
        if (size <= 0)
            return null;
        Entry entry = this.viewHistory.remove(size - 1);
        //org.ofbiz.base.util.Debug.log("**** pop " + this.name + " " + this.viewHistory.size() + " " + entry.view, module);
        if (SET_OLD_SESSION_ATTRIBUTES)
            setOldSessionAttributes(peek());
        return entry;
    }

    protected void setOldSessionAttributes(Entry entry) {
        if (entry != null) {
            this.session.setAttribute(this.name + SESSION_SUFFIX_NAME, entry.view);
            this.session.setAttribute(this.name + SESSION_SUFFIX_PARAMS, entry.paramMap);
            this.session.setAttribute(this.name + SESSION_SUFFIX_ATTRIBS, entry.attributeMap);
        } else {
            this.session.removeAttribute(this.name + SESSION_SUFFIX_NAME);
            this.session.removeAttribute(this.name + SESSION_SUFFIX_PARAMS);
            this.session.removeAttribute(this.name + SESSION_SUFFIX_ATTRIBS);
        }
    }
}
