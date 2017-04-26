package tree_fp;

import java.util.*;

/**
 * @author Poojitha, Snehal, Sandeep
 *	Class that implements the node of the FP Tree.
 */
public class FP_Tree {
	/** The no. of transactions this element is present at this position */
	int count;
	
	/** The item represented by the node */
	int item;
	
	/** The position int the tree at which this node is present */
	ArrayList<Integer> pos;
	
	/** Children of this node */
	ArrayList<FP_Tree> children;
	
	/** Reference pointer to the next node of the same item */
	FP_Tree ref;
	
	/**
	 * @param item The item which this node represents
	 * @param parentPos The position of the parent in the tree
	 * @param pos The child number of this node 
	 */
	FP_Tree(int item, ArrayList<Integer> parentPos, int pos){
		this.item = item;
		this.count = 1;
		ref = null;
		children = new ArrayList<FP_Tree>();
		this.pos = new ArrayList<Integer>();
		if(parentPos!=null){
			for(int i=0;i<parentPos.size();i++){
				this.pos.add(parentPos.get(i));
			}
		this.pos.add(pos);
		}
	}
	
	
	/**
	 * @param item The item to be inserted in the children list of this node
	 */
	public void addChild(int item, ArrayList<FP_Tree> startNode){
		FP_Tree child = new FP_Tree(item,this.pos,this.children.size()+1);
		this.children.add(child);
		if(startNode.get(child.item)==null){
			startNode.set(child.item, child);
			startNode.set(child.item, child);
		}
		else{
			child.ref = startNode.get(child.item);
			startNode.set(child.item, child);
		}
	}
	
	
	/**
	 * @param transaction The transaction to be added in the tree. Takes transaction as an array of integers.
	 */
	public void addTransaction(int transaction[], ArrayList<FP_Tree> startNode){
		FP_Tree node = this;
		boolean flag = false;
		for(int i=0;i<transaction.length;i++){
			flag = false;
			for(int j=0;j<node.children.size();j++){
				if(node.children.get(j).item == transaction[i]){
					node = node.children.get(j);
					node.count = node.count + 1;
					flag = true;
					break;
				}
			}
			if(!flag){
				node.addChild(transaction[i],startNode);
				node = node.children.get(node.children.size()-1);
			}
		}
	}
	
	/**
	 * Traverses the tree rooted at this node in a depth-first fashion and prints the item, its count and it's position in the tree.
	 */
	public void treeTraversal(){
		ArrayList<FP_Tree> stack = new ArrayList<FP_Tree>();
		FP_Tree node = this;
		stack.add(this);
		while(!stack.isEmpty()){
			node = stack.get(0);
			for(int i=node.children.size()-1;i>=0;i--){
				stack.add(0,node.children.get(i));
			}
			if(node.item!=-1){
				System.out.print("(");
				for(int i=0;i<node.pos.size();i++){
					System.out.print(node.pos.get(i));
					if(i!=node.pos.size()-1)
						System.out.print(".");
				}
				System.out.print(") ");
				System.out.print(node.item+":");
				System.out.print(node.count+" ");
				if(node.ref!=null)
					System.out.println("   ref to "+node.ref.pos);	
				else
					System.out.println("   ref to null");
			}
			stack.remove(node);
		}
	}
	
	/* For refs - BFS (same as level order traversal in the tree) - to figure out the next pointer to point to. Maintain a list of current pointers (in sorted order so lookup is easy) */
}
