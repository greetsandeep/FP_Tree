package tree_fp;

import java.util.*;

public class FP_Tree {
	int count;
	int item;
	ArrayList<FP_Tree> children;
	FP_Tree ref;
	
	FP_Tree(int item){
		this.item = item;
		this.count = 1;
		ref = null;
		children.add(null);
	}
	
	public void addChildren(int item,FP_Tree parent){
		FP_Tree child = new FP_Tree(item);
		parent.children.add(child);
	}
	
}
