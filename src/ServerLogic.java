import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ServerLogic {
	
	
	/*
	 * Funçao que escreve para um ficheiro
	 * out - fileoutputstream a usar
	 * file - ficheiro onde escreve
	 * towrite - string a ser escrita
	 */
	public static void writeToFile (FileOutputStream out, File file, String toWrite) throws IOException{
		
		byte[] contentInBytes = toWrite.getBytes();
		out.write(contentInBytes);
		out.flush();	
	}
	
	/*
	 * Funçao que le todos os utilizadores do ficheiro
	 * e retorna cada um numa posiçao de um arraylist<String>
	 */
	public static ArrayList<String> readFromFile(FileInputStream in, File file) throws IOException{

		ArrayList<Byte> contentInBytes=new ArrayList<Byte>();
		int content;
		while ((content = in.read()) != -1)
			contentInBytes.add((byte) content);
		
		byte[] bytes=new byte[contentInBytes.size()];
		
		int k=0;
		while(contentInBytes.size()>k){
			bytes[k]=contentInBytes.get(k);
			k++;
		}
			
		String read;
		read = new String(bytes, StandardCharsets.UTF_8);
		System.out.println(read+"String read");
		ArrayList<String> utilizadores = new ArrayList<String> ();
		utilizadores.clear();
		
		StringTokenizer st = new StringTokenizer(read,"\n");
		
		while(st.hasMoreTokens()){
			utilizadores.add(st.nextToken());
		}
		
		
		int i =0;
		while(utilizadores.size()>i){
			System.out.println(utilizadores.get(i));
			i++;
		}
		return utilizadores;
	}
	
	public static boolean existeUser(ArrayList<String> utilizadores,
			String utilizador, String pwd, FileOutputStream out,
			ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException, ClassNotFoundException{
		boolean existe = false;
		ResultMessage result=new ResultMessage();
		int nUser=0;
		while(utilizadores.size()>nUser){
			System.out.println("Utilizador a ser analisado no login "+separaUserPwd(utilizadores.get(nUser))[0]);
			System.out.println(utilizador+" iguais? "+separaUserPwd(utilizadores.get(nUser))[0]);
			//Existe utilizador?
			if(separaUserPwd(utilizadores.get(nUser))[0].equals(utilizador)){
				System.out.println("Existe utilizador, password: "+pwd);
				//Password correta
				if(separaUserPwd(utilizadores.get(nUser))[1].equals(pwd)){
					System.out.println("password correta, "+pwd);
					
					
					
					return existe=true;
				}
				else{
					System.out.println("password errada!");
					System.out.println("Password errada, tente novamente!");
					result.setSuccess(false);
					result.setMessage("Password errada, tente novamente!");
					//outStream.writeObject(result);
					return existe;
				}
			}
			nUser++;
			//System.out.println(nUser);
		}
		//Utilizador nao esta registao
		
		System.out.println("Registo");
		//BufferedReader d = new BufferedReader(new InputStreamReader(System.in));
		//String userInput = null;
		
		outStream.writeObject(new Boolean(true));
		String password = (String) inStream.readObject();
		
		
		
		if(pwd.equals(password)){
			writeToFile(out,null,utilizador+":"+password+"\n");
			System.out.println("O utilizador "+utilizador+" foi registado!");
			
			existe=true;
			outStream.writeObject(new String("Utilizador "+ utilizador + " registado com sucesso."));
			
		}
		else{
			System.out.println("Passwords diferentes, repita o comando myGit...");
			result.setSuccess(false);
			result.setMessage("Passwords diferentes, repita o comando myGit...");
			//outStream.writeObject(result);
			return false;
			
		}
		return existe;
	}
	
	private static String[] separaUserPwd(String utilizador){
		String[] user_pwd=new String[2];
		StringTokenizer st = new StringTokenizer(utilizador, ":");
		int i=0;
		while(i<2){
			user_pwd[i]=st.nextToken();
			i++;
		}
		
		return user_pwd;
	}
	
	public static boolean givePermission (ArrayList<String> utilizadores,ArrayList<String> reps,
			String rep, String dono,String pwd, String giveToUser,FileOutputStream out,
			ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException, ClassNotFoundException{
		boolean permission=false;
		
		ResultMessage result=new ResultMessage();
		//Verifica login
		if(!existeUser(utilizadores,dono,pwd,out,outStream,inStream)){
			System.out.println("Dados de login errados, tente novamente!");
			result.setSuccess(false);
			result.setMessage("Dados de login errados, tente novamente!");
			//outStream.writeObject(result);
			return permission;
			
		}
		
		int nRep=0;
		ArrayList<String> repositorioN=new ArrayList<String>();
		
		while(reps.size()>nRep){
			//Existe repositorio?
			repositorioN=separaRepDonoPermissions(reps.get(nRep));
			if(repositorioN.get(0).equals(rep)){
				System.out.println("Existe repositorio?"+rep);
				//O dono é o dono?
				if(repositorioN.get(1).equals(dono)){
					System.out.println("Dono eh o "+dono);
					//Comeca a 2 porque nao quero comparar com o nome
					//do rep nem com o dono
					int i=2;
					//O giveTouser ja tem permissao, ou seja, já estah na lista?
					while(i<repositorioN.size()){
						System.out.println("User a testar: "+repositorioN
								.get(i));
						if(repositorioN.get(i).equals(giveToUser)){
							System.out.println("O "+ giveToUser+" jah tem permissao.");
							result.setSuccess(true);
							result.setMessage("O "+ giveToUser+" jah tem permissao.");
							//outStream.writeObject(result);
							permission= true;
							return permission;
						}
						i++;
					}
					
					//Se chegou aqui eh porque o giveToUser ainda nao tem permissao, ha que dar-lha
					File file1 = new File("permissoesRepositorios.txt");
					
					FileOutputStream fop1=new FileOutputStream(file1,true);
					FileOutputStream fop2=new FileOutputStream(file1);
					
					String toWrite="";
					reps.remove(nRep);
					if(reps.size()!=0){
						toWrite=reps.get(0);
						writeToFile(fop2,file1,toWrite);
						//Escreve o novo ficheiro
						for(String s : reps){
							toWrite=s+="\n";
							System.out.println("String a escrever: "+toWrite);
							writeToFile(fop1,file1,toWrite+"\n");
						}
					}
					//Escreve a nova linha
					for(String s : repositorioN){
						toWrite+=s+=":";
						//System.out.println(toWrite);
					}
					toWrite+=giveToUser;
					System.out.println(toWrite);
					
					writeToFile(fop1,file1,toWrite+"\n");
					fop1.close();
					fop2.close();

					//////////////////////////////////////////
					
					
					System.out.println("Foi dada permissao no repositorio "+rep+ " ao utilizador "+giveToUser+".");
					result.setSuccess(true);
					result.setMessage("Foi dada permissao no repositorio "+rep+ " ao utilizador "+giveToUser+".");
					//outStream.writeObject(result);
					
					return permission=true;
				}
				else{
					System.out.println("Voce nao eh dono desse repositorio");
					result.setSuccess(false);
					result.setMessage("Foi dada permissao no repositorio "+rep+ " ao utilizador "+giveToUser+".");
					//outStream.writeObject(result);
					return permission=false;
				}
			}
			nRep++;
			
		}
	
		return permission;
		
	}
	
	private static ArrayList<String> separaRepDonoPermissions(String utilizador){
		ArrayList<String> permissions=new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(utilizador, ":");
		while(st.hasMoreTokens())
			permissions.add(st.nextToken());

		
		return permissions;
	}

	
	/*
	 * funcao que verifica se o userAcesso tem realmente acesso ao repositorio rep
	 */
	public static boolean temAcesso(ArrayList<String> reps, String rep, String userAcesso,
			ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException{
		boolean temAcesso=false;
		ResultMessage result=new ResultMessage();
		int nRep=0;
		ArrayList<String> repositorioN=new ArrayList<String>();
		
		while(reps.size()>nRep){
			repositorioN=separaRepDonoPermissions(reps.get(nRep));
			int i=1;
			while(i<repositorioN.size()){
				System.out.println("User a testar: "+repositorioN.get(i));
				if(repositorioN.get(i).equals(userAcesso)){
					System.out.println("O "+ userAcesso+" tem permissao de acesso.");
					result.setSuccess(true);
					result.setMessage("O "+ userAcesso+" tem permissao de acesso.");
					//outStream.writeObject(result);
					
					temAcesso= true;
					return temAcesso;
				}
				i++;
			
			}	
		nRep++;
		
	
		}
		result.setSuccess(false);
		result.setMessage("O "+ userAcesso+" nao tem permissao de acesso.");
		//outStream.writeObject(result);
		return temAcesso;
	}
	
	public static boolean removePermission (ArrayList<String> utilizadores,ArrayList<String> reps,
			String rep, String dono,String pwd, String removeUser,FileOutputStream out,
			ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException, ClassNotFoundException{
		boolean removePermission=false;
		ResultMessage result=new ResultMessage();
		//Verifica login
		if(!existeUser(utilizadores,dono,pwd,out,outStream,inStream)){
			System.out.println("Dados de login errados, tente novamente!");
			return removePermission;
			
		}
		
		int nRep=0;
		int repAlterado=0;
		ArrayList<String> repositorioN=new ArrayList<String>();
		
		while(reps.size()>nRep){
			//Existe repositorio?
			repositorioN=separaRepDonoPermissions(reps.get(nRep));
			if(repositorioN.get(0).equals(rep)){
				System.out.println("Existe repositorio?"+rep);
				//O dono é o dono?
				if(repositorioN.get(1).equals(dono)){
					System.out.println("Dono eh o "+dono);
					//Comeca a 2 porque nao quero comparar com o nome
					//do rep nem com o dono
					int i=2;
					//O giveTouser ja tem permissao, ou seja, já estah na lista?
					while(i<repositorioN.size()){
						System.out.println("User a testar: "+repositorioN.get(i));
						if(repositorioN.get(i).equals(removeUser)){
							System.out.println("O "+ removeUser+" tem permissao.");
							repositorioN.remove(i);
							System.out.println("O "+ removeUser+" perdeu a permissao.");
							removePermission= true;
							i=repositorioN.size();
							repAlterado=nRep;
							nRep=reps.size();
						}
						i++;
					}
					
					
					File file1 = new File("permissoesRepositorios.txt");
					
					FileOutputStream fop1=new FileOutputStream(file1,true);
					FileOutputStream fop2=new FileOutputStream(file1);
					
					String toWrite="";
					reps.remove(repAlterado);
					if(reps.size()!=0){
						toWrite=reps.get(0);
						writeToFile(fop2,file1,toWrite);
						//Escreve o novo ficheiro
						for(String s : reps){
							toWrite=s+="\n";
							System.out.println("String a escrever: "+toWrite);
							writeToFile(fop1,file1,toWrite+"\n");
						}
					}
					//Escreve a nova linha
					for(String s : repositorioN){
						toWrite+=s+=":";
						//System.out.println(toWrite);
					}
					System.out.println(toWrite);
					
					writeToFile(fop1,file1,toWrite+"\n");
					fop1.close();
					fop2.close();
					result.setSuccess(true);
					result.setMessage("O "+ removeUser+" perdeu a permissao.");
					//outStream.writeObject(result);

					return removePermission;
				}
				else{
					System.out.println("Voce nao eh dono desse repositorio");
					result.setSuccess(false);
					result.setMessage("Voce nao eh dono desse repositorio");
					//outStream.writeObject(result);
					return removePermission=false;
				}
			}
			nRep++;
			
		}
	
		return removePermission;
		
	}
	public static boolean registaNovoRepositorio(
			String rep, String dono,FileOutputStream fop1,ArrayList<String> reps,
			ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException{
		
		ResultMessage result=new ResultMessage();
		boolean adicionado=false;
		int nRep=0;
		ArrayList<String> repositorioN=new ArrayList<String>();
		while(reps.size()>nRep){
			//Existe repositorio?
			repositorioN=separaRepDonoPermissions(reps.get(nRep));
			if(repositorioN.get(0).equals(rep)){
				System.out.println("Já existe um repositorio com esse nome, escolha outro nome e repita o comando "+rep);
				result.setSuccess(false);
				result.setMessage("Já existe um repositorio com esse nome, escolha outro nome e repita o comando "+rep);
				//outStream.writeObject(result);
				return adicionado;
			}
			nRep++;
		}
		//Se chegou aqui eh porque nao encontro repositorio com o mesmo nome
		System.out.println("Repositorio registado "+ repositorioN.get(0));
		String toWrite = rep+":"+dono+"\n";
		writeToFile(fop1,null,toWrite);
		result.setSuccess(true);
		result.setMessage("Repositorio registado "+ repositorioN.get(0));
		//outStream.writeObject(result);
		adicionado=true;
		return adicionado;
		
	}
	public static boolean login(ArrayList<String> utilizadores,
			String utilizador, String pwd, ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException{
		boolean login=false;
		ResultMessage result=new ResultMessage();
		int nUser=0;
		while(utilizadores.size()>nUser){
			System.out.println("Utilizador a ser analisado no login "+separaUserPwd(utilizadores.get(nUser))[0]);
			System.out.println(utilizador+" iguais? "+separaUserPwd(utilizadores.get(nUser))[0]);
			//Existe utilizador?
			if(separaUserPwd(utilizadores.get(nUser))[0].equals(utilizador)){
				System.out.println("Existe utilizador, password: "+pwd);
				//Password correta
				if(separaUserPwd(utilizadores.get(nUser))[1].equals(pwd)){
					System.out.println("password correta, "+pwd);
					result.setSuccess(true);
					result.setMessage("password correta, "+pwd);
					System.out.println(outStream==null);
					//outStream.writeObject(result);
					
					return login=true;
				}
				else{
					System.out.println("Password errada, tente novamente!");
					result.setSuccess(false);
					result.setMessage("Password errada, tente novamente!");
					//outStream.writeObject(result);
					return login;
				}
			}
			nUser++;
			//System.out.println(nUser);
		}
		//Utilizador nao esta registao
		
		
		return login;
		
	}
	
	
	public boolean allTests(ArrayList<String> reps,ArrayList<String> utilizadores ,String rep, String userAcesso,String pwd,

			ObjectOutputStream outStream, ObjectInputStream inStream) throws IOException{

			boolean retorna=false;


			if(ServerLogic.login(utilizadores, userAcesso, pwd, outStream, inStream)){

			if(ServerLogic.temAcesso(reps, rep, userAcesso, outStream, inStream)){

			return true;


			}

			}


			return retorna;


			}

	
}
