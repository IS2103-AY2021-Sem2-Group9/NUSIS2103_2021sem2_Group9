package easyappointmentclient;

import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import ejb.session.stateless.AdminEntitySessionBeanRemote;

import java.util.Scanner;

public class MainApp {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private ServiceProviderTerminal spTerminal;
    private AdminModule adminModule;
    
    public MainApp() 
    {
    }

    public MainApp(ServiceProviderEntitySessionBeanRemote sericeProviderSessionBeanRemote, AdminEntitySessionBeanRemote adminEntitySessionBeanRemote) {
        this.serviceProviderSessionBeanRemote = sericeProviderSessionBeanRemote;
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
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
                    System.out.println("under development");
                } else if(response == 2) {
                    spTerminal = new ServiceProviderTerminal(serviceProviderSessionBeanRemote);
                    spTerminal.runApp();
                } else if (response == 3) {
                    adminModule = new AdminModule(adminEntitySessionBeanRemote);
                    adminModule.adminTerminal();
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
