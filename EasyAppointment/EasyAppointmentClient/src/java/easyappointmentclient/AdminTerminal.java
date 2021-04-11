package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AdminEntity;
import java.util.InputMismatchException;

import java.util.Scanner;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import util.exception.InvalidLoginCredentialException;

public class AdminTerminal {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;
    
    private AdminEntity loggedInAdminEntity;
    private AdminModule adminModule;
    
    public AdminTerminal() 
    {
    }

    public AdminTerminal(AdminEntitySessionBeanRemote adminEntitySessionBeanRemote, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote, ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote, Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) {
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.customerEntitySessionBeanRemote = customerEntitySessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }
    
    public void runApp() 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Welcome to the Admin Terminal ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit");
            response = 0;
            
            while (response < 1 || response > 2)
            {
                System.out.print("> ");
                try {
                    response = scanner.nextInt();
                } catch (InputMismatchException ex) {
                    System.err.println("Please input digits only.");
                    scanner.next();
                }
                
                if (response == 1) 
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        adminModule = new AdminModule(adminEntitySessionBeanRemote, businessCategorySessionBeanRemote, customerEntitySessionBeanRemote, serviceProviderSessionBeanRemote, loggedInAdminEntity, queueCheckoutNotification, queueCheckoutNotificationFactory);
                        adminModule.mainMenu();
                    }
                    catch (InvalidLoginCredentialException ex)
                    {
                        System.err.println("An error has occurred while logging in: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Please key in 1 or 2 only.");
                }
            }
            
            if (response == 2) 
            {
                System.out.println("Thank you!\n");
                break;
            }
        }   
    }
    
    public void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** Admin Terminal :: Login ***\n");
        System.out.print("Enter Email Address> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        password = scanner.nextLine().trim();
        System.out.println();
        
        if(email.length() > 0 && password.length() > 0)
        {
            loggedInAdminEntity = adminEntitySessionBeanRemote.adminLogin(email, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credentials!");
        }
    }
    
}
