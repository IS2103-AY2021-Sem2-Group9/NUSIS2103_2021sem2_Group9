package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import entity.AdminEntity;

import java.util.Scanner;
import util.exception.InvalidLoginCredentialException;

public class AdminModule {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    
    private AdminEntity loggedInAdminEntity;
    
    public AdminModule() 
    {
    }

    public AdminModule(AdminEntitySessionBeanRemote adminEntitySessionBeanRemote) {
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
    }
    
    public void adminTerminal() 
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
                response = scanner.nextInt();
                
                if (response == 1) 
                {
                    try
                    {
                        doLogin();
                        System.out.println("Login successful!");
                        System.out.println("Logged in as " + loggedInAdminEntity.getFirstName());
                    }
                    catch (InvalidLoginCredentialException ex)
                    {
                        System.out.println("An error has occurred while logging in: " + ex.getMessage() + "\n");
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
