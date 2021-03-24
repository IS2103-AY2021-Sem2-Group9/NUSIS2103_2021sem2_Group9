package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AdminEntity;
import javax.ejb.EJB;
import util.exception.AdminNotFoundException;

public class Main {

   
    @EJB
    private static ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;
    
    
    public static void main(String[] args) {
        
        MainApp mainApp = new MainApp(serviceProviderEntitySessionBeanRemote);
        mainApp.runApp();
        
    }
    
}
