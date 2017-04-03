/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/



/**
 * Class with possible operations to execute on the server side.
 * @author fc30396
 *
 */
public class OpTypes {
	public static final int OP_DEFAULT = 0; // Default/Error Operation
	public static final int OP_LOGIN = 10; // Check user and pass from server file
	public static final int OP_PUSH = 20; // Push resource to server
	public static final int OP_PULL = 30; // Pull resource from server
	public static final int OP_CREATE_SHARE = 40; // Share repository with user
	public static final int OP_REMOVE_SHARE = 41; // Remove shared repository from user
}