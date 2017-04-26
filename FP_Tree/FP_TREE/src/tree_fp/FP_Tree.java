package tree_fp;

import java.util.*;

public class FP_Tree {
	int count;
	int item;
	ArrayList<Integer> pos;
	ArrayList<FP_Tree> children;
	FP_Tree ref;
	
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
	
	public void addChild(int item){
		FP_Tree child = new FP_Tree(item,this.pos,this.children.size()+1);
		this.children.add(child);
	}
	
	public void addTransaction(int transaction[]){
		FP_Tree node = this;
		boolean flag = false;
		for(int i=0;i<transaction.length;i++){
			flag = false;
			for(int j=0;j<node.children.size();j++){
				if(node.children.get(j).item == transaction[i]){
					node = node.children.get(j);
					node.count = node.count + 1;
					flag = true;
				}
			}
			if(!flag){
				node.addChild(transaction[i]);
				node = node.children.get(node.children.size()-1);
			}
		}
	}
	
	public void treeTraversal(){
		ArrayList<FP_Tree> stack = new ArrayList<FP_Tree>();
		FP_Tree node = this;
		stack.add(this);
		while(!stack.isEmpty()){
			node = stack.get(0);
			for(int i=node.children.size()-1;i>=0;i--){
				stack.add(0,node.children.get(i));
					System.out.println(i);
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
				System.out.println(node.count);
			}
			stack.remove(0);
		}
	}
}
