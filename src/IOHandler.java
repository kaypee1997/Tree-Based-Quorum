

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class IOHandler {
	
	String filename;
	public int totalnodes=0;
	public Map<String, List<String>> map = new HashMap<String, List<String>>();
	
	public IOHandler(String f)
	{
		this.filename = f;
	}
	
	public int readconfig()
	{
		try 
		{
			File file = new File(filename);
			Scanner scan = new Scanner(file);
			
			int i = 0;
			while(scan.hasNext())
			{
				String line = scan.nextLine().trim();
				String[] tokens = line.split(" ");
				
				if(i == 0)
				{
					totalnodes = Integer.parseInt(tokens[0]);
					
					i+=1;
				}
				else if(i == 1)
				{
					Proj2.numclients = Integer.parseInt(tokens[0]);++i;
				}
				else if(i == 2)
				{
					Proj2.numservers = Integer.parseInt(tokens[0]);
					++i;
				}
				else 
				{
					
					List<String> valueList = new ArrayList<String>();
					valueList.add(tokens[1]);
					valueList.add(tokens[2]);
					System.out.println(valueList);
					map.put(tokens[0], valueList);
				}
			}
			
//			for (Map.Entry<String, List<String>> entry : map.entrySet())
//			{
//				String key = entry.getKey();	
//				List<String> values = entry.getValue();							
//				System.out.println("Key = " + key);
//				System.out.println("Values = " + values);
//				
//			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return totalnodes;
	}
}
