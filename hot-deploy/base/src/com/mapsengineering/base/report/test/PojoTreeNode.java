package com.mapsengineering.base.report.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PojoTreeNode implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String value;
    private List<PojoTreeNode> children;

    public PojoTreeNode(String id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.children = new ArrayList<PojoTreeNode>();
    }

    public String getId() {
        //Debug.log("*** id=" + id);
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<PojoTreeNode> getChildren() {
        return children;
    }

    public PojoTreeNode add(PojoTreeNode child) {
        this.children.add(child);
        return this;
    }
}
