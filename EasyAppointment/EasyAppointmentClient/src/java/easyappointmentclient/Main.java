package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.AdminNotFoundException;

public class Main {
   
    @EJB
    private static ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;   
    @EJB
    private static AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    @EJB
    private static BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    
    public static void main(String[] args) throws AdminNotFoundException {
        MainApp mainApp = new MainApp(serviceProviderEntitySessionBeanRemote, adminEntitySessionBeanRemote, businessCategorySessionBeanRemote);
        mainApp.runApp();
    }
    
}
