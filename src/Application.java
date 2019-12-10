import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;



public class Application extends Thread{

	long messageTS, currentTS;
	Socket socket;
	BufferedReader BR;
	Mutex RA;
	IOHandler IOH;
	
	Application(Socket socket, IOHandler IOH, Mutex RA)
	{
		super();
		start();
		this.socket = socket;
		this.RA = RA;
		this.IOH = IOH;
		try 
		{
			BR = Proj2.readers.get(socket);
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run()
	{
		while(true) {
		String msg;
		try
		{
			while((msg = BR.readLine()) != null)
			{
				++Proj2.messages_received;
				
				String tokens[] = msg.split(",");
				String messageType = tokens[0];
				int source = Integer.parseInt(tokens[1]);
				messageTS = Long.parseLong(tokens[2]);
				currentTS = System.currentTimeMillis();
				
				
				if(messageType.equals("complete"))
				{
					System.out.println("Received complete from "+ source);
					++Proj2.received_complete;
					
					if (Proj2.nodeid == 1)
					{
//						Send message to other servers to log their message counts
						if(Proj2.received_complete == Proj2.numclients)
						{
							for(int i=1; i<=Proj2.numservers; i++)
							{
								if (i!= Proj2.nodeid)
								{
									PrintWriter writer = Proj2.writers.get(Proj2.socketMap.get(Integer.toString(i)));
									writer.println("log"+","+Proj2.nodeid+","+currentTS);
						            writer.flush();
								}
							}
							
							Thread.sleep(100);
							
							
							try(FileWriter fw = new FileWriter("Messages.txt", true);
								    BufferedWriter bw = new BufferedWriter(fw);
								    PrintWriter out = new PrintWriter(bw))
								{
								    out.println("Total messages received by "+ Proj2.nodeid+" : "+Proj2.messages_received);
								    out.println("Total messages sent by : "+ Proj2.nodeid+" : "+ Proj2.messages_sent);
								    out.println("\n");
								} catch (IOException e) {
								    
//									e.printStackTrace();
								}
							
//							send complete to other nodes so that they can close their connection
							Proj2.closeSockets();
							System.out.println("Still here after closing sockets!####################");
						}
					}
					break;
				}
				
				if (messageType.equals("log"))
				{
					try(FileWriter fw = new FileWriter("Messages.txt", true);
						    BufferedWriter bw = new BufferedWriter(fw);
						    PrintWriter out = new PrintWriter(bw))
						{
						    out.println("Total messages received by "+ Proj2.nodeid+" : "+Proj2.messages_received);
						    out.println("Total messages sent by : "+ Proj2.nodeid+" : "+ Proj2.messages_sent);
						    out.println("\n");
						} catch (IOException e) {
						    
//							e.printStackTrace();
						}
					Thread.sleep(300);
					System.exit(0);
				}
				
				
				if(messageType.equals("request"))
				{
					++RA.cs_msgrec;
					System.out.println("Got request from "+ source);
					
//					Add message into queue
					Proj2.pq.add(new Message(messageType, source, messageTS));

					
					Message m = Proj2.pq.peek();
					if (Proj2.locked == false)
					{
	//					Add current request to queue and send grant
						
						Proj2.locked = true;
						Proj2.lockedby = m.source;
						
//						send grant to node at head
						PrintWriter writer = Proj2.writers.get(Proj2.socketMap.get(Integer.toString(m.source)));
						writer.println("grant"+","+Proj2.nodeid+","+currentTS);
			            writer.flush();
			            System.out.println("Sent grant to "+ source);
			            
			            ++Proj2.messages_sent;
			            ++RA.cs_msgsent;
					}
					else
					{
						System.out.println("Queued request of "+ source);
						
					}
						
					break;
				}
				
				if(messageType.equals("grant"))
				{
					System.out.println("Received grant from "+ source);
					
					++RA.cs_msgrec;
					++Proj2.receivedgrants;
					
					if (Proj2.receivedgrants == Proj2.quorom1.size())
					{
						RA.enterCS(messageTS); 
//						CS done! send release message to all servers in the quorom
						for(int i=0; i<Proj2.quorom1.size();i++)
						{
							Socket bs = Proj2.socketMap.get(Integer.toString(Proj2.quorom1.get(i)));
							System.out.println("Sending release message to : "+Proj2.quorom1.get(i));
							PrintWriter writer = Proj2.writers.get(bs);
							writer.println("release"+","+Proj2.nodeid+ ","+currentTS);
				            writer.flush();
				            
				            ++Proj2.messages_sent;
				            ++RA.cs_msgsent;
				            
						}
						String s = Integer.toString(Proj2.nodeid)+"Log.txt";
			    		try(FileWriter fw1 = new FileWriter(s, true);
			    				BufferedWriter bw = new BufferedWriter(fw1);
			    			    PrintWriter out = new PrintWriter(bw))
			    			{
//			    				Enter logs of response time and messages for each CS exec
//			    			out.println("CS #"+RA.cscount+" : "+  RA.cscount);
//			    			out.println("Elapsed_time: "+(currentTS-messageTS));
			    			out.println("Msgs sent: "+RA.cs_msgsent);
			    			out.println("Msgs received: "+RA.cs_msgrec);
			    			out.println("*******----******");
			    			}
			    		catch(IOException e)
			    		{
			    			e.printStackTrace();
			    		}
			    		RA.cs_msgsent = 0;
			    		RA.cs_msgrec = 0;
						
						if (RA.cscount <20)
						{
							RA.requestCS();
						}
//						Send complete to node 0
						else
						{
							try(FileWriter fw = new FileWriter("Messages.txt", true);
								    BufferedWriter bw = new BufferedWriter(fw);
								    PrintWriter out = new PrintWriter(bw))
								{
								    out.println("Total messages received by "+ Proj2.nodeid+" : "+Proj2.messages_received);
								    out.println("Total messages sent by : "+ Proj2.nodeid+" : "+ Proj2.messages_sent);
								    out.println("\n");
								   
								} catch (IOException e) {
								    
									e.printStackTrace();
								}
							System.out.println("All computaions done!!");
							Socket bs = Proj2.socketMap.get(Integer.toString(1));
							PrintWriter writer = Proj2.writers.get(bs);
							writer.println("complete"+","+Proj2.nodeid+ ","+currentTS);
				            writer.flush();
				            System.exit(0);
						}
						
					}
						
						
					break;
				}
				
				if(messageType.equals("release"))
				{
					++RA.cs_msgrec;
					System.out.println("Received release from "+ source);
					
//					++Proj2.messages_received;
					if (Proj2.lockedby == source && Proj2.locked == true)
					{
	                    
                    	Proj2.pq.poll();
                        if (!Proj2.pq.isEmpty()) 
                        {
                        	Proj2.locked = false;
                            Message request = Proj2.pq.peek();
                            Proj2.locked = true;
                            Proj2.lockedby = request.source;
//	                            Send grant to peek node in queue
                            PrintWriter writer = Proj2.writers.get(Proj2.socketMap.get(Integer.toString(request.source)));
                            System.out.println("Sent grant to peek of req queue -> "+request.source+ "socket: "+Proj2.socketMap.get(Integer.toString(request.source)));
    						writer.println("grant"+","+Proj2.nodeid+","+currentTS);
    			            writer.flush();
    			            ++Proj2.messages_sent;
    			            ++RA.cs_msgsent;
                            
                        } 
                        else 
                        {
                        	Proj2.locked = false;
                        	Proj2.lockedby = -1;
//                        	System.out.println("Else part: Set LOCKED to false ");
                        }
					
					}
                    break;
				}
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		
	}
	}
}
	
