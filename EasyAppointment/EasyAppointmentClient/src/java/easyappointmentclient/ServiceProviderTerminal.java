package easyappointmentclient;

import Enumeration.ServiceProviderStatus;
import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
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
            System.out.flush();
            System.err.flush();
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
                    catch (InvalidLoginCredentialException | InvalidPasswordFormatException ex)
                    {
                        System.err.println("An error occured while logging in: " + ex.getMessage());
                        continue;
                    }
                } else if (response == 3) 
                { 
                    break;
                } else 
                {
                    System.err.println("Invalid option, please key in options 1~3 only.");
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
        while (true) {
            System.out.print("Enter Name> ");
            name = scanner.nextLine().trim();
            if (name.length() > 0 ) {
                spEntity.setName(name);
                break;
            } else {
                System.err.println("Please input a name!");
            }
        }
        List<BusinessCategoryEntity> businessCategories = businessCategorySessionBeanRemote.retrieveAllBusinessCategories(); 
        System.out.printf("%11s%16s\n", "Category ID", "Category Name");
        
        for (BusinessCategoryEntity categoryEntity : businessCategories)
        {
            System.out.printf("%11s%16s\n", categoryEntity.getId(), categoryEntity.getCategoryName());
        }
        while (true) {
            System.out.print("Enter Business Category> ");
            String input = scanner.nextLine().trim();
            if (input.length() > 0) {
                try {
                    category = Long.valueOf(input);
                    if (category <= businessCategories.size()) {
                        break;
                    } else {
                        System.err.println("Please input a valid Business Category ID. ");
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Please input digits only!");
                }
            } else {
                System.err.println("Please input a Business Category ID.");
            }
        }
        
        while (true) {
            System.out.print("Enter Business Registration Number> ");
            uen = scanner.nextLine().trim(); 
            if (uen.length() > 0) {
                if (serviceProviderEntitySessionBeanRemote.checkUen(uen)) {
                    spEntity.setUen(uen);
                    break;
                } else {
                    System.err.println("Business registration number inputed has already been registered, try again with a different business registration number.");
                }
            } else {
                System.err.println("Please input a Business Registration Number");
            }
            
        }
        
        while (true) {
            System.out.print("Enter City> ");
            city = scanner.nextLine().trim();
            if (city.length() > 0) {
                spEntity.setCity(city);
                break;
            } else {
                System.err.println("Please input a City name.");
            }
        }
        
        while (true) {
            System.out.print("Enter Phone> ");
            phone = scanner.nextLine().trim(); 
            try {
                Integer testPhoneNumber = Integer.parseInt(phone);
                if (phone.length() > 0) {
                    if (serviceProviderEntitySessionBeanRemote.checkPhoneNumber(phone)) {
                        spEntity.setPhoneNumber(phone);
                        break;
                    } else {
                        System.err.println("Phone number inputed has already been registered, try again with a different phone number.");
                    }
                } else {
                    System.err.println("Please input a phone number.");
                }      
            } catch (NumberFormatException ex) {
                System.err.println("Please enter a phone number consisting of numbers.");
            }
        }

        while (true) {
            System.out.print("Enter Business Address> ");
            address = scanner.nextLine().trim(); 
            if (address.length() > 0) {
                spEntity.setAddress(address);
                break;
            } else {
                System.err.println("Please input a Business Address.");
            }
        }
        
        while (true) {
            System.out.print("Enter Email> ");
            email = scanner.nextLine().trim(); 
            if (email.length() > 0) {
                if (serviceProviderEntitySessionBeanRemote.checkEmail(email)) {
                    spEntity.setEmail(email);
                    break;
                } else {
                    System.err.println("Email inputed has already been registered, try again with a new Email.");
                }
            } else {
                System.err.println("Please input an Email.");
            }
            
        }
        
        while (true) {
            System.out.print("Enter Password (6 digit)> ");
            password = scanner.nextLine().trim(); 
            if (password.length() > 0) {
                try {
                    Integer intPassword = Integer.valueOf(password);
                    if (password.length() == 6) {
                        spEntity.setPassword(password);
                        break;
                    } else {
                        System.err.println("Please input a 6 digit Password.");
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("Please input a password consisting of 6 digits only.");
                }
            } else {
                System.err.println("Please input a 6 digit Password.");
            }                        
        }
        spEntity.setStatus(ServiceProviderStatus.PENDING);
        
        try {
            spEntity = serviceProviderEntitySessionBeanRemote.registerNewServiceProvider(spEntity, category);
            System.out.println("You have registered Service Provider: " + spEntity.getName() + " successfully!\n"); 
        } catch(ServiceProviderEmailExistException | BusinessCategoryNotFoundException | InvalidPasswordFormatException | UnknownPersistenceException ex ) {
            System.out.println("Error registering! " + ex.getMessage() + " Please try again.");
        }
        
        
        while(true) {
            Integer response = 1; 
            System.out.println("Enter 0 to go back to the previous menu"); 
            System.out.print("> ");
            try {
                response = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.err.println("Please input digits only.");
                scanner.next();
            }
            if (response == 0) {
                System.out.println("Heading back to Service Provider Terminal...");
                break;
            } 
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException, InvalidPasswordFormatException 
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
            if (password.length() != 6)
            {
                throw new InvalidPasswordFormatException("Password should be 6 digits long only.");
            }
            else 
            {
                try
                {
                    Integer pw = Integer.parseInt(password);
                    currentServiceProviderEntity = serviceProviderEntitySessionBeanRemote.serviceProviderLogin(email, pw);
                }
                catch(NumberFormatException ex)
                {
                    throw new InvalidPasswordFormatException("Please enter digits only.");
                }  
            }
        } else {
            throw new InvalidLoginCredentialException("Missing login credential");
        }
            
    }
}
