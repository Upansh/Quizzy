import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
class Competition implements ActionListener
{
	static int marks=0, wrn=0;
	static String answer;
	static char[] marked;
	int q=0;
	boolean a=false, flip=false;
	JTextField tf;
	JFrame Log, f, wlcm;
	JPanel jp, quesPanel, namePanel, quesBasePanel, optPanel, optPanel1, sidePanel, sideBasePanel, numPanel;
	JButton b, quesButton[], opt[], opt1[];
	JLabel jl, jl1, name, time;
	TextArea chatField, quesField[], welcome;
	CardLayout cl;
	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	static String name1;
	String[] question;
	Competition()
	{
		Log = new JFrame();
		jp = new JPanel();
		jl = new JLabel("Provide Your ID : ");
		jl1 = new JLabel("ID should be the one by which you are registered");
		jp.add(jl);
		tf = new JTextField();
		tf.addKeyListener(new KeyAdapter() {
											public void keyReleased(KeyEvent e)
											{
												if(e.getKeyChar() == KeyEvent.VK_ENTER)
												{
													validate(tf.getText());
												}
											}
											});
		jp.add(tf);
		b = new JButton("Join");
		b.addMouseListener(new MouseAdapter(){
												public void mouseClicked(MouseEvent e)
												{
													validate(tf.getText());
												}
											});
		jp.add(b);
		jp.add(jl1);
		jp.setLayout(new GridLayout(4,1));
		Log.add(jp);
		Log.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Log.setResizable(false);
		Log.setIconImage(Toolkit.getDefaultToolkit().getImage("Technophilia.jpg"));
		Log.setMinimumSize(new Dimension(300,150));
		Log.pack();
		
		f=new JFrame("Best of Luck");
		sidePanel = new JPanel();
		sideBasePanel = new JPanel();
		numPanel = new JPanel();
		quesBasePanel = new JPanel();
		quesPanel = new JPanel();
		namePanel = new JPanel();
		name = new JLabel("Candidate",SwingConstants.CENTER);
		time = new JLabel("00:00:00",SwingConstants.CENTER);
		name.setFont(new Font("Serif", Font.BOLD, 25));
		name.setForeground(new Color(154,26,168));
		time.setFont(new Font("Serif", Font.BOLD, 35));
		time.setForeground(new Color(244,113,26));
		namePanel.add(name);
		namePanel.add(time);
		namePanel.setLayout(new GridLayout(2,1));
		
		f.setLayout(new BorderLayout());
		
		sideBasePanel.setLayout(new BorderLayout());
		sideBasePanel.add(namePanel,BorderLayout.NORTH);
		quesButton = new JButton[30];
		marked = new char[30];
		for(int i=0;i<quesButton.length;i++)
		{
			quesButton[i] = new JButton(Integer.toString(i+1));
			quesButton[i].addActionListener(this);
			numPanel.add(quesButton[i]);
			marked[i] = '0';
		}
		numPanel.setLayout(new GridLayout(6,5));
		sidePanel.add(numPanel);
		chatField = new TextArea();
		chatField.setEditable(false);
		chatField.setBackground(Color.WHITE);
		sidePanel.add(chatField);
		sidePanel.setLayout(new GridLayout(2,1));
		sideBasePanel.add(sidePanel,BorderLayout.CENTER);
		
		quesBasePanel.setLayout(new BorderLayout());
		cl=new CardLayout();
		quesPanel.setLayout(cl);	
		quesField = new TextArea[30];
		for(int i=0;i<quesField.length;i++)
		{
			quesField[i] = new TextArea();
			quesField[i].setEditable(false);
			quesField[i].setBackground(Color.WHITE);
			String no = "t"+Integer.toString(i);
			quesPanel.add(quesField[i], no);
		}
		quesBasePanel.add(quesPanel,BorderLayout.CENTER);
		
		optPanel1 = new JPanel();
		optPanel = new JPanel();
		opt = new JButton[4];
		String s1[]={"A","B","C","D"};
		for(int i=0;i<opt.length;i++)
		{
			opt[i]=new JButton(s1[i]);
			opt[i].addActionListener(this);
			optPanel.add(opt[i]);
		}
		optPanel.setLayout(new GridLayout(2,2,10,10));
		opt1 = new JButton[2];
		opt1[0]=new JButton(" Previous ");
		opt1[0].addActionListener(this);
		opt1[1]=new JButton("   Next   ");
		opt1[1].addActionListener(this);
		//opt1[2]=new JButton("Submit your Answers and Sum-Up the Test");
		//opt1[2].addActionListener(this);
		optPanel1.setLayout(new BorderLayout());
		optPanel1.add(opt1[0],BorderLayout.WEST);
		optPanel1.add(optPanel,BorderLayout.CENTER);
		optPanel1.add(opt1[1],BorderLayout.EAST);
		quesBasePanel.add(optPanel1,BorderLayout.SOUTH);
		
		//sideBasePanel.add(opt1[2],BorderLayout.SOUTH);
		
		f.add(sideBasePanel,BorderLayout.WEST);
		f.add(quesBasePanel,BorderLayout.CENTER);
		f.setIconImage(Toolkit.getDefaultToolkit().getImage("Technophilia.jpg"));
		f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		f.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation(dim.width/2-f.getSize().width/2, dim.height/2-f.getSize().height/2);
		
		f.addWindowListener(new WindowListener(){	@Override
													public void windowDeactivated(WindowEvent e)
													{
														/*try
														{
															Thread tt = new Thread(new Warning(true, ++wrn));
															tt.start();
														}
														catch(Exception ee)
														{
															System.out.println("\n\nerror in deactivation-thread calling" + ee + "\n\n");
														}*/
													}
													@Override
													public void windowClosing(WindowEvent e) {}
													@Override
													public void windowOpened(WindowEvent e) {}
													@Override
													public void windowClosed(WindowEvent e) {}
													@Override
													public void windowIconified(WindowEvent e) {}
													@Override
													public void windowDeiconified(WindowEvent e) {}
													@Override
													public void windowActivated(WindowEvent e)
													{
														/*try
														{
															Thread tt = new Thread(new Warning(false, wrn));
															tt.start();
														}
														catch(Exception ee)
														{
															System.out.println("\n\nerror in activation-thread calling" + ee + "\n\n");
														}*/
													}
												});
		for(int i=0;i<quesButton.length;i++)								// buttons block in initialization
			quesButton[i].setEnabled(false);
		for(int i=0;i<opt.length;i++)
			opt[i].setEnabled(false);
		for(int i=0;i<opt1.length;i++)
			opt1[i].setEnabled(false);
		String r = "Hello there...Please Understand the rules clearly before the test begins..\n\n1) The 'Time Counter' displays the remaining time for the test.\n2) The 'RED' coloured number highlights the current question.\n3) The 'BLUE' coloured numbers highlight the questions visited, left unanswered.\n4) The 'GREEN' coloured numbers highlights the answered question.\n5) The answer submitted for each question is highlighted in 'GREEN'\n\tTo submit an answer(or change already submitted), just choose the appropriate option.\n6) If you 'Submit your Answers and Sum-Up the Test', you would not be able to answer questions further\n\tAlthough you would have to wait till the Test ends.\n7) The Marks would be displayed once the test has been completed\n8) The 'Chat Field' will keep you posted with all Server Messages\n\n\t\tHave A Happy Time..!";
		welcome = new TextArea(r);
		welcome.setEditable(false);
		welcome.setBackground(Color.WHITE);
		wlcm = new JFrame("Rules for Test");
		wlcm.add(welcome,BorderLayout.CENTER);
		wlcm.setSize(650,350);
		wlcm.setLocation(dim.width/2-wlcm.getSize().width/2, dim.height/2-wlcm.getSize().height/2);
		wlcm.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		Log.setVisible(true);
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			System.out.print("Error in Competition constructor");
			System.exit(0);
		}
	}
	public void actionPerformed(ActionEvent e)
	{
		if( quesButton[q].getBackground()!= Color.GREEN )
			quesButton[q].setBackground(new Color(28,158,255));
		if(marked[q]!='0')
			quesButton[q].setBackground(Color.GREEN);
		a=false;
		flip=false;
		if(e.getSource()==quesButton[0])
			showQues(0);
		if(e.getSource()==quesButton[1])
			showQues(1);
		if(e.getSource()==quesButton[2])
			showQues(2);
		if(e.getSource()==quesButton[3])
			showQues(3);
		if(e.getSource()==quesButton[4])
			showQues(4);
		if(e.getSource()==quesButton[5])
			showQues(5);
		if(e.getSource()==quesButton[6])
			showQues(6);
		if(e.getSource()==quesButton[7])
			showQues(7);
		if(e.getSource()==quesButton[8])
			showQues(8);
		if(e.getSource()==quesButton[9])
			showQues(9);
		if(e.getSource()==quesButton[10])
			showQues(10);
		if(e.getSource()==quesButton[11])
			showQues(11);
		if(e.getSource()==quesButton[12])
			showQues(12);
		if(e.getSource()==quesButton[13])
			showQues(13);
		if(e.getSource()==quesButton[14])
			showQues(14);
		if(e.getSource()==quesButton[15])
			showQues(15);
		if(e.getSource()==quesButton[16])
			showQues(16);
		if(e.getSource()==quesButton[17])
			showQues(17);
		if(e.getSource()==quesButton[18])
			showQues(18);
		if(e.getSource()==quesButton[19])
			showQues(19);
		if(e.getSource()==quesButton[20])
			showQues(20);
		if(e.getSource()==quesButton[21])
			showQues(21);
		if(e.getSource()==quesButton[22])
			showQues(22);
		if(e.getSource()==quesButton[23])
			showQues(23);
		if(e.getSource()==quesButton[24])
			showQues(24);
		if(e.getSource()==quesButton[25])
			showQues(25);
		if(e.getSource()==quesButton[26])
			showQues(26);
		if(e.getSource()==quesButton[27])
			showQues(27);
		if(e.getSource()==quesButton[28])
			showQues(28);
		if(e.getSource()==quesButton[29])
			showQues(29);
		if(e.getSource()==opt1[0])
			showQues( (q == 0)?29:--q );
		if(e.getSource()==opt1[1])
			showQues( (q == 29)?0:++q );
		//if(e.getSource()==opt1[2])
			// pop up new frame confirming to end the test..
		if(e.getSource()==opt[0])
		{
			marked[q] = 'A';
			opt[0].setBackground(new Color(16,216,41));
			opt[1].setBackground(new JButton().getBackground());
			opt[2].setBackground(new JButton().getBackground());
			opt[3].setBackground(new JButton().getBackground());
			a=true;
			quesButton[q].setBackground(Color.GREEN);
		}
		if(e.getSource()==opt[1])
		{
			marked[q] = 'B';
			opt[0].setBackground(new JButton().getBackground());
			opt[1].setBackground(new Color(16,216,41));
			opt[2].setBackground(new JButton().getBackground());
			opt[3].setBackground(new JButton().getBackground());
			a=true;
			quesButton[q].setBackground(Color.GREEN);
		}
		if(e.getSource()==opt[2])
		{
			marked[q] = 'C';
			opt[0].setBackground(new JButton().getBackground());
			opt[1].setBackground(new JButton().getBackground());
			opt[2].setBackground(new Color(16,216,41));
			opt[3].setBackground(new JButton().getBackground());
			a=true;
			quesButton[q].setBackground(Color.GREEN);
		}
		if(e.getSource()==opt[3])
		{
			marked[q] = 'D';
			opt[0].setBackground(new JButton().getBackground());
			opt[1].setBackground(new JButton().getBackground());
			opt[2].setBackground(new JButton().getBackground());
			opt[3].setBackground(new Color(16,216,41));
			a=true;
			quesButton[q].setBackground(Color.GREEN);
		}
		try		//send msg to server
		{
			String print="";
			if(a==true)
				print = name1 + " answered a question";
			if(flip==true)
				print = name1 + " viewed another question";
			dout.writeUTF(print);
			dout.flush();
		}
		catch(Exception e1)
		{}
	}
	void showQues(int q)
	{
		this.q = q;
		cl.show(quesPanel, "t"+String.valueOf(q));
		quesButton[q].setBackground(Color.RED);
		opt[0].setBackground(new JButton().getBackground());
		opt[1].setBackground(new JButton().getBackground());
		opt[2].setBackground(new JButton().getBackground());
		opt[3].setBackground(new JButton().getBackground());
		if(marked[q] == 'A')
			opt[0].setBackground(new Color(16,216,41));
		if(marked[q] == 'B')
			opt[1].setBackground(new Color(16,216,41));
		if(marked[q] == 'C')
			opt[2].setBackground(new Color(16,216,41));
		if(marked[q] == 'D')
			opt[3].setBackground(new Color(16,216,41));
		flip=true;			
	}
	void validate(String text)
	{
		try				 //read from IP-file, verify and get exam details over network
		{
			BufferedReader br1 = new BufferedReader(new FileReader("IP.txt"));	// read IP from file
			String s2 = "";
			String s3 = "";
			while(s3!=null)
			{
				s3 = br1.readLine();
				if(s3!=null)
					s2 += s3;
			}
			s = new Socket(s2, 10);
			din = new DataInputStream(s.getInputStream());
			dout = new DataOutputStream(s.getOutputStream());
			dout.writeUTF(text);											//send the user name for verification
			dout.flush();
			String abc = din.readUTF();										//read verification from server
			if(abc.equals("true"))
			{
				name1 = text;
				name.setText(name1);
				wlcm.setVisible(true);
				Log.setVisible(false);
			}
			else
			{
				if(abc.equals("invalid"))
					jl.setText("Your ID is not registered");
				else
					if(abc.equals("exists"))
						jl.setText("You are already logged in");
			}
			time.setText(din.readUTF());									//read time from server
			question = new String[30];
			for(int i=0;i<quesField.length;i++)
				question[i] = new String(din.readUTF());					//read ques from server in string mode
			answer = din.readUTF();											//read the ans keys from server in char mode
			My m = new My(din, dout, f, wlcm, welcome, chatField, quesField, quesButton, opt, opt1, time, question);
			Thread t1 = new Thread(m);
			t1.start();
		}
		catch(Exception e)
		{
			jl.setText("Problem in verifying the ID");
		}
	}
	public static void main(String s[])
	{
		new Competition();
	}

}
class Warning implements Runnable
{
	static JDialog warning = new JDialog();
	static JLabel warn = new JLabel("", JLabel.CENTER);
	int wrn;
	Warning(boolean x, int wrn)
	{
		this.wrn = wrn;
		warning.setTitle("Warning...");
		warning.add(warn);
		warning.setModal (true);
		warning.setAlwaysOnTop(true);
		warning.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		warning.setResizable(false);
		warning.setIconImage(Toolkit.getDefaultToolkit().getImage("Technophilia.jpg"));
		warning.setMinimumSize(new Dimension(300,200));
		if(wrn<4)
			warn.setText("<HTML>You are being warned...<br><br>You are trying to switch to a window outside the scope of the Test Screen.<br>Such further attempts can lead to your disqualification!</HTML>");
		if(wrn==4)
			warn.setText("<HTML>This is your Final Waring...<br><br>You are trying to switch to a window outside the scope of the Test Screen.<br>Any further attempt will lead to your disqualification from this test!</HTML>");
		if(wrn>=5)
		{
			warn.setText("<HTML>You are Disqualified..<br><br>You have gone outside the scope more than 4 times, hence you are disqualified from this test.<br><br>Contact your invigilator for further details..</HTML>");
			warning.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		}
		warning.pack();
		warning.setLocation(dim.width/2-warning.getSize().width/2, dim.height/2-warning.getSize().height/2);
		warning.setVisible(x);
	}
	public void run()
	{
		System.out.println("Warning thread executed (" + wrn + ") times");
	}
}
class My implements Runnable
{
	DataInputStream din;
	DataOutputStream dout;
	static TextArea chatField;
	TextArea quesField[], welcome;
	JButton quesButton[];
	static JButton opt[], opt1[];
	JLabel time;
	JFrame f, wlcm;
	String[] question;
	My(DataInputStream din, DataOutputStream dout, JFrame f, JFrame wlcm, TextArea welcome, TextArea chatField, TextArea quesField[], JButton quesButton[], JButton opt[], JButton opt1[], JLabel time, String[] question)
	{
		this.din = din;
		this.dout = dout;
		this.f = f;
		this.wlcm = wlcm;
		this.welcome = welcome;
		this.chatField = chatField;
		this.quesField = quesField;
		this.quesButton = quesButton;
		this.opt = opt;
		this.opt1 = opt1;
		this.time = time;
		this.question = question;
	}
	public void run() //prints message to chat window from server
	{
		boolean count = true;
		String s2 = "";
		do{
			try
			{
				String ch = chatField.getText();
				s2 = din.readUTF();
				if(s2.equals("start") && count==true )
				{
					wlcm.setVisible(false);
					f.setVisible(true);
					chatField.setText("\n\nSERVER MESSAGE\t\tLet's Begin..\n\n"+ch);
					for(int i=0;i<quesButton.length;i++)
						quesButton[i].setEnabled(true);
					for(int i=0;i<opt.length;i++)
						opt[i].setEnabled(true);
					for(int i=0;i<opt1.length;i++)
						opt1[i].setEnabled(true);
					for(int i=0;i<quesField.length;i++)
						quesField[i].setText(question[i]);				// set ques
					quesButton[0].setBackground(Color.RED);	
					Thread thread = new Thread(new Stopwatch1(dout, f, wlcm, welcome, chatField, quesButton, opt, opt1, time));
					thread.start();
					count = false;
				}	
				else
				{
					if(s2.equals("score"))
					{
						wlcm.setVisible(true);
						f.setVisible(false);
						for(int z=0; z<Competition.answer.length(); z++)
						{
							if(Competition.answer.charAt(z) == Competition.marked[z])
								Competition.marks++;
						}
						welcome.setText("\n\n\n\n\n Your score is :\n\n\n\t\t" + Competition.marks + "\n\n\n Thank You for taking the test..");
						wlcm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						dout.writeUTF(Competition.name1+" secured "+Competition.marks+" marks..");
						dout.flush();
						dout.writeUTF("stop");
						dout.flush();
					}
					else
					{
						if(!s2.equals("start"))
							chatField.setText(s2+"\n"+ch);
					}
				}
			}
			catch(Exception e)
			{
				System.out.print("Error in My run()");
				System.exit(0);
			}
		}while(!s2.equals("score"));
	}
}
class Stopwatch1 implements Runnable
{
	DataOutputStream dout;
	TextArea chatField, welcome;
	JButton quesButton[], opt[], opt1[];
	JLabel time;
	JFrame f, wlcm;
	Stopwatch1(DataOutputStream dout,  JFrame f, JFrame wlcm, TextArea welcome, TextArea chatField, JButton quesButton[], JButton opt[], JButton opt1[], JLabel time)
	{
		this.time = time;
		this.dout = dout;
		this.f = f;
		this.wlcm = wlcm;
		this.welcome = welcome;
		this.chatField = chatField;
		this.quesButton = quesButton;
		this.opt = opt;
		this.opt1 = opt1;
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
						if((hour==0)&&(min==0)&&(sec<=30))
						{
							time.setForeground(Color.RED);
							Thread.sleep(500);
						}
						time.setText(h+":"+m+":"+s);
						if(false == ((h.equals("00"))&&(m.equals("00"))&&(s.equals("00"))))
						{
							if((hour==0)&&(min==0)&&(sec<=30))
							{
								time.setVisible(false);
								Thread.sleep(500);
								time.setVisible(true);
							}
							else
								Thread.sleep(1000);
						}
						sec--;
					}while(sec>=0);
					min--;
					sec = 59;
				}while(min>=0);
				hour--;
				min = 59;
			}while(hour>=0);
			for(int i=0;i<quesButton.length;i++)								// buttons blocked as time ends
				quesButton[i].setEnabled(false);
			for(int i=0;i<opt.length;i++)
				opt[i].setEnabled(false);
			for(int i=0;i<opt1.length;i++)
				opt1[i].setEnabled(false);
			chatField.setText("\n\n The test has finished successfully..\n\n Summing up the Test..!");
			dout.writeUTF(Competition.name1+" finished the test successfully..");
			dout.flush();
			Thread.sleep(3000);
			f.setVisible(false);
			Warning.warning.setVisible(false);
			wlcm.setTitle("And the test Ends..");
			welcome.setText("\n\n\n\n\n The test has finished successfully...\n\n Result will be displayed soon..!");
			wlcm.setVisible(true);
		}
		catch(Exception ee)
		{
			System.out.println("Error in Stopwatch"+ee);
			System.exit(0);
		}
	}
}