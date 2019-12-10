

import java.io.BufferedReader;
import java.util.Collections;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;



public class Proj2 
{
	
	public static int totalnodes;
	public static int nodeid;
	public static int receivedgrants=0;
	public static int messages_sent=0;
	public static int messages_received=0;
	public static int numservers;
	public static int numclients;
	public static boolean locked = false;
	public static int lockedby = -1;

	static Comparator<Message> comparator = new MessageComparator();
	static PriorityBlockingQueue<Message> pq = new PriorityBlockingQueue(11, comparator);
	
	public static ArrayList<Integer> quorom1;
	
	public static HashMap<String,Socket> socketMap = new HashMap<String,Socket>();
    public static HashMap<Socket,BufferedReader> readers = new HashMap<Socket,BufferedReader>();
    public static HashMap<Socket,PrintWriter> writers = new HashMap<Socket,PrintWriter>();
	public static int received_complete = 0;
	
	public static void main(String[] args) 
	{
		// List of String arrays
		if (args.length>0) 
		{
			try 
			{
				nodeid = Integer.parseInt(args[0]);
				
			}
			catch (NumberFormatException e)
			{
				System.err.println("Argument must be an integer");
				System.exit(1);
		    }
			try 
			{
				
				IOHandler IOH = new IOHandler(args[1]);
				totalnodes = IOH.readconfig();
				System.out.println("totalnodes: "+totalnodes);
				System.out.println("clients: "+numclients);
				System.out.println("servers: "+numservers);
				
				Mutex RA = new Mutex(IOH);
				
				Receiver RCT = new Receiver(nodeid, totalnodes, IOH);
				
				Thread.sleep(15000);
				
				SendConnectionThread SCT = new SendConnectionThread(nodeid, totalnodes, IOH);
				
				// Sleep so that socket connections can be made
				Thread.sleep(5000);
				
				System.out.println(socketMap);
				// Starting threads for always read listeners
				for (int i=1;i<=totalnodes;i++)
				{
					if (i!=nodeid)
					{
						System.out.println("Socket for application call: "+socketMap.get(Integer.toString(i)));
						
						Application DT = new Application(socketMap.get(Integer.toString(i)),IOH, RA);
						System.out.println("SocketID: "+DT);
						System.out.println("Started thread at "+nodeid+" for listening "+i);
					}
				}
				
				
				
				if (nodeid>numservers) {
					quorom.main2();
					new Thread()
					{
						public void run()
						{
//							Send first message
							quorom1 = new ArrayList<Integer>();
							quorom1= quorom.getquorom();
							
							System.out.println("Selected quorom: "+quorom1);
							try {
								Thread.sleep((int)(Math.random() * (10 - 5) + 5)*1000);
								} catch (InterruptedException e1) {
								
								e1.printStackTrace();
								}
							
							System.out.println("Sending request messages to all servers in the quorom");
							Long currentTS = System.currentTimeMillis();
							for(int i=0; i<Proj2.quorom1.size();i++)
							{
								
								Socket bs = Proj2.socketMap.get(Integer.toString(Proj2.quorom1.get(i)));
								System.out.println("Sending request message to : "+bs);
								PrintWriter writer = Proj2.writers.get(bs);
								writer.println("request"+","+Proj2.nodeid+ ","+currentTS);
					            writer.flush();
					            ++messages_sent;
					            ++RA.cs_msgsent;
							}
						}
					 }.start();

					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}

	public static void closeSockets()
	{
		System.out.println("Halting this node");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=1; i<=totalnodes; i++)
		{
			if (i!=nodeid)
			{
				try
				{	
					System.out.println("Closing socket with "+Integer.toString(i));
					Socket socket = socketMap.get(Integer.toString(i));
					if (socket != null)
					{
						PrintWriter writer = writers.get(socket);
						BufferedReader BR = readers.get(socket);
						writer.close();
						BR.close();
						socket.close();
						
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		
		System.out.println("**************************************************************Going to exit!!!!************************************");
		System.exit(0);
	}

	
}
