package org.ofbiz.widget;

public class Paginator {
	private int viewIndex;
	private int viewSize;
	private int highIndex;
	private int listSize;
	
	public Paginator(int viewIndex, int viewSize, int highIndex, int listSize) {
		this.viewIndex = viewIndex;
		this.viewSize = viewSize;
		this.highIndex = highIndex;
		this.listSize = listSize;
	}
	
	public boolean showFirstPage() {
		return viewIndex > 0;
	}
	
	public boolean showPreviousPage() {
		return viewIndex > 0;
	}
	
	public boolean showNextPage() {
		return highIndex < listSize;
	}
	
	public boolean showLastPage() {
		return highIndex < listSize;
	}
	
	public int getFirstPage() {
		return 0;
	}
	
	public int getPreviousPage() {
		return viewIndex - 1;
	}
	
	public int getNextPage() {
		return viewIndex + 1;
	}
	
	public int getLastPage() {
		int q = listSize / viewSize;
		int r = listSize % viewSize;
		return r > 0 ? q : (q - 1);
	}

}
