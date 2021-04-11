package util.exception;

public class BusinessCategoryExistException extends Exception {

    public BusinessCategoryExistException() 
    {
    }

    public BusinessCategoryExistException(String msg) 
    {
        super(msg);
    }
}