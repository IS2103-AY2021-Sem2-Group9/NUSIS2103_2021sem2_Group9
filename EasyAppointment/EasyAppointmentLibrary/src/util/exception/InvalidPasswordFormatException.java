package util.exception;


public class InvalidPasswordFormatException extends Exception {
    public InvalidPasswordFormatException()
    {
        
    }
    
    public InvalidPasswordFormatException(String msg)
    {
        super(msg);
    }
}
