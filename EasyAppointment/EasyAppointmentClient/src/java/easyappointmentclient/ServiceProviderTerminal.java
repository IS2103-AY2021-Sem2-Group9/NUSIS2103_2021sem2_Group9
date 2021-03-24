/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyappointmentclient;

import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.ServiceProviderEntity;
import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author marcuslee
 */
public class ServiceProviderTerminal {
    
    private ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;
    private ServiceProviderModule serviceProviderModule;
    private ServiceProviderEntity currentServiceProviderEntity;

    public ServiceProviderTerminal() {
    }
    
    public ServiceProviderTerminal(ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote) {
        this.serviceProviderEntitySessionBeanRemote = serviceProviderEntitySessionBeanRemote;
    }
    
    public void runApp() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in); 
        Integer response = 0;
        
        while(true) {
            System.out.println("*** Welcome to Service provider terminal ***");
            System.out.println("1: Registration");
            System.out.println("2: Login");
            System.out.println("3: Exit");
                
            while (response < 1 || response > 4) {
                System.out.print("> ");
                response = scanner.nextInt(); 
                if (response == 1) {
                    doRegister();
                } else if (response == 2) {
                   // try {
                        doLogin();
                        System.out.println("Login successful!");
                        serviceProviderModule = new ServiceProviderModule(serviceProviderEntitySessionBeanRemote, currentServiceProviderEntity);
                        serviceProviderModule.menuServiceProvider();
                        break;
                    //} 
                } else if (response == 3) { 
                    break;
                } else {
                    System.out.println("Invalid option, please try again");
                }
            }
            if (response == 3) {
                break; 
            }
        }
    }
    
    
    private void doRegister() {
        Scanner scanner = new Scanner(System.in);
        ServiceProviderEntity spEntity = new ServiceProviderEntity(); 
       
        System.out.println("*** Service Provider Terminal :: Registration Operation ***");
        System.out.print("Enter Name> ");
        spEntity.setName(scanner.nextLine().trim());
        System.out.print("Enter Business Category> ");
        spEntity.setCategory(scanner.nextLine().trim());
        System.out.print("Enter Business Registration Number>");
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
  
    }
    
    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        Integer password; 
        
        System.out.println("*** Service provider terminal :: Login ***");
        System.out.print("Enter Email Address> ");
        email = scanner.nextLine().trim(); 
        System.out.print("Enter password> "); 
        password = scanner.nextInt(); 
        
        if(email.length() > 0 && password.toString().length() > 0) {
            currentServiceProviderEntity = serviceProviderEntitySessionBeanRemote.serviceProviderLogin(email, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential");
        }
            
    }
}
