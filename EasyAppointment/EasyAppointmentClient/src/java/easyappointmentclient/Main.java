package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AdminEntity;
import javax.ejb.EJB;
import util.exception.AdminNotFoundException;

public class Main {
   
    @EJB
    private static ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;   
    @EJB
    private static AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    
    public static void main(String[] args) throws AdminNotFoundException {
        MainApp mainApp = new MainApp(adminEntitySessionBeanRemote, serviceProviderEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
