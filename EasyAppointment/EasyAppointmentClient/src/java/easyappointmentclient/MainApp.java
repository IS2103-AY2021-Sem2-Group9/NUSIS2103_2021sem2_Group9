package easyappointmentclient;

import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;

import java.util.Scanner;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

public class MainApp {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    private ServiceProviderTerminal spTerminal;
    private AdminTerminal adminTerminal;
    private CustomerTerminal customerTerminal;
    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;
    
    public MainApp() 
    {
    }

    public MainApp(ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote, AdminEntitySessionBeanRemote adminEntitySessionBeanRemote, CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote, Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) 
    {
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
        this.customerEntitySessionBeanRemote = customerEntitySessionBeanRemote;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in); 
        Integer response = 0; 
        while(true) {
            System.out.println("*** Welcome to EasyAppoinment ***\n"); 
            System.out.println("1: Customer Terminal");
            System.out.println("2: Service Provider Terminal");
            System.out.println("3: Admin Terminal");
            System.out.println("4: Exit");
            response = 0;
          
            while (response < 1 || response > 4) {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if(response == 1) {
                    customerTerminal = new CustomerTerminal(customerEntitySessionBeanRemote, serviceProviderSessionBeanRemote, businessCategorySessionBeanRemote, appointmentEntitySessionBeanRemote);
                    customerTerminal.runApp();
                } else if(response == 2) {
                    spTerminal = new ServiceProviderTerminal(serviceProviderSessionBeanRemote, businessCategorySessionBeanRemote, appointmentEntitySessionBeanRemote);
                    spTerminal.runApp();
                } else if (response == 3) {
                    adminTerminal = new AdminTerminal(adminEntitySessionBeanRemote, businessCategorySessionBeanRemote, customerEntitySessionBeanRemote, serviceProviderSessionBeanRemote, queueCheckoutNotification, queueCheckoutNotificationFactory);
                    adminTerminal.runApp();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again\n");
                }
            }
            if (response == 4) {
                System.out.println("Thank you!\n"); 
                break;
            }            
        }
    }

    
}
