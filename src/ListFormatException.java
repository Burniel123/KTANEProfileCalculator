/**
 * Models exceptional behaviour in the form of a list used in Create mode being badly formatted.
 *
 * @author Daniel Burton
 */
public class ListFormatException extends Exception
{
    /**
     * Standard exception constructor for this exception, without an error message.
     */
    public ListFormatException()
    {
        super();
    }

    /**
     * Standard exception constructor for this exception, with an error message.
     */
    public ListFormatException(String message)
    {
        super(message);
    }
}
