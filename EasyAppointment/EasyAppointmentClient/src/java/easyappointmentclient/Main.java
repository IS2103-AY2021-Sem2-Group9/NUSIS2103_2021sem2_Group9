package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
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

    @Resource(mappedName = "jms/queueAppointmentNotification")
    private static Queue queueCheckoutNotification;
    @Resource(mappedName = "jms/queueAppointmentNotificationFactory")
    private static ConnectionFactory queueCheckoutNotificationFactory;
    
    public static void main(String[] args) throws AdminNotFoundException {
        MainApp mainApp = new MainApp(serviceProviderEntitySessionBeanRemote, adminEntitySessionBeanRemote, customerEntitySessionBeanRemote, businessCategorySessionBeanRemote, appointmentEntitySessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory);
        mainApp.runApp();
    }
    
}
