import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class MyServer implements ActionListener
{
	ArrayList<Socket> al = new ArrayList<Socket>();
	ServerSocket ss;
	Socket s;
	JFrame f;
	JButton start, REFRESH, score;
	JLabel time;
	TextArea screen;
	JTextField message;
	JPanel interact, button;
	public MyServer()
	{
		f = new JFrame("Technophilia Sever");
		screen = new TextArea();
		f.setLayout(new BorderLayout());
		f.add(screen, BorderLayout.CENTER);
		interact = new JPanel();
		message = new JTextField();
		message.addKeyListener(new KeyAdapter() {
											public void keyReleased(KeyEvent e)
											{
												if(e.getKeyChar() == KeyEvent.VK_ENTER)
												{
													MyThread.tellEveryOne("\n\nSERVER MESSAGE\t\t"+message.getText()+"\n\n");
													String text = screen.getText();
													screen.setText(message.getText()+"\n"+text);
													message.setText("");
												}
											}
											});
		interact.add(message);
		start = new JButton("START");
		start.addActionListener(this);
		start.setEnabled(false);
		try{
			BufferedReader br1 = new BufferedReader(new FileReader("Time.txt"));	// read time from file
			String s2 = "";
			String s1 = "";
			while(s1!=null)
			{
				s1 = br1.readLine();
				if(s1!=null)
					s2 += s1;
			}
			time = new JLabel(s2,SwingConstants.CENTER);
			REFRESH = new JButton("REFRESH");
			REFRESH.addActionListener(this);
			REFRESH.setEnabled(false);
			score = new JButton("SCORE");
			score.addActionListener(this);
			score.setEnabled(false);
			button = new JPanel();
			button.add(start);
			button.add(time);
			button.add(score);
			button.add(REFRESH);
			button.setLayout(new GridLayout(1,4));
			interact.add(button);
			interact.setLayout(new GridLayout(2,1));
			f.add(interact, BorderLayout.SOUTH);
			f.setIconImage(Toolkit.getDefaultToolkit().getImage("Technophilia.jpg"));
			f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			f.pack();
			f.setVisible(true);
			ss = new ServerSocket(10);
			while(true)
			{
				s = ss.accept();
				al.add(s);
				start.setEnabled(true);
				Runnable r = new MyThread(s,al,screen,time.getText());
				Thread t = new Thread(r);
				t.start();
			}
		}
		catch(Exception e)
		{
			System.out.print("Error in constructor");
			System.exit(0);
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == REFRESH)
		{
			try
			{
				MyServer.restartApplication(new Runnable(){
															@Override
															public void run()
															{
																System.out.println("Restarting the server ...");
															}
															});
			}
			catch(Exception err)
			{
				System.out.print("Error in refreshing");
				System.exit(0);
			}
		}
		if(e.getSource() == score)
		{
			String text = screen.getText();
			screen.setText("\n\n\nCompiling the SCORE(s)\n\n\n"+text);
			MyThread.tellEveryOne("score");
			score.setEnabled(false);
			REFRESH.setEnabled(true);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		if(e.getSource() == start)
		{
			String text = screen.getText();
			MyThread.tellEveryOne("start");
			Thread thread = new Thread(new Stopwatch(time, start, score));
			thread.start();
			screen.setText("\nSTART\n"+text);
		}
	}
	public static final String SUN_JAVA_COMMAND = "sun.java.command";
	public static void restartApplication(Runnable runBeforeRestart) throws IOException
	{
		try {
			String java = System.getProperty("java.home") + "/bin/java";
			final StringBuffer cmd = new StringBuffer("\"" + java + "\" ");
			String[] mainCommand = System.getProperty(SUN_JAVA_COMMAND).split(" ");
			if (mainCommand[0].endsWith(".jar"))
			{
				cmd.append("-jar " + new File(mainCommand[0]).getPath());
			}
			else
			{
				cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
			}
			for (int i = 1; i < mainCommand.length; i++)
			{
				cmd.append(" ");
				cmd.append(mainCommand[i]);
			}
			Runtime.getRuntime().addShutdownHook(new Thread(){
																@Override
																public void run()
																{
																	try
																	{
																		FileWriter fw = new FileWriter("Present.txt");
																		fw.close();
																		Runtime.getRuntime().exec(cmd.toString());
																	}
																	catch (IOException e)
																	{
																		e.printStackTrace();
																	}
																}
															});
			if (runBeforeRestart!= null)
				runBeforeRestart.run();
			System.out.print("restarting server");
			System.exit(0);
		}
		catch (Exception e)
		{
			throw new IOException("Error while trying to restart the application", e);
		}
	}
	public static void main(String... args)
	{
		new MyServer();
	}
}
class MyThread implements Runnable
{
	Socket s;
	static ArrayList<Socket> al;
	TextArea screen;
	String t;
	MyThread(Socket s, ArrayList<Socket> al, TextArea screen, String t)
	{
		this.s = s;
		this.al = al;
		this.screen = screen;
		this.t = t;
		try
		{
			String s3,s2,s1,ques,user,msg;
			BufferedReader br1;
			DataOutputStream dout = new DataOutputStream(s.getOutputStream());
			DataInputStream din = new DataInputStream(s.getInputStream());
			user = din.readUTF();			//receive user name to verify in list
			br1 = new BufferedReader(new FileReader("Applicants.txt"));
			s2 = "";
			s1 = "";
			while(s2!=null)
			{
				s2 = br1.readLine();
				if(s2!=null)
					s1 += s2;
			}
			br1 = new BufferedReader(new FileReader("Present.txt"));
			s2 = "";
			s3 = "";
			while(s2!=null)
			{
				s2 = br1.readLine();
				if(s2!=null)
					s3 += s2;
			}
			if(s1.contains(user))
			{
				if(s3.contains(user))
				{
					msg = "exists";
				}
				else
				{
					FileWriter fw = new FileWriter("Present.txt",true);
					PrintWriter pw = new PrintWriter(fw);
					pw.println(user);
					fw.close();
					msg = "true";
				}
			}
			else
				msg = "invalid";
			dout.writeUTF(msg);			//send validity of user
			dout.flush();
			dout.writeUTF(t);						//send time to client just connected
			dout.flush();
			for(int i=0; i<30; i++)
			{
				ques = "Ques"+ Integer.toString(i) +".txt";
				br1 = new BufferedReader(new FileReader(ques));
				s2 = "";
				s1 = "";
				while(s2!=null)
				{
					s2 = br1.readLine();
					if(s2!=null)
						s1 += "\n"+s2;
				}
				dout.writeUTF(s1);						//send ques to client just connected
				dout.flush();
			}
			br1 = new BufferedReader(new FileReader("Answer.txt"));
			s2 = "";
			s1 = "";
			while(s2!=null)
			{
				s2 = br1.readLine();
				if(s2!=null)
					s1 += s2;
			}
			dout.writeUTF(s1);							// send ans keys from the file
			dout.flush();
		}
		catch(Exception ed)
		{
			System.out.print("Error in MyThread constructor");
			System.exit(0);
		}
	}
	public void run()
	{
		String s1;
		try
		{
			DataInputStream din = new DataInputStream(s.getInputStream());
			do{
				s1 = din.readUTF();
				String text = screen.getText();
				if(s1.toLowerCase().contains("marks"))	//s1 contains 'marks' then write to Result file..
				{
					FileWriter fw = new FileWriter("Result.txt",true);
					PrintWriter pw = new PrintWriter(fw);
					pw.println(s1);
					fw.close();
				}
				else
				{
					if(!s1.equals("stop"))
					{
						tellEveryOne(s1);
						screen.setText(s1+"\n"+text);
					}
				}
			}while(!s1.equals("stop"));
			al.remove(s);
		}
		catch(Exception e)
		{
			String text = screen.getText();
			screen.setText(e+"\n"+text);
		}
	}
	public static void tellEveryOne(String s1)
	{
		Iterator<Socket> i = MyThread.al.iterator();
		while(i.hasNext())
		{
			try
			{
				Socket sc = i.next();
				DataOutputStream dout = new DataOutputStream(sc.getOutputStream());
				dout.writeUTF(s1);
				dout.flush();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}
}
class Stopwatch implements Runnable
{
	JLabel time;
	JButton start, score;
	Stopwatch(JLabel time, JButton start, JButton score)
	{
		this.time = time;
		this.start = start;
		this.score = score;
	}
	public void run()
	{
		try
		{
			String t = time.getText();
			String[] ti = t.split(":");
			int hour = Integer.parseInt(ti[0]);
			int min = Integer.parseInt(ti[1]);
			int sec = Integer.parseInt(ti[2]);
			do
			{
				do
				{
					do
					{
						String h = String.valueOf(hour);
						if(h.length()==1)
							h = "0"+h;
						String m = String.valueOf(min);
						if(m.length()==1)
							m = "0"+m;
						String s = String.valueOf(sec);
						if(s.length()==1)
							s = "0"+s;
						time.setText(h+":"+m+":"+s);
						if(false == ((h.equals("00"))&&(m.equals("00"))&&(s.equals("00"))))
							Thread.sleep(1000);
						sec--;
					}while(sec>=0);
					min--;
					sec = 59;
				}while(min>=0);
				hour--;
				min = 59;
			}while(hour>=0);
		}
		catch(Exception ee)
		{}
		start.setEnabled(false);
		score.setEnabled(true);
	}
}