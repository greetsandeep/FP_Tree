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
			if(node.item>=0){
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
	
	/**
	 * Functions builds a new tree which is a sub-tree of tree object that called it
	 * @param endWith The integer with which should exist in all the leaves. Hence all other branches and nodes that come after this node are pruned out
	 * @param start The reference to the starting node of one of the 'endWith' nodes. All other nodes are linked to each other after this, with start referring to the beginning node
	 * @param subStartNode The list of references built in the new sub tree
	 * @return The new sub tree formed
	 */
	public FP_Tree subTree(int endWith, FP_Tree start, ArrayList<FP_Tree> subStartNode){
		FP_Tree sub = new FP_Tree(-1*endWith,null,0);
		FP_Tree curr = start;
		FP_Tree node = this;
		while(curr!=null){
			node=this;
			ArrayList<Integer> transaction = new ArrayList<Integer>();
			for(int i=0;i<curr.pos.size();i++){
				node = node.children.get(curr.pos.get(i)-1);
				transaction.add(node.item);
			}
			int tran[] = new int[transaction.size()];
			for(int i=0;i<transaction.size();i++){
				tran[i] = transaction.get(i);
			}
			for(int i=0;i<curr.count;i++)
				sub.addTransaction(tran, subStartNode);
			curr = curr.ref;
		}
		return sub;
	}
	
	/**
	 * @param startNode The list of references which refer to each kind of node item in the tree that called the function
	 * @param minsup The minimum support value
	 * @param subStartNode The list of references built in the new sub tree
	 * @param support The array of support values of each kind of node item in the current sub tree that called the function
	 * @return the new conditional sub tree based on pruning of nodes that don't cross the support count threshold
	 */
	public FP_Tree conditionalSubTree(ArrayList<FP_Tree> startNode, int minsup, ArrayList<FP_Tree> subStartNode,int support[]){
		FP_Tree condSub = new FP_Tree(this.item,null,0);
		FP_Tree node = this;
		FP_Tree curr = this;
		/*int support[] = new int[startNode.size()];
		for(int i=1;i<startNode.size();i++){
			curr = startNode.get(i);
			while(curr!=null){
				support[i] += curr.count;
				curr = curr.ref;
			}
		}*/
		curr = startNode.get(-1*this.item);
		while(curr!=null){
			node=this;
			ArrayList<Integer> transaction = new ArrayList<Integer>();
			for(int i=0;i<curr.pos.size()-1;i++){
				node = node.children.get(curr.pos.get(i)-1);
				if(support[node.item]>=minsup){
					transaction.add(node.item);
				}
			}
			if(transaction.size()>0){
				int tran[] = new int[transaction.size()];
				for(int i=0;i<transaction.size();i++){
					tran[i] = transaction.get(i);
				}
				for(int i=0;i<curr.count;i++)
					condSub.addTransaction(tran, subStartNode);
			}
			curr = curr.ref;
		}
		return condSub;
	}
	
	/**
	 * @return The next candidates to be considered along with the already previously considered for generating the frequent itemsets
	 */
	public ArrayList<Integer> getNextEnd(){
		ArrayList<Integer> nextEnd = new ArrayList<Integer>();
		ArrayList<FP_Tree> stack = new ArrayList<FP_Tree>();
		FP_Tree node = this;
		stack.add(this);
		while(!stack.isEmpty()){
			node = stack.get(0);
			for(int i=node.children.size()-1;i>=0;i--){
				stack.add(0,node.children.get(i));
			}
			if(node.item>0){
				if(!nextEnd.contains(node.item)){
					nextEnd.add(node.item);
				}
			}
			stack.remove(node);
		}
		return nextEnd;
	}
}
