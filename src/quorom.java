import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class quorom 
{
	
	static ArrayList<ArrayList<ArrayList<Integer>>> f = new ArrayList<ArrayList<ArrayList<Integer>>>();
	static ArrayList<ArrayList<Integer>> l = new ArrayList<ArrayList<Integer>>();
    int val;
    quorom left;
    quorom right;
   
    quorom(int val, quorom left, quorom right) {
    this.val = val;
    this.left = left;
    this.right = right;
    }
   
 
 
 
    public static void gen_quorom(quorom root, ArrayList<ArrayList<ArrayList<Integer>>> f2)
    {
        
        if(root.left == null && root.right == null)
        {
        	ArrayList<Integer> a = new ArrayList<Integer>();
        	a.add(root.val);
            f2.get(root.val-1).add(a);
            return;
        }
       
        if(f2.get(root.right.val-1).size()==0)
        	gen_quorom(root.right, f2);
    
        ArrayList<ArrayList<Integer>> right_subtree = new ArrayList<ArrayList<Integer>>();
        
        right_subtree = f2.get(root.right.val-1);
       
        if(f2.get(root.left.val-1).size()==0)
        	gen_quorom(root.left, f2);
        ArrayList<ArrayList<Integer>> left_subtree = new ArrayList<ArrayList<Integer>>();
        left_subtree = f2.get(root.left.val-1);
        
       
        ArrayList<ArrayList<Integer>> f1 = new ArrayList<ArrayList<Integer>>();
       
   
    //  iii. any one of Right_subtree + any one of left_subtree
       
        for(int i=0; i<right_subtree.size(); i++)
        {
            for(int j=0; j<left_subtree.size(); j++)
            {
            	ArrayList<Integer> temp1 = new ArrayList<Integer>();
            	temp1.addAll(right_subtree.get(i));
            	ArrayList<Integer> temp2 = new ArrayList<Integer>();
            	temp2.addAll(left_subtree.get(j));
            	temp1.addAll(temp2);
                f1.add(temp1);
            }
        }

    //  ii. Root of the tree + a quorum of right subtree
        for(int i=0; i<right_subtree.size(); i++)
        {
        	ArrayList<Integer> temp_root = new ArrayList<Integer>();
        	temp_root.add(root.val);
            temp_root.addAll(right_subtree.get(i));
            f1.add(temp_root);
        }

    //  i. Root of the tree + a quorum of left subtree
        
        for(int i=0; i<left_subtree.size(); i++)
        {
        	ArrayList<Integer> temp_left = new ArrayList<Integer>();
        	temp_left.add(root.val);
            temp_left.addAll(left_subtree.get(i));
            f1.add(temp_left);
        }
       

        f2.set(root.val-1, f1);

        return;
    }
    
    
    
    
    public static ArrayList<Integer> getquorom()
    {
       	ArrayList<Integer> temp = new ArrayList<Integer>();
       	int min = 0;
       	int max = quorom.l.size();
       	System.out.println("size of l: "+quorom.l.size());
       	int randomNum = (int)(Math.random() * (max - min) + min);
       	temp.addAll(l.get(randomNum));
       	return temp;
        	
    	
    }
    
    
    
    
    public static void main2()
    {
    	quorom h = new quorom(1,null,null);
		h.left = new quorom(2,null,null);
		h.right = new quorom(3,null,null);
		h.left.left = new quorom(4,null,null);
		h.left.right = new quorom(5,null,null);
		h.right.left = new quorom(6,null,null);
		h.right.right = new quorom(7,null,null);
		
		for (int i =0;i<7;i++)
	    {
			ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
	    	f.add(i, temp);
	    }
		
		gen_quorom(h, f);
        
        
        for(int i=0; i<f.size(); i++)
        {
            for(int j=0; j<f.get(i).size();j++)
            {

            	ArrayList<Integer> tem = new ArrayList<Integer>();
            	tem.addAll(f.get(i).get(j));
            	l.add(tem);
            }
        }
    }
 
}
