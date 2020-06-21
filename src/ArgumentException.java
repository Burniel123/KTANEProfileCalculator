public class ArgumentException extends Exception
{
    /**
     * Standard exception constructor for this exception, without an error message.
     */
    public ArgumentException()
    {
        super();
    }

    /**
     * Standard exception constructor for this exception, with an error message.
     */
    public ArgumentException(String message)
    {
        super(message);
    }
}
