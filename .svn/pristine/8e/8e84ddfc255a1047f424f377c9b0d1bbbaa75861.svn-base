package com.mapsengineering.base.report.test;

import java.io.Serializable;

public class PojoTree implements Serializable {

    private static final long serialVersionUID = 1L;

    private String treeId;
    private PojoTreeNode root;

    public PojoTree(Object context) {
        //Debug.log("*** delegator=" + this.context.getDelegator().getDelegatorName());
        load();
    }

    public String getTreeId() {
        //Debug.log("*** treeId=" + treeId);
        return treeId;
    }

    public PojoTreeNode getRoot() {
        return root;
    }

    protected void load() {
        this.treeId = "tree-1";
        this.root = new PojoTreeNode("1", "root", "Root").add(new PojoTreeNode("1.1", "node-1.1", "Node 1.1")).add(new PojoTreeNode("1.2", "node-1.2", "Node 1.2").add(new PojoTreeNode("1.2.1", "node-1.2.1", "Node 1.2.1")).add(new PojoTreeNode("1.2.2", "node-1.2.2", "Node 1.2.2"))).add(new PojoTreeNode("1.3", "node-1.3", "Node 1.3"));
    }
}
