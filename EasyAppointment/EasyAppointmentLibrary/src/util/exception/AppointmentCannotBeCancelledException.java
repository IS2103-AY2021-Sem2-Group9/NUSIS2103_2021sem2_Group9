package util.exception;

public class AppointmentCannotBeCancelledException extends Exception {

    public AppointmentCannotBeCancelledException() {
    }

    public AppointmentCannotBeCancelledException(String string) {
        super(string);
    }
}
