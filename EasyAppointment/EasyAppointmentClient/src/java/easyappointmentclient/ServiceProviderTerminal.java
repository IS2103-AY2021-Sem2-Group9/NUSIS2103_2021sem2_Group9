package easyappointmentclient;

import Enumeration.ServiceProviderStatus;
import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.UnknownPersistenceException;

public class ServiceProviderTerminal {
    
    private ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;
    private ServiceProviderModule serviceProviderModule;
    private ServiceProviderEntity currentServiceProviderEntity;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;
    
    public ServiceProviderTerminal() {
    }
    
    public ServiceProviderTerminal(ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote) {
        this.serviceProviderEntitySessionBeanRemote = serviceProviderEntitySessionBeanRemote;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
    }
    public void runApp() {
        Scanner scanner = new Scanner(System.in); 
        Integer response = 0;
        
        while(true) {
            System.out.println("*** Welcome to the Service Provider Terminal ***\n");
            System.out.println("1: Registration");
            System.out.println("2: Login");
            System.out.println("3: Exit");
            response = 0;
                
            while (response < 1 || response > 3) {
                System.out.print("> ");
                try {
                    response = scanner.nextInt();
                } catch (InputMismatchException ex) {
                    System.err.println("Please input digits only.");
                    scanner.next();
                }  
                if (response == 1) 
                {
                    doRegister();
                } 
                else if (response == 2) 
                {
                    try 
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        
                        serviceProviderModule = new ServiceProviderModule(serviceProviderEntitySessionBeanRemote, currentServiceProviderEntity, businessCategorySessionBeanRemote, appointmentEntitySessionBeanRemote);
                        serviceProviderModule.menuServiceProvider();
                    }
                    catch (InvalidLoginCredentialException ex)
                    {
                        System.out.println("An error occured while logging in: " + ex.getMessage());
                    }
                } else if (response == 3) 
                { 
                    break;
                } else 
                {
                    System.out.println("Invalid option, please key in options 1~3 only.");
                }
            }
            if (response == 3) 
            {
                break; 
            }
        }
    }
    
    
    private void doRegister() 
    {
        Scanner scanner = new Scanner(System.in);
        ServiceProviderEntity spEntity = new ServiceProviderEntity(); 
        String name = ""; 
        Long category; 
        String uen = ""; 
        String city = ""; 
        String phone = "";
        String address = "";
        String email = "";
        String password = "";
       
        System.out.println("*** Service Provider Terminal :: Registration Operation ***\n");
        System.out.print("Enter Name> ");
        spEntity.setName(scanner.nextLine().trim());
        List<BusinessCategoryEntity> businessCategories = businessCategorySessionBeanRemote.retrieveAllBusinessCategories(); 
        System.out.printf("%11s%16s\n", "Category ID", "Category Name");
        
        for (BusinessCategoryEntity categoryEntity : businessCategories)
        {
            System.out.printf("%11s%16s\n", categoryEntity.getId(), categoryEntity.getCategoryName());
        }
        System.out.print("Enter Business Category> ");
        category = scanner.nextLong(); 
        scanner.nextLine();
        System.out.print("Enter Business Registration Number> ");
        spEntity.setUen(scanner.nextLine().trim());
        System.out.print("Enter City> ");
        spEntity.setCity(scanner.nextLine().trim());
        System.out.print("Enter Phone> ");
        spEntity.setPhoneNumber(scanner.nextLine().trim());
        System.out.print("Enter Business Address> ");
        spEntity.setAddress(scanner.nextLine().trim());
        System.out.print("Enter Email> ");
        spEntity.setEmail(scanner.nextLine().trim());
        System.out.print("Enter Password> ");
        spEntity.setPassword(scanner.nextInt());
        spEntity.setStatus(ServiceProviderStatus.PENDING);
        
        try {
            spEntity = serviceProviderEntitySessionBeanRemote.registerNewServiceProvider(spEntity, category);
            System.out.println("You have registered Service Provider: " + spEntity.getName() + " successfully!\n"); 
        } catch(ServiceProviderEmailExistException | BusinessCategoryNotFoundException | UnknownPersistenceException ex ) {
            System.out.println("Error registering! " + ex.getMessage());
        }
        
        
        while(true) {
            Integer response; 
            System.out.println("Enter 0 to go back to the previous menu"); 
            System.out.print("> ");
            response = scanner.nextInt();
            if(response == 0) {
                System.out.println("Heading back to Service Provider Terminal...");
                break;
            }  
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException 
    {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        String password = ""; 
        
        System.out.println("*** Service provider terminal :: Login ***\n");
        System.out.print("Enter Email Address> ");
        email = scanner.nextLine().trim(); 
        System.out.print("Enter password> "); 
        password = scanner.nextLine().trim(); 
        
        if(email.length() > 0 && password.length() > 0) {
            Integer pw = Integer.parseInt(password);
            currentServiceProviderEntity = serviceProviderEntitySessionBeanRemote.serviceProviderLogin(email, pw);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential");
        }
            
    }
}
