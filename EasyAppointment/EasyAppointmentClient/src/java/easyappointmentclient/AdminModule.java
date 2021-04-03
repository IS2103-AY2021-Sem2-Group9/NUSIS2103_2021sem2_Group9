package easyappointmentclient;

import Enumeration.ServiceProviderStatus;
import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AdminEntity;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import java.util.Scanner;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEntityNotFoundException;

public class AdminModule {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private AdminEntity loggedInAdminEntity;
    
    public AdminModule() 
    {
    }

    public AdminModule(AdminEntitySessionBeanRemote adminEntitySessionBeanRemote, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote, AdminEntity loggedInAdminEntity) {
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.loggedInAdminEntity = loggedInAdminEntity;
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
            System.out.println("9: Logout");
            
            response = 0;
            
            while (response < 1 || response > 9)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if (response == 1) 
                {
                    System.out.println("work in progress...\n");
                } 
                else if (response == 2) 
                {
                    System.out.println("work in progress...\n");
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
                    System.out.println("work in progress...\n");
                }
                else if (response == 9) 
                {
                    break;
                }
                else 
                {
                    System.out.println("Please key in 1 ~ 9 only.");
                }
            }
            
            if (response == 9) 
            {
                System.out.println("Thank you! Logging out...\n");
                break;
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
            System.out.printf("%-18s%-20s%-15s%-18s%-10s\n", sp.getName(), "| " + sp.getCategory(), "| " + sp.getCity(), "| " + "<rating>", "| " + sp.getStatus());
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
            System.out.printf("%-3s%-18s%-20s%-22s%-15s%-22s%-20s%-10s\n", sp.getServiceProviderId().toString(), "| " + sp.getName(), "| " + sp.getCategory(), "| " + sp.getUen() , "| " + sp.getCity(), "| " + sp.getAddress(), "| " + sp.getEmail(), "| " + sp.getPhoneNumber());
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
                System.err.println("Error occured while approving Service Provider: " + ex.getMessage());
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
}
