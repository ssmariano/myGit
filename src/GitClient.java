//package gitclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GitClient {

	private Socket sock;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;

	private String host;
	private int port;
	private String BasePath;
	Scanner sc = null;
	
	public GitClient(String host, int port, String basePath) {
		this.host = host;
		this.port = port;
		this.BasePath = basePath;
		
	}
	

	/**
	 * 
	 * @param soc
	 * @param outStream
	 * @param inStream
	 * @return True if the connection to the server was done; else False
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Boolean startClient(SCMessage scm) throws IOException, ClassNotFoundException{
		this.sock = new Socket(host,port);
		this.outStream = new ObjectOutputStream(this.sock.getOutputStream());
		this.inStream = new ObjectInputStream(this.sock.getInputStream());

		System.out.println(scm.toString());
		outStream.writeObject(scm);
		
		
		//se o OpCode for 10 e porque o utilizdor nao existe e e para criar
		if (scm.getOpCode()==10){
			//recebe true
			Boolean toConfirmPass = (Boolean) this.inStream.readObject();
			//confirma pass
			if (toConfirmPass){
				String confirmedPassword = GitClient.askForPassword(sc);
				//envia pass
				outStream.writeObject(new String(confirmedPassword));
				String message = (String) this.inStream.readObject();
				System.out.println(message);
			}else
				System.out.println("O utilizador que pretende registar ja existe");
			
		}
		
		
		Boolean success = (Boolean) this.inStream.readObject();
		return success;

	}

	
	//#################################################
	//##            pull from server to client       ##
	//#################################################
		
	
	/**
	 * 
	 * @param path directory_name or directory_name/file_name
	 * @param isDir just to simplify server inform if is directory or not 
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public void pullFile(String path) throws IOException, ClassNotFoundException {
		//first send to server what action
		//this.outStream.writeObject("pull");
		boolean temPermissao=(boolean)this.inStream.readObject();
		
			this.outStream.writeObject(path);
		
			String tmpRes = (String) this.inStream.readObject();
			if(temPermissao){
			Boolean isDir = false;
			if(tmpRes.equals("dir")) {
				isDir = true;
			} else if(tmpRes.equals("file")) {
				isDir = false;
			} else {
				System.out.println(tmpRes);
				return;
			}
			
		
			this.receiveFilesFromServer(isDir, path);
			this.lastCheckPull();
		}else
			System.out.println("Não tem permissao para fazer o pull desse repositorio");
		
	}
	
	private void receiveFilesFromServer(Boolean isDir, String path) throws ClassNotFoundException, IOException {
		
		if (isDir) {
			int numberOfFiles = (int) this.inStream.readObject();
			

			// create directory
			File serverDirectoryName = new File(this.BasePath + path);

			
			// if directory not exists, create it
			if (!serverDirectoryName.exists()) {
				serverDirectoryName.mkdir();
			}

			while (numberOfFiles > 0) {
				// file name
				String tmpName = (String) this.inStream.readObject();

				this.receiveFile(tmpName, path);

				outStream.writeObject(new Boolean(true));
				numberOfFiles--;
			}

		} else {
			// parent folder
			//String parentFolder = (String) inStream.readObject();

			this.receiveFile(path, "");
		}
	}

	private long getLastModifiedFileInClient(String directory, String name) {
		File tmpFile = new File(this.BasePath + directory + "/" + name);
		System.out.println(tmpFile);
		return tmpFile.lastModified();
	}

	// receber terceiro agumento que vai ser push ou pull
	private int checkLastModifier(long timeStampFileServer, long timeStampeFileClient) {
		

		if (timeStampFileServer < timeStampeFileClient) {
			
			return 0;
		} else if (timeStampFileServer > timeStampeFileClient) {
			
			return 1;
			// write file from server to client
		} else {
			System.out.println("Ficheiros atualizados.");
			// inform both directories are updated
			return 0;
		}

	}

	private void receiveFile(String fileName, String directoryName) throws IOException, ClassNotFoundException {

		
		//cria directoria no cliente se quando faz pull ela nao existe
		
		
		
		String newFilePath = this.BasePath + directoryName + "/" + fileName;

		Integer fileSize = (Integer) this.inStream.readObject();
		// last modified
		long lastModServerV = (long) this.inStream.readObject();
		long lastModClientV = getLastModifiedFileInClient(directoryName, fileName);
		int newest = this.checkLastModifier(lastModServerV, lastModClientV);
		// send to clien whitch are new
		this.outStream.writeObject((int) newest);
		if (newest == 0) {
			return;
		}
		//int numberOfFiles = this.numberOfFilesInFolder(this.BasePath + directoryName, newFilePath);

		// verificar se newFilePathExiste
		// se existe verificar os ficheiros
		
		

		File newFile = new File(newFilePath);
		

		String parentFolder = newFile.getParentFile().getName();
		File checkDirectory = new File(BasePath + parentFolder);
		if (!checkDirectory.exists()) {
			checkDirectory.mkdir();
		}
		

		
		OutputStream fos = new FileOutputStream(newFilePath);
		

		byte[] mybytearray = new byte[1024];
		int count;
		int totalRead = 0;
		InputStream inStreamFile = this.sock.getInputStream();
		while ((count = inStreamFile.read(mybytearray)) > 0) {
			fos.write(mybytearray, 0, count);
			totalRead += count;
			if (totalRead == fileSize) {
				break;
			}
		}
		System.out.println("\rTotal Read: " + totalRead);
		fos.close();
		newFile.setLastModified(lastModServerV);
		
	}
	private int numberOfFilesInFolder(String folderName, String fileName) {

		File myFolder = new File(folderName);
		File myFile = new File(fileName);
		

		int count = 0;
		for (File f : myFolder.listFiles()) {
			if (f.getName().startsWith(myFile.getName())) {
				count++;
				System.out.println(count);
			}
		}

		return count;

	}
	
	

	//#################################################
	//##            push to server code              ##
	//#################################################
	
	
	/*
	 * write a file to the server, after success is True
	 */
	public void pushFile(String path) throws IOException, ClassNotFoundException {
		//first send to server what action
		
		//this.outStream.writeObject("push");
		
		System.out.println("ligado e autenticado");

		File myFile = new File(this.BasePath + path);
		
		if (!myFile.exists()){
			System.out.println("o ficheiro que pretende enviar para o gitServer não existe");
			return;
		}
		
		boolean isDirectory = myFile.isDirectory(); // Check if it's a directory
		

		// send file name
		String fileName = myFile.getName();

		

		int fileSize = (int) myFile.length();

		// parameters of the file send to the server
		this.outStream.writeObject(fileName);
		this.outStream.writeObject(new Integer(fileSize));
		// this.outStream.writeObject(timestamp);
		this.outStream.writeObject(isDirectory);

		this.sendFiles(isDirectory, myFile);
	}
	private void sendFiles(boolean isDirectory, File myFile) throws ClassNotFoundException, IOException {
		if (isDirectory) {
			this.outStream.writeObject(myFile.listFiles().length);
			
			String files[] = myFile.list();

			for (String file : files) {
				File srcFile = new File(myFile, file);
				
				this.outStream.writeObject(srcFile.getName());
				this.sendOneFile(srcFile);
				Boolean success = (Boolean) this.inStream.readObject();
			}
		} else {
			this.outStream.writeObject(myFile.getParentFile().getName());
			this.sendOneFile(myFile);
		}
	}

	private void sendOneFile(File myFile) throws IOException, ClassNotFoundException {
		
		if (!myFile.exists()){
			System.out.println("o ficheiro que pretende enviar para o gitServer não existe");
			return;
		}
		// send size of file
		this.outStream.writeObject((int) myFile.length());
		// send last modified
		this.outStream.writeObject((long) myFile.lastModified());

		int newest = (int) this.inStream.readObject();
		if (newest == 0) {
			System.out.println("os ficheiros sao iguais");
			return;
		}

		
		byte[] mybytearray = new byte[1024];
		InputStream fileIn = new FileInputStream(myFile);
		int count;
		int totalSend = 0;
		OutputStream outStreamFile = this.sock.getOutputStream();

		// send file
		while ((count = fileIn.read(mybytearray)) > 0) {
			outStreamFile.write(mybytearray, 0, count);
			totalSend += count;
		}
		outStreamFile.flush();
		fileIn.close();
		System.out.print("\rtotal Sent:" + totalSend);
	}
	
	
	public void lastCheckPush(String path) throws IOException{
		File myFile = new File( path);
		
		this.outStream.writeObject((File) myFile);
	}
	
	public void lastCheckPull() throws ClassNotFoundException, IOException{
		File testFileServer = (File) this.inStream.readObject();
		
		
		File clientDirectoryName = new File(BasePath + testFileServer.getName());
		
		
		
		ArrayList<String>  filesFromServer =new ArrayList<String>(Arrays.asList(testFileServer.list()));
		ArrayList<String> filesFromClient = new ArrayList<String>( Arrays.asList(clientDirectoryName.list()));
		
		//remove duplicates
		
		
		filesFromClient.removeAll(filesFromServer);
		
		
		ArrayList<String> filesToRemove = new ArrayList<String>();
		
		
		for (String tmpFile : filesFromServer) {
			if(!tmpFile.endsWith("*_")) {
				filesToRemove.add(tmpFile);
			}
		}
		
		if (!filesFromClient.isEmpty()){
			System.out.println("Os seguintes ficheiros nao estao no seu repositorio git: ");
			System.out.println(filesFromClient);
		} else {
			System.out.println("o repositorio esta atualizado");
		}
		
	}
	
	public void receivesShareResponse() throws ClassNotFoundException, IOException{
	
		String response = (String)inStream.readObject();
		System.out.println(response);
	
	
	}
	
	public static String askForPassword(Scanner sc) {
		String password = null;
		sc = new Scanner(System.in);
		System.out.println("Password: ");
		password = sc.nextLine();
		sc.close();
		return password;
	}
}