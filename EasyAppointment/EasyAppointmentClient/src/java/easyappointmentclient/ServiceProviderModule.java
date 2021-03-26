/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package easyappointmentclient;

import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.ServiceProviderEntity;
import java.util.Scanner;

/**
 *
 * @author marcuslee
 */
public class ServiceProviderModule {
    
    private ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;
    private ServiceProviderEntity currentServiceProviderEntity; 

    public ServiceProviderModule(ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote, ServiceProviderEntity currentServiceProviderEntity) {
        this.serviceProviderEntitySessionBeanRemote = serviceProviderEntitySessionBeanRemote;
        this.currentServiceProviderEntity = currentServiceProviderEntity;
    }
    
    public void menuServiceProvider() {
        Scanner sc = new Scanner(System.in); 
        Integer response = 0; 
        
        while (true) {
            System.out.println("*** Service provider terminal :: Main ***\n");

            System.out.println("You are login as " + currentServiceProviderEntity.getName());
            System.out.println("1: View Profile");
            System.out.println("2: Edit Profile");
            System.out.println("3: View Appointments");
            System.out.println("4: Cancel Appointment");
            System.out.println("5: Logout");
            response = 0; 
            
            while (response < 1 || response > 6) {
                System.out.print("> ");
                if (response == 1) {
                    
                } else if (response == 2) {
                    
                } else if (response == 3) {
                    
                } else if (response == 4) {
                    
                } else if(response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again");
                }
            }
            if (response == 5) {
                break;
            }
        }
        
    }
    
}
