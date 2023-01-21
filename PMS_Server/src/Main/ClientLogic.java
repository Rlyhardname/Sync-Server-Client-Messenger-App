package Main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class ClientLogic extends Thread {

	private InetAddress host;
	private final int PORT = 1337;
	String username;
	private String password;
	private BufferedReader input;
	private PrintWriter output;
	private ClientOperationGUI clientGUI;
	private Socket link;
	private ClientLoginGUI login;
	private boolean started;
	private DataOutputStream outputFile;
	private DataInputStream inputFile;
	private String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	ClientLogic() {
		{
			try {
				setStarted(false);
				host = InetAddress.getLocalHost();
				link = new Socket(host, PORT);
				input = new BufferedReader(new InputStreamReader(link.getInputStream()));
				output = new PrintWriter(link.getOutputStream(), true);
				outputFile = new DataOutputStream(link.getOutputStream());
				inputFile = new DataInputStream(link.getInputStream());
			} catch (IOException e) {
				System.out.println("Host ID not found");
			}

		}
	}

	@Override
	public void run() {
	}

	public void runHandleServer() {
		setStarted(true);
		Thread handle = new Thread(new Runnable() {

			@Override
			public void run() {
				if (!ServerSettings.onlineUsers.isEmpty()) {
					handleServer();
				}
				setStarted(false);

			}

		});
		handle.start();

	}

	public void handleServer() {
		do {
			try {

				String msgIN = input.readLine();
				int duckTapeFix = 0;

				System.err.println("Message in client.handleServer" + msgIN);
				if (msgIN == null) {
					break;
				}
				String[] splitMessage = msgIN.split(",");
				if(splitMessage.length<4) {
					continue;
				}
				if(splitMessage.length==5) {
					duckTapeFix=1;
				}
//				for (String string : splitMessage) {
//					System.err.println(string);
//				}
				if (splitMessage[3].equals("ReceiveFile")) {

					// TODO Auto-generated method stub
				//	System.err.println("tuka?");
					String path = saveDirectory();
					receiveFile(path);
					clientGUI.concattArea(msgIN);
//					Thread tr = new Thread(new Runnable() {
//
//						@Override
//						public void run() {
//							
//						}
//						
//					}); tr.start();
							
				
				} else if(splitMessage[3+duckTapeFix].equals("TextMessage")){
					clientGUI.concattArea(splitMessage[0+duckTapeFix]);
				}
//				if (splitMessage[0].equals("sendFile")) {
//					
//					sendFile(getFilePath());
//				//	output.println("sent");
//					
//				}

//				if(!splitMessage[0].equals("ReceiveFile")) {
//					
//				}
//				if(splitMessage[0]=="-1") {
//					break;
//				}
//				
			} catch (IOException e) {
				break;
			} catch (NullPointerException e1) {
				break;
			}
			
		} while (true);
	}

	boolean accessServer(String string) {
		loginMessage(string, username, password);
		if (isLoginSuccess()) {

			return true;
		}
		return false;
	}

	public boolean isLoginSuccess() {
		String serverMsg = receiveMessage();
		if (serverMsg.equals("LoginSuccess,Succesfully logged in!")) {
			return true;
		}
		System.out.println(serverMsg);
		return false;

	}

	public String receiveMessage() {
		String msg = "";
		try {
			msg = input.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}

	public boolean login() {
		String message = "", serverMsg = "";
		do {

			try {
				serverMsg = input.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (serverMsg.equals("Username")) {
				System.out.println("Entering username: Account1");
				System.out.println(serverMsg);
				output.println(username);
			} else if (serverMsg.equals("Password")) {
				System.out.println("Entering password: password");
				System.out.println(serverMsg);
				output.println(password);
			} else if (serverMsg.equals("LoginSuccess\" + \",\" + \"Succesfully logged in!")) {
				System.out.println(serverMsg);
				message = "*CLOSE*";
			} else {

				System.out.println(serverMsg);
			}

		} while (!message.equals("*CLOSE*"));
		return true;
	}

	public StringBuffer concatStrings(String... data) {
		StringBuffer concat = new StringBuffer();

		for (String string : data) {
			concat.append(string);
			concat.append(",");
		}

		concat.deleteCharAt(concat.length() - 1);
		return concat;
	}

	public void loginMessage(String option, String user, String pass) {
		String msg = concatStrings(user, pass).toString();
		if (option.equals("login")) {
			output.println("LOGIN" + "," + msg);
		} else if (option.equals("signup")) {
			output.println("SIGN UP" + "," + msg);
		}

	}

	public void sendFile(String path)

	{
		int bytes = 0;
		// Open the File where he located in your pc
		File file = new File(path);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// send the File
		try {
			outputFile.writeLong(file.length());
			// break file into chunks
			byte[] buffer = new byte[4 * 1024];
			while ((bytes = fileInputStream.read(buffer)) != -1) {
				// Send the file to Server Socket
				outputFile.write(buffer, 0, bytes);
				//System.out.println("ostanali baitove " +  bytes);
				outputFile.flush();
			}
			// close the file here
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receiveFile(String fileName)

	{
		int bytes = 0;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			//System.err.println("priema li?");
			
			long size = inputFile.readLong(); // read file size
			byte[] buffer = new byte[4 * 1024];
			while (size > 0 && (bytes = inputFile.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
				// write the file using write method
				fileOutputStream.write(buffer, 0, bytes);
			//	System.err.println("priema li?");
				size -= bytes; // read upto file size
				
			}

			System.out.println("File is Received");
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String saveDirectory() {
		JFileChooser fileChooserj = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooserj.setFileSelectionMode(JFileChooser.FILES_ONLY);

		File file = fileChooserj.getSelectedFile();
	//	System.out.println(file.getName());
		
		fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("jpg","jpg"));
		fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("gif","gif"));
		fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("txt","txt"));
		fileChooserj.setAcceptAllFileFilterUsed(true);

		String path = "";
		int r = fileChooserj.showSaveDialog(null);
		if (r == JFileChooser.APPROVE_OPTION) {
			path = (fileChooserj.getSelectedFile().getAbsolutePath());
		}

		return path;
	}
	
	public String pickFile() {
		JFileChooser fileChooserj = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		fileChooserj.setFileSelectionMode(JFileChooser.FILES_ONLY);

		File file = fileChooserj.getSelectedFile();
	//	System.out.println(file.getName());
		
		fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("jpg","jpg"));
		fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("gif","gif"));
		fileChooserj.addChoosableFileFilter(new FileNameExtensionFilter("txt","txt"));
		fileChooserj.setAcceptAllFileFilterUsed(true);

		String path = "";
		int r = fileChooserj.showSaveDialog(null);
		if (r == JFileChooser.APPROVE_OPTION) {
			path = (fileChooserj.getSelectedFile().getAbsolutePath());
		}

		return path;
	}
	
	

	public void sendMessage(String msg) {

		output.println(msg);

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public InetAddress getHost() {
		return host;
	}

	public void setHost(InetAddress host) {
		this.host = host;
	}

	public int getPORT() {
		return PORT;
	}

	public ClientLoginGUI getLogin() {
		return login;
	}

	public void setLogin(ClientLoginGUI login) {
		this.login = login;
	}

	public ClientOperationGUI getGui() {
		return clientGUI;
	}

	public void setGui(ClientOperationGUI gui) {
		this.clientGUI = gui;
	}

	public BufferedReader getInput() {
		return input;
	}

	public void setInput(BufferedReader input) {
		this.input = input;
	}

	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput(PrintWriter output) {
		this.output = output;
	}

	public Socket getLink() {
		return link;
	}

	public void setLink(Socket link) {
		this.link = link;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

}
