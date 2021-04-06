package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import javax.ejb.EJB;
import util.exception.AdminNotFoundException;

public class Main {

    @EJB
    private static CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;
    @EJB
    private static ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;   
    @EJB
    private static AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    @EJB
    private static BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    @EJB
    private static AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    
    public static void main(String[] args) throws AdminNotFoundException {
        MainApp mainApp = new MainApp(serviceProviderEntitySessionBeanRemote, adminEntitySessionBeanRemote, customerEntitySessionBeanRemote, businessCategorySessionBeanRemote, appointmentEntitySessionBeanRemote);
        mainApp.runApp();
    }
    
}
