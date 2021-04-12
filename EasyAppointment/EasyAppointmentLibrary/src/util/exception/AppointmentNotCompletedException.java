package util.exception;

public class AppointmentNotCompletedException extends Exception{

    public AppointmentNotCompletedException() {
    }

    public AppointmentNotCompletedException(String string) {
        super(string);
    }
    
}
