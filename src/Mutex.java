import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Mutex {
	IOHandler IOH;
	public int cscount = 0;
	public int cs_msgsent = 0;
	public int cs_msgrec = 0;
	public int cshalt = 700;
	
	
	Mutex(IOHandler IOH)
	{
		this.IOH = IOH;
	}

//	When a node enters a CS
	public void enterCS(Long messageTS)
	{
		++cscount;
		
		long currentTS = System.currentTimeMillis();
		String s = Integer.toString(Proj2.nodeid)+"Log.txt";
		try(FileWriter fw1 = new FileWriter(s, true);
				BufferedWriter bw = new BufferedWriter(fw1);
			    PrintWriter out = new PrintWriter(bw))
			{
//				Enter logs of response time and messages for each CS exec
			out.println("CS #"+cscount+" : "+  cscount);
			out.println("Elapsed_time: "+(currentTS-messageTS));

			}
		catch(IOException e)
		{
			e.printStackTrace();
		}
			
		Proj2.receivedgrants = 0;
		//enter CS and edit the file
		try(FileWriter fw = new FileWriter("CSENTRY.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println("entering "+ Proj2.nodeid+" "+cscount);
			    
			    out.println(System.currentTimeMillis()+"\n");
			    
			    try {
			    	
//			    	System.out.println("Done with writing into file");
					Thread.sleep(cshalt);
					System.out.println("delay of "+cshalt + " "+(currentTS%10000));
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
			    
			   
			} catch (IOException e) {
			    
				e.printStackTrace();
			}
//		Send Release message to every sever in the quorom
		
	}

	public void requestCS() {
		// TODO Auto-generated method stub
		
		System.out.println("Request CS "+ cscount);
		
		Proj2.quorom1= quorom.getquorom();
		
		System.out.println("Selected quorom: "+Proj2.quorom1);
		try {
			Thread.sleep((int)(Math.random() * (10 - 5) + 5)*100);
			} catch (InterruptedException e1) {
			
			e1.printStackTrace();
			}
		
		System.out.println("Sending request messages to all servers in the quorom");
		long currentTS = System.currentTimeMillis();
		for(int i=0; i<Proj2.quorom1.size();i++)
		{
			
			Socket bs = Proj2.socketMap.get(Integer.toString(Proj2.quorom1.get(i)));
			System.out.println("Sending request message to : "+Proj2.quorom1.get(i));
			PrintWriter writer = Proj2.writers.get(bs);
			writer.println("request"+","+Proj2.nodeid+ ","+currentTS);
            writer.flush();
            ++cs_msgsent;
            ++Proj2.messages_sent;
            
		}
		
	}
}
