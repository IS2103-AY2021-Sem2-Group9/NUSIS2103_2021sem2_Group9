package easyappointmentcustomerclient;

import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.CustomerEntity;
import ws.client.CustomerNotFoundException_Exception;

public class EasyAppointmentCustomerClient {

    public static void main(String[] args) {
        try {
            CustomerEntity ce = retrieveCustomerEntityById(Long.valueOf(1));
            System.out.println(ce.getEmail());
        } catch (CustomerNotFoundException_Exception ex) {
            Logger.getLogger(EasyAppointmentCustomerClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        CustomerTerminal customerTerminal = new CustomerTerminal();
        customerTerminal.runApp();
    }    

    private static CustomerEntity retrieveCustomerEntityById(java.lang.Long customerId) throws CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCustomerEntityById(customerId);
    }
    
    

}
