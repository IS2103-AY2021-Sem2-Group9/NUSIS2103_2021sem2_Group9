package easyappointmentcustomerclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.CustomerEntity;
import ws.client.CustomerNotFoundException_Exception;

public class EasyAppointmentCustomerClient {

    public static void main(String[] args) {
        
        try {
            CustomerEntity customer = retrieveCustomerEntityById(Long.valueOf(1));
            System.out.println(customer.getFirstName() + " " + customer.getLastName() + " " + customer.getEmail());
        } catch (CustomerNotFoundException_Exception ex) {
            ex.printStackTrace();
        }
    }

    private static CustomerEntity retrieveCustomerEntityById(java.lang.Long arg0) throws CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCustomerEntityById(arg0);
    }
    
    
}
