//package gitclient;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class myGit {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		final String basePath = System.getProperty("user.home")+"/";
		
		
		
		
		
		

		SCMessage scm = null;
		boolean result = false;
		int commandLength = args.length;
		String repo = null;
		String username = null;
		String[] serverAddress = null;
		String serverAddressIP = ServerConfig.SERVER_DEF_ADDR;
		int serverAddressPort = ServerConfig.SERVER_DEF_PORT;
		Scanner sc = null;
		String password = null;
		String owner = null;                                                        
		File resource = null;
		String userId = null;
		String rep = null;// rep a partilhar
		
		GitClient client = new GitClient(serverAddressIP, serverAddressPort, basePath);

		if(args[0].equals("-init")) {
			//criar repo local
			repo = args[1];
			//System.out.println(ServerConfig.CLIENT_REPOS + repo);
			
			
			
			//File f = new File(ServerConfig.CLIENT_REPOS + repo);
			File f = new File(basePath + repo);
			if(f.exists()) {
				System.out.println("O repositorio " + repo + " já existe!");
				System.exit(0);
			} else {
				result = f.mkdirs();
				if(result) {
					System.out.println("Repositorio " + repo + " criado com sucesso!");
					System.exit(0);
				} else {
					System.out.println("Erro ao criar repositorio " + repo + "!");
					System.exit(1);
				}
			}
		} else {
			//enviar action para o server
			username = args[0];
			serverAddress = args[1].split(":"); // IP:port
			serverAddressIP = serverAddress[0];
			serverAddressPort = Integer.parseInt(serverAddress[1]);
			
			if(commandLength == 2) {
				//login/registo SEM password no comando
				password = client.askForPassword(sc);
				scm = new SCMessage(OpTypes.OP_LOGIN, username, password, null, null, null, null);
			} else if(commandLength == 4) {
				if(args[2].equals("-p")) {
					//login/registo COM password no comando
					password = args[3];
					scm = new SCMessage(OpTypes.OP_LOGIN, username, password, null, null, null, null);
				} else {
					//action push ou pull SEM password no comando
					password = client.askForPassword(sc);
					int op = (args[2].equals("-push")) ? OpTypes.OP_PUSH : OpTypes.OP_PULL;
					resource = new File(args[3]);
					//String[] path = resource.getPath().split("/");
					//rep = (path.length < 2) ? path[0] : path[path.length-2];
					scm = new SCMessage(op, username, password, resource, null, null, rep);
				}
			} else if(commandLength == 5) {
				//action share ou remove SEM password no comando
				password = client.askForPassword(sc);
				int op = (args[2].equals("-share")) ? OpTypes.OP_CREATE_SHARE : OpTypes.OP_REMOVE_SHARE;
				resource = new File(args[3]);
				scm = new SCMessage(op, username, password, resource, null, null, resource.getName());
			} else if(commandLength == 6) {
				//action push ou pull COM password no comando
				int op = (args[4].equals("-push")) ? OpTypes.OP_PUSH : OpTypes.OP_PULL;
				resource = new File(args[5]);
				//String[] path = resource.getPath().split("/");
				//rep = (path.length < 2) ? path[0] : path[path.length-2];
				password = args[3];
				scm = new SCMessage(op, username, password, resource, null, null, rep);
			} else {
				//action share ou remove COM password no comando
				int op = (args[4].equals("-share")) ? OpTypes.OP_CREATE_SHARE : OpTypes.OP_REMOVE_SHARE;
				resource = new File(args[5]);
				userId = args[6];
				password = args[3];
				scm = new SCMessage(op, username, password, resource, userId, null, resource.getName());
			}
			
		}
		
		

		
		
		System.out.println(scm.toString());
		
		
		

		

		try {
			Boolean success = client.startClient(scm);
			
			
			
			if (success) {
				if (scm.getOpCode() == 20) {
					client.pushFile(scm.getResource().toString());
					client.lastCheckPush(basePath + scm.getRep());
				} else if (scm.getOpCode() == 30) {
					client.pullFile(scm.getResource().toString());
					//client.lastCheckPull();

				} else if (scm.getOpCode() == 40) {
					// gives access to repo
					client.receivesShareResponse();

				} else if (scm.getOpCode() == 41) {
					// removes access to repo
					client.receivesShareResponse();

				}

			} else {
				System.err.println("ligacao falhou");
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	

	
}