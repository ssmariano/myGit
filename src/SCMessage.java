import java.io.File;
import java.io.Serializable;

public class SCMessage implements Serializable{
	
	private int opCode = OpTypes.OP_DEFAULT;
	private String username = null;
	private String password = null;
	private File resource = null;
	private String shareUser = null;
	private String owner=null;
	private String rep=null;

	/**
	 * SCMessage class constructor.
	 * @param opCode The operation code from the OpTypes class.
	 * @param username The user's id (repository's owner).
	 * @param password The user's password.
	 * @param resource The resource to be processed (if any).
	 * @param shareUser shareUser The user to whom the user wants to share
	 * the given resource (repository).
	 */
	public SCMessage(int opCode, String username, String password, File resource, String shareUser,String owner,String rep) {
		this.opCode = opCode;
		this.username = username;
		this.password = password;
		this.resource = resource;
		this.shareUser = shareUser;
		this.owner = owner;
		this.rep = rep;
	}

	/**
	 * Gets the operation code.
	 * @return The operation code from the OpTypes class.
	 */
	public int getOpCode() {
		return opCode;
	}

	/**
	 * Sets the operation code.
	 * @param opCode The new operation code from the OpTypes class.
	 */
	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}

	/**
	 * Gets the user's id (repository's owner).
	 * @return The user's id (repository's owner).
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the user's id (repository's owner).
	 * @param username The user's id (repository's owner).
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the user's password.
	 * @return The user's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the user's password.
	 * @param password The user's password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the resource to be processed (if any).
	 * @return The resource to be processed (if any).
	 */
	public File getResource() {
		return resource;
	}

	/**
	 * Sets the resource to be processed (if any).
	 * @param resource The resource to be processed (if any).
	 */
	public void setResource(File resource) {
		this.resource = resource;
	}

	/**
	 * Gets the user to whom the user wants to share
	 * the given resource (repository).
	 * @return The user to whom the user wants to share
	 * the given resource (repository).
	 */
	public String getShareUser() {
		return shareUser;
	}

	/**
	 * Sets the user to whom the user wants to share
	 * the given resource (repository).
	 * @param shareUser The user to whom the user wants to share
	 * the given resource (repository).
	 */
	public void setShareUser(String shareUser) {
		this.shareUser = shareUser;
	}
	
	public String getRep(){
		return rep;
	}
	
	public void setRep(String rep){
		this.rep=rep;
		
	}

	@Override
	public String toString() {
		return "SCMessage [opCode=" + opCode + ", username=" + ((username==null)?"NULL":username) + ", password="
				+ ((password==null)?"NULL":password) + ", resource=" + ((resource==null)?"NULL":resource.getPath()) 
				+ ", shareUser=" + ((shareUser==null)?"NULL":shareUser) + ", rep=" + ((rep==null)?"NULL":rep) + "]";
	}
	
}


