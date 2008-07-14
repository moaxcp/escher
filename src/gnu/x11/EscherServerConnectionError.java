/**
 * 
 */


package gnu.x11;

/**
 * Thrown when the client is not able to connect to the server.
 * 
 * @author Mario Torre <neugens@aicas.com>
 */
public class EscherServerConnectionError
    extends EscherException
{
  /**
   * 
   */
  public EscherServerConnectionError()
  {

    super();
  }

  /**
   * @param message
   */
  public EscherServerConnectionError(String message)
  {

    super(message);
  }

  /**
   * @param cause
   */
  public EscherServerConnectionError(Throwable cause)
  {

    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public EscherServerConnectionError(String message, Throwable cause)
  {

    super(message, cause);
  }
}
