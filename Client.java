package server_client;

import java.io.*;
import java.lang.*;
import java.net.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame
{
	private JTextField usertext;
	private JTextArea chatwindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private String serverIP;
	
	public Client(String host)
	{
		super("CLIENT");
		serverIP = host;
		usertext = new JTextField();
		usertext.setEditable(false);
		usertext.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						sendmessage(event.getActionCommand());
						usertext.setText("");
					}
				});
		add(usertext,BorderLayout.SOUTH);
		chatwindow = new JTextArea();
		chatwindow.setEditable(false);
		add(new JScrollPane(chatwindow),BorderLayout.CENTER);
		setSize(1280,720);
		setVisible(true);
	}
	public void startRunning() throws IOException  
	{
		try
		{
			connection();
			setup();
			chatting();
		}
		catch(EOFException e)
		{
			showmessage("\n Client ended the connection!");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeall();
		}
	}
	private void connection() throws IOException
	{
		showmessage("COnnecting..\n");
		connection = new Socket(InetAddress.getByName(serverIP),3000);
		showmessage("Connected to "+connection.getInetAddress().getHostName()+ "\n");
	}
	private void setup() throws IOException
	{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
	}
	private void chatting() throws IOException
	{
		String message = "Connected!";
		sendmessage(message);
		canType(true);
		do
		{
			try
			{
				message = (String) input.readObject();
				showmessage(message+"\n");
			}
			catch(ClassNotFoundException e)
			{
				showmessage("commmand not recognized\n");
			}
		}
		while(!message.equals("Client - End"));
	}
	private void closeall() throws IOException
	{
		showmessage("Closing connection...\n");
		canType(false);
		try
		{
			output.close();
			input.close();
			connection.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	private void sendmessage(String message)
	{
		try
		{
			output.writeObject("CLIENT "+message);
			output.flush();
			showmessage("CLINET "+message+"\n");
		}
		catch(IOException e)
		{
			chatwindow.append("Error - message cannot be sent");
		}
	}
	private void showmessage(final String text)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						chatwindow.append(text);
					}
				});
	}
	private void canType(final boolean tof)
	{
		SwingUtilities.invokeLater(
				new Runnable()
				{
					public void run()
					{
						usertext.setEditable(tof);
					}
				});
	}
}
