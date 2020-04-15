package server_client;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame
{
	private JTextField usertext;
	private JTextArea chatwindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server()
	{
		super("SERVER");
		usertext = new JTextField();
		usertext.setEditable(false);
		usertext.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent event)
					{
						sendMessage(event.getActionCommand());
						usertext.setText("");
					}
				});
		add(usertext,BorderLayout.SOUTH);
		chatwindow = new JTextArea();
		chatwindow.setEditable(false);
		add(new JScrollPane(chatwindow));
		setSize(1280,720);
		setVisible(true);
	}
	public void startRunning()
		{
			try
			{
				server = new ServerSocket(3000,20);
				while(true)
				{
					try
					{
						connection();
						setup();
						chatting();
					}
					catch(IOException e)
					{
						showMessage("\n Server ended the connection! ");
					}
					finally
					{
						closeAll();
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	private void connection() throws IOException
		{
			showMessage("waiting for the connection..\n");
			connection = server.accept();
			showMessage(connection.getInetAddress().getHostName()+" connection");
		}
	private void setup() throws IOException
		{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage("Everything is setup!\n");
		}
	private void chatting() throws IOException
		{
			String message = "Connected!";
			sendMessage(message);
			canType(true); //a lock on text box so that only one client can talk to server at same time not mulitplle
			do
			{
				try
				{
					message = (String) input.readObject();
					showMessage(message + "\n");
				}
				catch(ClassNotFoundException e)
				{
					showMessage("commmand not recognized\n");
				}
			}
			while(!message.equals("Client - End"));
		}
	private void closeAll() throws IOException
		{
			showMessage("Closing connection...\n");
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
	private void sendMessage(String message)
		{
			try
			{
				output.writeObject("SERVER "+message);
				output.flush();
				showMessage("SERVER "+message+"\n");
			}
			catch(IOException e)
			{
				chatwindow.append("Error - message cannot be sent");
			}
		}
	private void showMessage(final String text)
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
