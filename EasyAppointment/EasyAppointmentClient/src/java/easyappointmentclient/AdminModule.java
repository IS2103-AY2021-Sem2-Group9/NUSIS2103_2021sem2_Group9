package easyappointmentclient;

import Enumeration.ServiceProviderStatus;
import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AdminEntity;
import entity.AppointmentEntity;
import entity.BusinessCategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.NamingException;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.DeleteCustomerException;
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEntityNotFoundException;

public class AdminModule {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private AdminEntity loggedInAdminEntity;
    private Queue queueCheckoutNotification;
    private ConnectionFactory queueCheckoutNotificationFactory;
    
    
    
    public AdminModule() 
    {
    }

    public AdminModule(AdminEntitySessionBeanRemote adminEntitySessionBeanRemote, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote, ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote, AdminEntity loggedInAdminEntity, Queue queueCheckoutNotification, ConnectionFactory queueCheckoutNotificationFactory) {
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.customerEntitySessionBeanRemote = customerEntitySessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.loggedInAdminEntity = loggedInAdminEntity;
        this.queueCheckoutNotification = queueCheckoutNotification;
        this.queueCheckoutNotificationFactory = queueCheckoutNotificationFactory;
    }
    
    public void mainMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Admin Terminal :: Main ***\n");
            System.out.println("You are now logged in as " + loggedInAdminEntity.getFirstName() + " " + loggedInAdminEntity.getLastName() + "\n");
            System.out.println("1: View Appointments for Customers");
            System.out.println("2: View Appointments for Service Providers");
            System.out.println("3: View Service Providers");
            System.out.println("4: Approve Service Provider");
            System.out.println("5: Block Service Provider");
            System.out.println("6: Add Business category");
            System.out.println("7: Remove Business category");
            System.out.println("8: Send reminder email");
            System.out.println("9: Delete customer");
            System.out.println("10: Logout");
            
            response = 0;
            
            while (response < 1 || response > 9)
            {
                System.out.print("> ");
                try
                {
                    response = scanner.nextInt();
                }
                catch (InputMismatchException ex)
                {
                    System.err.println("Please only input digits!");
                    scanner.next();
                }
                
                if (response == 1) 
                {
                    viewCustomerAppointments();
                } 
                else if (response == 2) 
                {
                    viewServiceProviderAppointments();
                }
                else if (response == 3) 
                {
                    viewServiceProviders();
                }
                else if (response == 4) 
                {
                    approveServiceProvider();
                }
                else if (response == 5) 
                {
                    blockServiceProvider();
                }
                else if (response == 6) 
                {
                    createBusinessCategory();
                }
                else if (response == 7) 
                {
                    removeBusinessCategories();
                }
                else if (response == 8) 
                {
                    sendReminderEmail();
                }
                else if (response == 9) 
                {
                    deleteCustomer();
                }
                else if (response == 10) 
                {
                    break;
                }
                else 
                {
                    System.out.println("Please key in 1 ~ 10 only.");
                }
            }
            
            if (response == 10) 
            {
                System.out.println("Thank you! Logging out...\n");
                break;
            }
        }
    }
    
    private void viewCustomerAppointments() 
    {
        System.out.println("*** Admin Terminal :: View Appointments for customers ***\n");
        Scanner scanner = new Scanner(System.in);
        Long customerId;
         
        while (true)
        {
            System.out.println("Enter 0 to go back to the previous menu.\n");
            System.out.print("Enter customer ID> ");
            try
            {
                customerId = scanner.nextLong();
                System.out.println();
                
                if (customerId == 0)
                {
                    System.out.println("Heading back to main menu...\n");
                    break;
                }
                else 
                {
                    CustomerEntity customerEntity = customerEntitySessionBeanRemote.retrieveCustomerEntityById(customerId);
                    List<AppointmentEntity> customerAppointments = customerEntitySessionBeanRemote.retrieveCustomerEntityAppointments(customerId);
                    
                    if (!customerAppointments.isEmpty())
                    {
                        System.out.println(customerEntity.getFirstName() + "'s Appointments: \n");

                        System.out.printf("%-15s%-20s%-15s%-10s%-18s\n", "Name", "| Business Category", "| Date", "| Time", "| Appointment No.");

                        for (AppointmentEntity appointment : customerAppointments)
                        {
                            ServiceProviderEntity apptServiceProvider = appointment.getServiceProviderEntity();
                            System.out.printf("%-15s%-20s%-15s%-10s%-18s\n", apptServiceProvider.getName(), "| " + apptServiceProvider.getCategory().getCategoryName(), "| " + appointment.getAppointmentDate().toString(), "| " + appointment.getAppointmentTime().toString(), "| " + appointment.getAppointmentNum());
                        }
                    }
                    else
                    {
                        System.out.println(customerEntity.getFirstName() + " does not have any appointments!\n");
                    }
                }
                
            }
            catch(InputMismatchException ex)
            {
                System.err.println("Please input digits only.");
                scanner.next();
            }
            catch(CustomerNotFoundException ex)
            {
                System.err.println("Error while retrieving customer's appointments: " + ex.getMessage());
            }
        }
    }
    
    private void viewServiceProviderAppointments()
    {
        System.out.println("*** Admin Terminal :: View Appointments for Service Providers ***\n");
        Scanner scanner = new Scanner(System.in);
        Long serviceProviderId;
        
        while (true)
        {
            System.out.println("Enter 0 to go back to the previous menu.\n");
            System.out.print("Enter Service Provider ID> ");
            
            try
            {
                serviceProviderId = scanner.nextLong();
                
                if (serviceProviderId == 0)
                {
                    break;
                }
                else 
                {
                    List<AppointmentEntity> appointments = serviceProviderSessionBeanRemote.retrieveAppointmentsOfServiceProviderById(serviceProviderId);
                    String serviceProviderName = serviceProviderSessionBeanRemote.retrieveServiceProviderByServiceProviderId(serviceProviderId).getName();
                    
                    if (!appointments.isEmpty())
                    {
                        System.out.println("Viewing Appointments for " + serviceProviderName + ":\n");
                        System.out.printf("%-18s%-18s%-15s%-15s%-10s%-10s\n", "Appointment No.", "| Customer Name", "| Phone Number", "| Date", "| Time", "| Status", "| Rating");

                        for(AppointmentEntity appointment : appointments)
                        {
                            CustomerEntity customer = appointment.getCustomerEntity();
                            System.out.printf("%-18s%-18s%-15s%-15s%-10s%-10s\n", appointment.getAppointmentNum(), "| " + customer.getFirstName() + " " + customer.getLastName(), "| " + customer.getPhoneNumber(), "| " + appointment.getAppointmentDate(), "| " + appointment.getAppointmentTime(), "| " + "<status>", "| " + appointment.getRating());
                        }
                    }
                    else
                    {
                        System.out.println(serviceProviderName + " does not have any appointments!");
                    }
                }
            }
            catch(InputMismatchException ex)
            {
                System.err.println("Please input a digit.\n");
                scanner.next();
            }
            catch(ServiceProviderEntityNotFoundException ex)
            {
                System.err.println("Error while retrieving appointments: " + ex.getMessage());
            }
        }
                
    }
    
    private void viewServiceProviders()
    {
        System.out.println("*** Admin Terminal :: View Service Providers ***\n");
        Scanner scanner = new Scanner(System.in);
        List<ServiceProviderEntity> serviceProviders = serviceProviderSessionBeanRemote.retrieveAllServiceProviders();
        
        System.out.printf("%-18s%-20s%-15s%-18s%-10s\n", "Name", "| Business Category", "| City", "| Overall Rating", "| Status");
        
        for (ServiceProviderEntity sp : serviceProviders)
        {
            System.out.printf("%-18s%-20s%-15s%-18s%-10s\n", sp.getName(), "| " + sp.getCategory().getCategoryName(), "| " + sp.getCity(), "| " + "<rating>", "| " + sp.getStatus());
        }
        
        System.out.println();
    }
    
    private void approveServiceProvider()
    {
        System.out.println("*** Admin Terminal :: Approve Service Provider ***\n");
        Scanner scanner = new Scanner(System.in);
        System.out.println("List of service providers with pending approval: \n");
        List<ServiceProviderEntity> serviceProviders = serviceProviderSessionBeanRemote.retrieveServiceProvidersByStatus(ServiceProviderStatus.PENDING);
        
        System.out.printf("%-3s%-18s%-20s%-22s%-15s%-22s%-20s%-10s\n", "ID", "| Name", "| Business Category", "| Business Reg. Num", "| City", "| Address", "| Email", "| Phone");
        
        for (ServiceProviderEntity sp : serviceProviders)
        {
            System.out.printf("%-3s%-18s%-20s%-22s%-15s%-22s%-20s%-10s\n", sp.getServiceProviderId().toString(), "| " + sp.getName(), "| " + sp.getCategory().getCategoryName(), "| " + sp.getUen() , "| " + sp.getCity(), "| " + sp.getAddress(), "| " + sp.getEmail(), "| " + sp.getPhoneNumber());
        }
        
        System.out.println();
        
        while (true)
        {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("Enter Service Provider ID> ");
            
            try
            {
                Long id = scanner.nextLong();
                scanner.nextLine();
                if (id == 0)
                {
                   break;
                }
                String approvedSp = serviceProviderSessionBeanRemote.approveServiceProviderById(id);
                System.out.println(approvedSp + "'s registration is approved.\n");
                break;
            }
            catch (ServiceProviderEntityNotFoundException | ServiceProviderAlreadyApprovedException ex)
            {
                System.err.println("Error occurred while approving Service Provider: " + ex.getMessage());
            }
            catch (InputMismatchException ex)
            {
                System.err.println("Please only enter digits!");
                scanner.next();
            }
        }
    }
    
        private void blockServiceProvider()
    {
        System.out.println("*** Admin Terminal :: Block Service Provider ***\n");
        Scanner scanner = new Scanner(System.in);
        System.out.println("List of service providers: \n");
        List<ServiceProviderEntity> approvedServiceProviders = serviceProviderSessionBeanRemote.retrieveServiceProvidersByStatus(ServiceProviderStatus.APPROVED);
        List<ServiceProviderEntity> pendingServiceProviders = serviceProviderSessionBeanRemote.retrieveServiceProvidersByStatus(ServiceProviderStatus.PENDING);
        List<ServiceProviderEntity> totalServiceProviders = new ArrayList<>();
        totalServiceProviders.addAll(approvedServiceProviders);
        totalServiceProviders.addAll(pendingServiceProviders);
        
        System.out.printf("%-3s%-18s%-20s%-22s%-15s%-22s%-20s%-10s%-10s\n", "ID", "| Name", "| Business Category", "| Business Reg. Num", "| City", "| Address", "| Email", "| Phone", "| Status");
        
        for (ServiceProviderEntity sp : totalServiceProviders)
        {
            System.out.printf("%-3s%-18s%-20s%-22s%-15s%-22s%-20s%-10s%-10s\n", sp.getServiceProviderId().toString(), "| " + sp.getName(), "| " + sp.getCategory(), "| " + sp.getUen() , "| " + sp.getCity(), "| " + sp.getAddress(), "| " + sp.getEmail(), "| " + sp.getPhoneNumber(), "| " + sp.getStatus());
        }
        
        System.out.println();
        
        while (true)
        {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("Enter Service Provider ID> ");

            try
            {
                Long id = scanner.nextLong();
                scanner.nextLine();
                if (id == 0)
                {
                   break;
                }
                String blockedSp = serviceProviderSessionBeanRemote.blockServiceProviderById(id);
                System.out.println("Service Provider: " + blockedSp + " has been blocked.\n");
                break;
            }
            catch (ServiceProviderEntityNotFoundException | ServiceProviderAlreadyBlockedException ex)
            {
                System.err.println("Error occured while blocking Service Provider: " + ex.getMessage());
            }
            catch (InputMismatchException ex)
            {
                System.err.println("Please only enter digits!");
                scanner.next();
            }
        }
    }
    
    private void createBusinessCategory() 
    {
        System.out.println("*** Admin Terminal :: Add a Business Category ***\n");
        Scanner scanner = new Scanner(System.in);
        
        BusinessCategoryEntity newBusinessCategory = new BusinessCategoryEntity();
        String category = "";
        Integer zero = 1;
        
        while (true)
        {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("Enter a new business category> ");
            category = scanner.nextLine();
            
            if (category.length() > 1)
            {
                newBusinessCategory.setCategoryName(category);

                String addedCategory = businessCategorySessionBeanRemote.createBusinessCategoryEntity(newBusinessCategory);
                System.out.println("The business category " + addedCategory + " is added.\n");
                break;
            }
            else if (category.length() == 1)
            {
                try
                {
                    zero = Integer.valueOf(category);
                }
                catch (NumberFormatException ex)
                {
                    newBusinessCategory.setCategoryName(category);

                    String addedCategory = businessCategorySessionBeanRemote.createBusinessCategoryEntity(newBusinessCategory);
                    System.out.println("The business category " + addedCategory + " is added.\n");
                    break;
                }
                
                if (zero == 0) 
                {
                    break;
                } 
                else 
                {
                    System.out.println("Please enter 0 if you would like to go back to the previous menu.");
                    System.out.println("Single digits are not allowed to be category names.");
                }
            }
            else
            {
                continue;
            }
        }
    }
    
    private void removeBusinessCategories()
    {
        System.out.println("*** Admin Terminal :: Remove a Business Category ***\n");
        Scanner scanner = new Scanner(System.in);
        List<BusinessCategoryEntity> businessCategories = businessCategorySessionBeanRemote.retrieveAllBusinessCategories();
        
        System.out.printf("%11s%16s\n", "Category ID", "Category Name");
        
        for (BusinessCategoryEntity category : businessCategories)
        {
            System.out.printf("%11s%16s\n", category.getId(), category.getCategoryName());
        }
        
        System.out.println();
        
        Integer zero = 1;
        
        while (true)
        {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("Enter the name of the category you want to remove> ");

            String toBeRemoved = scanner.nextLine().trim();

            try
            {
                if (toBeRemoved.length() > 1)
                {
                    String removedName = businessCategorySessionBeanRemote.deleteBusinessCategory(toBeRemoved);
                    System.out.println("Business Category " + removedName + " has been removed.\n");
                    break;
                }
                else if (toBeRemoved.length() == 1)
                {
                    try
                    {
                        zero = Integer.valueOf(toBeRemoved);
                    }
                    catch (NumberFormatException ex)
                    {
                        String removedName = businessCategorySessionBeanRemote.deleteBusinessCategory(toBeRemoved);
                        System.out.println("Business Category " + removedName + " has been removed.\n");
                        break;
                    }
                
                    if (zero == 0) 
                    {
                        break;
                    } 
                    else 
                    {
                        System.out.println("Please enter 0 if you would like to go back to the previous menu.");
                        System.out.println("Single digits are not allowed to be category names.");
                    }
                }
                else
                {
                    continue;
                }
            }
            catch (BusinessCategoryNotFoundException ex)
            {
                System.out.println("Error removing Business Category: " + ex.getMessage() + "\n");
            }
        }
    }
    
    private void sendReminderEmail()
    {
        System.out.println("*** Admin Terminal :: Send Reminder Email ***\n");
        
        Scanner sc = new Scanner(System.in);
        Long customerId;
        
        try
        {
            System.out.print("Enter customer ID> ");
            customerId = sc.nextLong();
            sc.nextLine();
            CustomerEntity customerEntity = customerEntitySessionBeanRemote.retrieveCustomerEntityById(customerId);
            List<AppointmentEntity> customerAppointments = customerEntitySessionBeanRemote.retrieveCustomerEntityAppointments(customerId);
                    
            if (!customerAppointments.isEmpty())
            {
                System.out.println(customerEntity.getFirstName() + "'s upcoming appointment: \n");
                System.out.printf("%-15s%-20s%-15s%-10s%-18s\n", "Name", "| Business Category", "| Date", "| Time", "| Appointment No.");
                
                AppointmentEntity appointment = customerAppointments.get(0);
                ServiceProviderEntity apptServiceProvider = appointment.getServiceProviderEntity();
                System.out.printf("%-15s%-20s%-15s%-10s%-18s\n", apptServiceProvider.getName(), "| " + apptServiceProvider.getCategory().getCategoryName(), "| " + appointment.getAppointmentDate().toString(), "| " + appointment.getAppointmentTime().toString(), "| " + appointment.getAppointmentNum());
                sendJMSMessageToQueueAppointmentNotification(customerEntity.getId(), "EasyAppointment_Group9");
                System.out.println("Email sent successfully!\n");
            }
            else
            {
                System.out.println("Unable to send email as " + customerEntity.getFirstName() + " does not have any upcoming appointments!\n");
            }
        }
        catch(InputMismatchException ex)
        {
            System.err.println("Please enter digits only.\n");
            sc.next();
        }
        catch (Exception ex)
        {
            System.err.println("An error occured while trying to send email: " + ex.getMessage());
        }
    }
    
    private void deleteCustomer()
    {
        Scanner scanner = new Scanner(System.in);        
        Long id;
        String response;
        CustomerEntity deletingCustomer;
        
        System.out.println("*** Admin Terminal :: Delete Customer's Account ***\n");
        
        while (true)
        {
            System.out.println("Enter 0 to go back.");
            System.out.printf("Enter Customer ID to delete> ");
            response = "";

            try 
            {
                id = scanner.nextLong();
                scanner.nextLine();
                if (id == 0)
                {
                    break;
                }
                deletingCustomer = customerEntitySessionBeanRemote.retrieveCustomerEntityById(id);
                System.out.printf("Confirm deletion of " + deletingCustomer.getFirstName() + " " + deletingCustomer.getLastName() + " (Enter 'Y' to delete)> ");
                response = scanner.nextLine().trim().toUpperCase();
                if (response.equals("Y"))
                {
                    customerEntitySessionBeanRemote.deleteCustomerEntity(id);
                    System.out.println("Customer " + deletingCustomer.getFirstName() + " deleted successfully!\n");
                }
                else
                {
                    System.out.println("Customer " + deletingCustomer.getFirstName() + " not deleted!\n");
                }
            }
            catch (CustomerNotFoundException | DeleteCustomerException ex)
            {
                System.err.println("Error while deleting customer: " + ex.getMessage() + "\n");
            }
            catch (InputMismatchException ex)
            {
                System.err.println("Please enter digits only for Customer ID!");
                scanner.next();
            }
            
        }
    }

    private void sendJMSMessageToQueueAppointmentNotification(Long customerEntityId, String fromEmailAddress) throws JMSException, NamingException 
    {
        Connection conn = null;
        Session s = null;
        try {
            conn = queueCheckoutNotificationFactory.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            MessageProducer mp = s.createProducer(queueCheckoutNotification);
            
            MapMessage mapMessage = s.createMapMessage();
            mapMessage.setString("fromEmailAddress", fromEmailAddress);            
            mapMessage.setLong("customerEntityId", customerEntityId);
            
            mp.send(mapMessage);
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }


}
