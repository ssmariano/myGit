/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/



/**
 * Class with shared settings between client and server. 
 * @author fc30396
 *
 */
public class ServerConfig {
	public static final String SERVER_DEF_ADDR = "localhost"; //Default server address (testing purposes)
	public static final int SERVER_DEF_PORT = 23456; //Default server port (testing purposes)
	public static final String DIR_HOME = System.getenv("HOME") + "/"; //System home directory
	public static final String DIR_REPOS = DIR_HOME + "repos/"; //Default repositories directory
	public static final String FILE_PASSWD = "userdb.txt"; //Default passwords filename
	public static final String FILE_SHARES = "sharedb.txt"; //Default shares filename
}
