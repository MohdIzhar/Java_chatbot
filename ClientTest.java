package server_client;
import java.io.IOException;

import javax.swing.JFrame;
public class ClientTest 
{

	public static void main(String[] args) throws IOException 
	{
		Client homeAddress = new Client("127.0.0.1");
		homeAddress.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		homeAddress.startRunning();
		
	}

}
