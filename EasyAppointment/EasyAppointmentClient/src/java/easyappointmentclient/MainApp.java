/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyappointmentclient;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import java.util.Scanner;

/**
 *
 * @author marcuslee
 */
public class MainApp {
    
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private ServiceProviderTerminal spTerminal;

    public MainApp() {
    }

    public MainApp(ServiceProviderEntitySessionBeanRemote sericeProviderSessionBeanRemote) {
        this.serviceProviderSessionBeanRemote = sericeProviderSessionBeanRemote;
    }
    
    public void runApp() {
        Scanner scanner = new Scanner(System.in); 
        Integer response = 0; 
        
        while(true) {
            System.out.println("*** Welcome to EasyAppoinment ***"); 
            System.out.println("1: Customer Terminal");
            System.out.println("2: Service Provider Terminal");
            System.out.println("3: Admin Terminal");
            System.out.println("4: Exit");
            while (response < 1 || response > 2) {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if(response == 1) {
                    System.out.println("under development");
                } else if(response == 2) {
                    spTerminal = new ServiceProviderTerminal(serviceProviderSessionBeanRemote);
                    spTerminal.runApp();
                } else if (response == 3) {
                    System.out.println("under development");
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again\n");
                }
            }
            if (response == 4) {
                break;
            }            
        }
    }

            
}
