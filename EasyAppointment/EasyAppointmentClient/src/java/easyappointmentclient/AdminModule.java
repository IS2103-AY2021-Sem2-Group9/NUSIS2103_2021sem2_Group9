package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import entity.AdminEntity;
import entity.BusinessCategoryEntity;

import java.util.Scanner;

public class AdminModule {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private AdminEntity loggedInAdminEntity;
    
    public AdminModule() 
    {
    }

    public AdminModule(AdminEntitySessionBeanRemote adminEntitySessionBeanRemote, AdminEntity loggedInAdminEntity) {
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
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
                    System.out.println("work in progress...\n");
                }
                else if (response == 4) 
                {
                    System.out.println("work in progress...\n");
                }
                else if (response == 5) 
                {
                    System.out.println("work in progress...\n");
                }
                else if (response == 6) 
                {
                    createBusinessCategory();
                }
                else if (response == 7) 
                {
                    System.out.println("work in progress...\n");
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
    
    private void createBusinessCategory() 
    {
        System.out.println("*** Admin Terminal :: Add a Business Category ***\n");
        Scanner scanner = new Scanner(System.in);
        
        BusinessCategoryEntity newBusinessCategory = new BusinessCategoryEntity();
        String category = "";
        System.out.print("Enter a new business category> ");
        category = scanner.nextLine();
        newBusinessCategory.setCategoryName(category);
        
        String addedCategory = businessCategorySessionBeanRemote.createBusinessCategoryEntity(newBusinessCategory);
        System.out.println("The business category " + addedCategory + " is added.");
    }
}
