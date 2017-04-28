import java.util.*;
import java.io.*;

public class FP_Growth {
	public static ArrayList<int []> data = new ArrayList<int[]>();
	
	public static ArrayList<FP_Tree> startNode = new ArrayList<FP_Tree>();
	
	public static HashMap<TreeSet<Integer>, Integer> itemWithSupport = new HashMap<TreeSet<Integer>, Integer>();
	
	public static HashMap<ArrayList<TreeSet<Integer>>, Double> confidentRules = new HashMap<ArrayList<TreeSet<Integer>>, Double>();
	
	public static void main(String args[]){
		PreProcessing pre = new PreProcessing("rule.data");
		int[] row;
		for(int i=0;i<pre.expandedData.size();i++){
			row = new int[9];
			for(int j=0;j<pre.expandedData.get(i).length;j++){
				row[j] = pre.expandedData.get(i)[j];
			}
			data.add(i,row);
		}
		
		long startTime = System.currentTimeMillis();
		
		FP_Tree fp = new FP_Tree(-1,null,0);
		
		for(int i=0;i<pre.support.length;i++){
			startNode.add(null);
		}
		
		/*int test1[] = {1,2};
		int test2[] = {2,3,4};
		int test3[] = {1,3,4,5};
		int test4[] = {1,4,5};
		int test5[] = {1,2,3};
		int test6[] = {1,2,3,4};
		int test7[] = {1};
		int test8[] = {1,2,3};
		int test9[] = {1,2,4};
		int test10[] = {2,3,5};
		
		fp.addTransaction(test1, startNode);
		fp.addTransaction(test2, startNode);
		fp.addTransaction(test3, startNode);
		fp.addTransaction(test4, startNode);
		fp.addTransaction(test5, startNode);
		fp.addTransaction(test6, startNode);
		fp.addTransaction(test7, startNode);
		fp.addTransaction(test8, startNode);
		fp.addTransaction(test9, startNode);
		fp.addTransaction(test10, startNode);
  		
		int support[] = {0,8,7,6,5,3};*/
 		
		for(int i=0;i<data.size();i++){
			fp.addTransaction(data.get(i), startNode);
		}
		
		int minsup = 153;
		double minConfidence = 0.9;
		
		FP_Growth fpg = new FP_Growth();
		TreeSet<Integer> set = new TreeSet<Integer>();
		for(int i=1;i<pre.support.length;i++){
			if(pre.support[i]>minsup){
				fpg.generateItemsets(fp, i, minsup, pre.support,set,startNode);
			}
		}
		System.out.println("Support: "+minsup+"\n"+"Confidence: "+minConfidence);
		System.out.println("Total No. of Frequent Itemsets: "+itemWithSupport.size());
		
		/*for(Map.Entry<TreeSet<Integer>, Integer> e: itemWithSupport.entrySet()){
			System.out.println(e.getKey()+" "+e.getValue());
		}*/
		
		long supportStopTime = System.currentTimeMillis();
		System.out.println("\nThe Time elapsed to find all frequent Item Subsets: " + (supportStopTime-startTime)+" milliseconds\n");
		
		System.out.println("Rules Generated: ");
		confidentRuleGen(minConfidence);
		
		
		System.out.println(" ");
		System.out.println("No of Rules: "+confidentRules.size());
		System.out.println(" ");
		
		long finalStopTime = System.currentTimeMillis();
		System.out.println("The Time elapsed for confidence pruning:  " + (finalStopTime-supportStopTime)+" milliseconds");
		System.out.println("The Total Time for generating all rules: " + (finalStopTime-startTime)+" milliseconds");
		
	}
	
	public void generateItemsets(FP_Tree tree, int endWith, int minsup, int support[],TreeSet<Integer> subSet, ArrayList<FP_Tree> startNode){
		TreeSet<Integer> set = new TreeSet<Integer>(subSet);
		ArrayList<Integer> candidates = new ArrayList<Integer>();
		ArrayList<FP_Tree> subStartNode = new ArrayList<FP_Tree>();
		ArrayList<FP_Tree> condSubStartNode = new ArrayList<FP_Tree>();
		set.add(endWith);
		itemWithSupport.put(set,support[endWith]);
		support = new int[support.length];
		FP_Tree sub = new FP_Tree((-1)*endWith,null,0);
		for(int j=0;j<support.length;j++){
			subStartNode.add(null);
		}
		sub = tree.subTree(endWith, startNode.get(endWith), subStartNode);
		FP_Tree curr = tree;
		for(int i=1;i<subStartNode.size();i++){
			curr = subStartNode.get(i);
			while(curr!=null){
				support[i] += curr.count;
				curr = curr.ref;
			}
		}
		FP_Tree condSub = new FP_Tree(-1*endWith,null,0);
		for(int j=0;j<support.length;j++){
			condSubStartNode.add(null);
		}
		condSub = sub.conditionalSubTree(subStartNode, minsup, condSubStartNode,support);
		candidates = condSub.getNextEnd();
		for(int i = 0;i<candidates.size();i++){
			generateItemsets(condSub,candidates.get(i),minsup,support,set,condSubStartNode);
		}
	}
	
	/**
	 * @param minConf The Confidence threshold below which any rule would be pruned out for not having sufficient confidence
	 * This function checks confidence of only those rules who have the element 'democrat' or 'republican' in them.
	 * It passes such rules to the isConfRule() method
	 */
	public static void confidentRuleGen(double minConf){
		
			for(Map.Entry<TreeSet<Integer>, Integer> f: itemWithSupport.entrySet()){
				
				HashMap<TreeSet<Integer>,TreeSet<Integer>> can = candidatesForConfidence(f.getKey());
				for(Map.Entry<TreeSet<Integer>, TreeSet<Integer>> e : can.entrySet()){
					isConfRule(e,minConf);
				}
			}
		
	}

	/**
	 * @param e The Rule in the set format whose confidence is to be tested
	 * @param minConf The Confidence threshold below which any rule would be pruned out for not having sufficient confidence
	 * This function checks the confidence of each rule and adds only those rules to the global variable : <b> confidentRules </b> who have confidence more than the minimum threshold
	 * This function also prints all these rules in sentence format by referencing the DataRef Class
	 */
	public static void isConfRule(Map.Entry<TreeSet<Integer>, TreeSet<Integer>> e, double minConf){
		TreeSet<Integer> lhs = new TreeSet<Integer>(e.getKey());
		TreeSet<Integer> rhs = new TreeSet<Integer>(e.getValue());
 		TreeSet<Integer> union = new TreeSet<Integer>();
 		union.addAll(lhs);
 		union.addAll(rhs);
		double conf = (double)itemWithSupport.get(union)/(double)itemWithSupport.get(lhs); 
		
		DataRef dref = new DataRef();
		
		ArrayList<TreeSet<Integer>> temp = new ArrayList<TreeSet<Integer>>();
		temp.add(lhs);
		temp.add(rhs);
		
		if(conf >= minConf){
			confidentRules.put(temp, conf);
			//System.out.println("No of Rules: "+confidentRules.size());
			for(Integer i:temp.get(0)){
				if(i<=6){
					System.out.print(dref.classes[0]+":"+DataRef.subclasses.get(1)[i-1]+" ");
				}
				else if(i<=11){
					System.out.print(dref.classes[1]+":"+DataRef.subclasses.get(2)[i-7]+" ");
				}
				else if(i<=15){
					System.out.print(dref.classes[2]+":"+DataRef.subclasses.get(3)[i-12]+" ");
				}
				else if(i<=20){
					System.out.print(dref.classes[3]+":"+DataRef.subclasses.get(4)[i-16]+" ");
				}
				else if(i<=25){
					System.out.print(dref.classes[4]+":"+DataRef.subclasses.get(5)[i-21]+" ");
				}
				else if(i<=28){
					System.out.print(dref.classes[5]+":"+DataRef.subclasses.get(6)[i-26]+" ");
				}
				else if(i<=33){
					System.out.print(dref.classes[6]+":"+DataRef.subclasses.get(7)[i-29]+" ");
				}
				else if(i<=38){
					System.out.print(dref.classes[7]+":"+DataRef.subclasses.get(8)[i-34]+" ");
				}
				else if(i<=40){
					System.out.print(dref.classes[8]+":"+DataRef.subclasses.get(9)[i-39]+" ");
				}
		    }
			System.out.print(" ---> ");
			for(Integer i:temp.get(1)){
				if(i<=6){
					System.out.print(dref.classes[0]+":"+DataRef.subclasses.get(1)[i-1]+" ");
				}
				else if(i<=11){
					System.out.print(dref.classes[1]+":"+DataRef.subclasses.get(2)[i-7]+" ");
				}
				else if(i<=15){
					System.out.print(dref.classes[2]+":"+DataRef.subclasses.get(3)[i-12]+" ");
				}
				else if(i<=20){
					System.out.print(dref.classes[3]+":"+DataRef.subclasses.get(4)[i-16]+" ");
				}
				else if(i<=25){
					System.out.print(dref.classes[4]+":"+DataRef.subclasses.get(5)[i-21]+" ");
				}
				else if(i<=28){
					System.out.print(dref.classes[5]+":"+DataRef.subclasses.get(6)[i-26]+" ");
				}
				else if(i<=33){
					System.out.print(dref.classes[6]+":"+DataRef.subclasses.get(7)[i-29]+" ");
				}
				else if(i<=38){
					System.out.print(dref.classes[7]+":"+DataRef.subclasses.get(8)[i-34]+" ");
				}
				else if(i<=40){
					System.out.print(dref.classes[8]+":"+DataRef.subclasses.get(9)[i-39]+" ");
				}
		    }
			System.out.println(" "+conf);
			System.out.println(" ");
		}
	}
	
	/**
	 * @param originalSet The Set of which we have to find all the possible Sub sets
	 * @return All the possible sub sets of the given Set including null set and the set itself
	 */
	public static Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
        Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<Integer>());
            return sets;
        }
        List<Integer> list = new ArrayList<Integer>(originalSet);
        Integer head = list.get(0);
        Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
        
        for (Set<Integer> set : powerSet(rest)) {
            Set<Integer> newSet = new HashSet<Integer>();
            newSet.add(head);
            newSet.addAll(set);
    		sets.add(set);
    		sets.add(newSet);
        }

        return sets;
    }
	
	/**
	 * @param rule The set currently under consideration
	 * @return Returns the possible candidates for confidence pruning. Eliminates Null gives all and vice versa rules.
	 */
	public static HashMap<TreeSet<Integer>,TreeSet<Integer>> candidatesForConfidence(TreeSet<Integer> rule){
		HashMap<TreeSet<Integer>,TreeSet<Integer>> candidates = new HashMap<TreeSet<Integer>,TreeSet<Integer>>();
		Set<Set<Integer>> temp = powerSet(rule);

		for (Set<Integer> s : temp) {
		    if(!(s.size()==0 || s.size()==rule.size()))
		    {
		    	Set<Integer> tempSet = new HashSet<Integer>(rule);
		    	tempSet.removeAll(s);
		    	candidates.put(new TreeSet<Integer>(s),new TreeSet<Integer>(tempSet));
		    }
		}
		return candidates;
	}
}

class FP_Tree {
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

class PreProcessing {
	public ArrayList<int []> data = new ArrayList<int[]>();
	public ArrayList<int []> expandedData = new ArrayList<int[]>();
	//public ArrayList<int []> sortedExpandedData = new ArrayList<int[]>();
	public int support[] = new int[41];
	public PreProcessing(String filename) {
		try{
			inputHandle(filename,data);
		}catch(Exception e){
			System.out.println("\nError In file Reading " + e);
		}

		expand(data,expandedData);
		support = calcSupport(expandedData);
		/*for(int i=0;i<expandedData.size();i++){
			for(int j=0;j<expandedData.get(i).length;j++){
				System.out.print(expandedData.get(i)[j]+" ");
			}
			System.out.println(" ");
					
		}
		System.out.println("Support Count: ");
		for(int j=1;j<support.length;j++){
			System.out.print(support[j] +" ");
		}
		System.out.println(" ");
		System.out.println("After Sorting: ");*/
		sort(expandedData,support);
		/*for(int i=0;i<expandedData.size();i++){
			for(int j=0;j<expandedData.get(i).length;j++){
				System.out.print(expandedData.get(i)[j]+" ");
			}
			System.out.println(" ");
					
		}*/
		
	}

	public void inputHandle(String filename,ArrayList<int[]> data)throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line=null;


		ArrayList<Integer> pregnancy = new ArrayList<Integer>();
		ArrayList<Integer> plasma = new ArrayList<Integer>();
		ArrayList<Integer> blood_pressure = new ArrayList<Integer>();
		ArrayList<Integer> skinFold = new ArrayList<Integer>();
		ArrayList<Integer> serum = new ArrayList<Integer>();
		ArrayList<Double> bmi = new ArrayList<Double>();
		ArrayList<Double> pedigree = new ArrayList<Double>();
		ArrayList<Integer> age = new ArrayList<Integer>();
		ArrayList<Integer> diabetes = new ArrayList<Integer>();

		while((line=br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line,",");

			while(st.hasMoreTokens()){
				pregnancy.add(Integer.parseInt(st.nextToken()));
				plasma.add(Integer.parseInt(st.nextToken()));
				blood_pressure.add(Integer.parseInt(st.nextToken()));
				skinFold.add(Integer.parseInt(st.nextToken()));
				serum.add(Integer.parseInt(st.nextToken()));
				bmi.add(Double.parseDouble(st.nextToken()));
				pedigree.add(Double.parseDouble(st.nextToken()));
				age.add(Integer.parseInt(st.nextToken()));
				diabetes.add(Integer.parseInt(st.nextToken()));
			}
		} 


		discPregnancy(pregnancy);
		discPlasma(plasma);
		discBP(blood_pressure);
		discSkinFold(skinFold);
		discSerum(serum);
		discBMI(bmi);
		discPedigree(pedigree);
		discAge(age);
		discDiabetes(diabetes);

		handleMissing(age);
		handleMissing(serum);
		handleMissing(skinFold);
		handleMissing(blood_pressure);
		handleMissing(plasma);
		handleMissing(pregnancy);

		handleMissingDouble(pedigree);
		handleMissingDouble(bmi);


		for(int i=0;i<pregnancy.size();i++)
		{
			int row[] = new int[9];
			row[0] = pregnancy.get(i);
			row[1] = plasma.get(i);
			row[2] = blood_pressure.get(i);
			row[3] = skinFold.get(i);
			row[4] = serum.get(i);
			row[5] = bmi.get(i).intValue();
			row[6] = pedigree.get(i).intValue();
			row[7] = age.get(i);
			row[8] = diabetes.get(i);
			data.add(i,row);
		}
		br.close();
	}

	/**
	 * @param pregnancy Takes in an ArrayList of Pregnancies and classifies into sub categories as mentioned.
	 * The Classification Principle is Equal Frequency Classes
	 */
	public void discPregnancy(ArrayList<Integer> pregnancy){
		for(int i=0;i<pregnancy.size();i++)
		{
			if(pregnancy.get(i)==0)
				pregnancy.set(i,1);
			else if(pregnancy.get(i)==1)
				pregnancy.set(i,2);
			else if(pregnancy.get(i)==2)
				pregnancy.set(i,3);
			else if(pregnancy.get(i)==3 ||pregnancy.get(i)==4)
				pregnancy.set(i,4);
			else if(pregnancy.get(i)>=5 && pregnancy.get(i)<=7)
				pregnancy.set(i,5);
			else
				pregnancy.set(i,6);
		}
		String categories[] = {"0","1","2",">=3 and <=4",">=5 and <=7",">7"};
		DataRef.subclasses.put(1,categories);
	}

	/**
	 * @param plasma Takes in an ArrayList of Plasma Glucose Conc. and classifies into sub categories as mentioned.
	 * The Classification Principle is Equal Frequency Classes
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discPlasma(ArrayList<Integer> plasma){
		for(int i=0;i<plasma.size();i++)
		{
			if(plasma.get(i)==0)
				plasma.set(i,-1);
			else if(plasma.get(i)>=44 &&plasma.get(i)<99)
				plasma.set(i,1);
			else if(plasma.get(i)>=99 && plasma.get(i)<111)
				plasma.set(i,2);
			else if(plasma.get(i)>=111 && plasma.get(i)<127)
				plasma.set(i,3);
			else if(plasma.get(i)>=127 && plasma.get(i)<=150)
				plasma.set(i,4);
			else
				plasma.set(i,5);
		}
		String categories[] = {"44-98","99-110","111-126","127-150","150+"};
		DataRef.subclasses.put(2,categories);
	}

	/**
	 * @param blood_pressure Takes in an ArrayList of Blood Pressures and classifies into sub categories as mentioned. 
	 * The classification into classes is based on the domain knowledge.
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discBP(ArrayList<Integer> blood_pressure){
		for(int i=0;i<blood_pressure.size();i++)
		{
			if(blood_pressure.get(i)==0)
				blood_pressure.set(i,-1);
			else if(blood_pressure.get(i)<=60)
				blood_pressure.set(i,1);
			else if(blood_pressure.get(i)<=80)
				blood_pressure.set(i,2);
			else if(blood_pressure.get(i)<=90)
				blood_pressure.set(i,3);
			else
				blood_pressure.set(i,4);
		}
		String categories[] = {"<=60",">60 and <=80",">80 and <=90",">90"};
		DataRef.subclasses.put(3,categories);
	}

	/**
	 * @param skinFold Takes in an ArrayList of Triceps skin fold thickness and classifies into sub categories as mentioned. 
	 * The Classification Principle is Equal Frequency Classes
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discSkinFold(ArrayList<Integer> skinFold){

		for(int i=0;i<skinFold.size();i++)
		{
			if(skinFold.get(i)==0)
				skinFold.set(i,-1);
			else if(skinFold.get(i)<=19)
				skinFold.set(i,1);
			else if(skinFold.get(i)<=26)
				skinFold.set(i,2);
			else if(skinFold.get(i)<=31)
				skinFold.set(i,3);
			else if(skinFold.get(i)<=38)
				skinFold.set(i,4);
			else
				skinFold.set(i,5);
		}
		String categories[] = {"<=19",">19 and <=26",">26 and <=31",">31 and <=38",">38"};
		DataRef.subclasses.put(4,categories);
	}
	/**
	 * @param serum Takes in an ArrayList of 2-Hour serum insulin (mu U/ml) and classifies into sub categories as mentioned. 
	 * The Classification Principle is Equal Frequency Classes
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discSerum(ArrayList<Integer> serum){

		for(int i=0;i<serum.size();i++)
		{
			if(serum.get(i)==0)
				serum.set(i,-1);
			else if(serum.get(i)<=70)
				serum.set(i,1);
			else if(serum.get(i)<=106)
				serum.set(i,2);
			else if(serum.get(i)<=150)
				serum.set(i,3);
			else if(serum.get(i)<=210)
				serum.set(i,4);
			else
				serum.set(i,5);
		}
		String categories[] = {"<=70",">70 and <=106",">106 and <=150",">150 and <=210",">210"};
		DataRef.subclasses.put(5,categories);
	}


	/**
	 * @param bmi Takes in an ArrayList of BMIs and classifies into sub categories as mentioned.
	 * The classification into classes is based on the domain knowledge.
	 * The function feeds -1 in case of missing values which in this data set have been represented by 0
	 */
	public void discBMI(ArrayList<Double> bmi){
		for(int i=0;i<bmi.size();i++)
		{
			if(bmi.get(i)==0)
				bmi.set(i,-1.0);
			else if(bmi.get(i)<=18.5)
				bmi.set(i,1.0);
			else if(bmi.get(i)<=25)
				bmi.set(i,2.0);
			else
				bmi.set(i,3.0);
		}
		String categories[] = {"<=18.5",">18.5 and <=25",">25"};
		DataRef.subclasses.put(6,categories);
	}


	/**
	 * @param pedigree Takes in an ArrayList of Diabetes pedigree function and classifies into sub categories as mentioned.
	 * This discretisation function uses the equal width strategy to classify data
	 */
	public void discPedigree(ArrayList<Double> pedigree){
		double min = 100,max = 0;
		for(int i=0;i<pedigree.size();i++){
			if(pedigree.get(i)>max)
				max = pedigree.get(i);
			if(pedigree.get(i)<min)
				min = pedigree.get(i);
		}
		double width = (max-min)/5;
		for(int i=0;i<pedigree.size();i++)
		{
			if(pedigree.get(i)==0)
				pedigree.set(i,-1.0);
			else
			{
				for(int j=1;j<6;j++)
				{
					if(pedigree.get(i)<= min+(j*width))
					{
						pedigree.set(i,(double)j);
						break;
					}
				}
			}
		}
		String temp[] = new String[5];
		for(int i=1;i<6;i++)
		{
			String temp1 =" >= "+ Double.toString(min+(i-1)*width)+ " and <= " + Double.toString(min+i*width);
			temp[i-1] = temp1;
		}
		DataRef.subclasses.put(7,temp);
	}

	/**
	 * @param age Takes in an ArrayList of Ages and classifies into sub categories as mentioned.
	 * This discretization function uses the equal width strategy to classify data
	 * Missing data is represented as -1
	 */
	public void discAge(ArrayList<Integer> age){
		int min = 100,max = 0;
		for(int i=0;i<age.size();i++){
			if(age.get(i)>max)
				max = age.get(i);
			if(age.get(i)<min)
				min = age.get(i);
		}
		int width = (max-min)/5;
		for(int i=0;i<age.size();i++)
		{
			if(age.get(i)==0)
				age.set(i,-1);
			else
			{
				for(int j=1;j<6;j++)
				{
					if(age.get(i)<= min+(j*width))
					{
						age.set(i,j);
						break;
					}
				}
			}
		}
		String temp[] = new String[5];
		for(int i=1;i<6;i++)
		{
			String temp1 =" >= "+ Integer.toString(min+(i-1)*width)+ " and <= " + Integer.toString(min+i*width);
			temp[i-1] = temp1;
		}
		DataRef.subclasses.put(8,temp);
	}
	
	public void discDiabetes(ArrayList<Integer> diabetes){
		for(int i=0;i<diabetes.size();i++)
		{
			if(diabetes.get(i)==0)
				diabetes.set(i,1);
			
			else
				diabetes.set(i,2);
		}
		String categories[] = {"No","Yes"};
		DataRef.subclasses.put(9,categories);
	}

	/**
	 * @param list The list which contains Missing Values
	 * Fills the missing values with the class which occurs the most.
	 */
	public void handleMissing(ArrayList<Integer> list){
		TreeSet<Integer> unique = new TreeSet<Integer>();
		for(int i=0;i<list.size();i++)
			unique.add(list.get(i));

		HashMap<Integer, Integer> freqTable = new HashMap<Integer,Integer>();

		for(Integer temp : unique)
			freqTable.put(temp,0);


		for(Integer temp : list)
			freqTable.put(temp, freqTable.get(temp)+1);

		int max = 0;
		for(Integer temp : unique)
			if(freqTable.get(temp)>max)
				max = temp;

		for(int i=0;i<list.size();i++)
			if(list.get(i)==-1)
				list.set(i,max);
	}
	/**
	 * @param list The list which contains Missing Values
	 * Fills the missing values with the class which occurs the most.
	 */
	public void handleMissingDouble(ArrayList<Double> list){
		TreeSet<Double> unique = new TreeSet<Double>();
		for(int i=0;i<list.size();i++)
			unique.add(list.get(i));

		HashMap<Double, Integer> freqTable = new HashMap<Double,Integer>();

		for(Double temp : unique)
			freqTable.put(temp,0);

		for(Double temp : unique)
			freqTable.put(temp, freqTable.get(temp)+1);

		double max = 0;
		for(Double temp : unique)
			if(freqTable.get(temp)>max)
				max = temp;

		for(int i=0;i<list.size();i++)
			if(list.get(i)==-1.0)
				list.set(i,max);
	}


	/**
	 * @param row the row which we want to print
	 * An utility function that prints the passed array
	 */
	public static void printArray(int []row){
		for(int i=0;i<row.length;i++)
			System.out.print(row[i]+" ");
		System.out.println();
	}
	public static void printArray(double []row){
		for(int i=0;i<row.length;i++)
			System.out.print(row[i]+" ");
		System.out.println();
	}

	/**
	 * @param data The data to be handled
	 * @param expandedData The representation of data considering each category to be a product
	 */
	public static void expand(ArrayList<int []> data, ArrayList<int []> expandedData){
		for(int i=0;i<data.size();i++)
		{
			int row[] = new int[9];
			row[0] = data.get(i)[0];
			row[1] = data.get(i)[1]+6;
			row[2] = data.get(i)[2]+11;
			row[3] = data.get(i)[3]+15;
			row[4] = data.get(i)[4]+20;
			row[5] = data.get(i)[5]+25;
			row[6] = data.get(i)[6]+30;
			row[7] = data.get(i)[7]+33;
			row[8] = data.get(i)[8]+38;
			expandedData.add(i,row);
		}
	}

	/**
	 * @param list The list of all transactions
	 * @return The Support of each product
	 */
	public int[] calcSupport(ArrayList<int []> list){
		int row[] =  new int[41];
		for(int i=0;i<list.size();i++)
			for(int j=0;j<list.get(i).length;j++)
				row[list.get(i)[j]]++;

		return row;
	}

	public void sort(ArrayList<int []> toSort, int []support)
	{
		
		
		for(int c =0; c<toSort.size();c++){
			
			for(int i=0;i<(toSort.get(c).length - 1);i++)
			{
				for(int j=0;j<(toSort.get(c).length - i - 1);j++){
					if(support[toSort.get(c)[j]] < support[toSort.get(c)[j+1]]){
						int temp;
						temp = toSort.get(c)[j];
						toSort.get(c)[j] = toSort.get(c)[j+1];
						toSort.get(c)[j+1] = temp;
					}
				}
				
			}
		}
	}

	
}

class DataRef {
	public String[] classes = new String[9];
	public static HashMap<Integer, String[]> subclasses = new HashMap<Integer,String[]>();
	DataRef() {
		classes[0] = "Number of times pregnant";
		classes[1] = "Plasma glucose concentration a 2 hours in an oral glucose tolerance test";
		classes[2] = "Diastolic blood pressure (mm Hg)";
		classes[3] = "Triceps skin fold thickness (mm)";
		classes[4] = "2-Hour serum insulin (mu U/ml)";
		classes[5] = "Body mass index (weight in kg/(height in m)^2)";
		classes[6] = "Diabetes pedigree function";
		classes[7] = "Age (years)";
		classes[8] = "Tested Positive for Diabetes?"; 	
	}
}
