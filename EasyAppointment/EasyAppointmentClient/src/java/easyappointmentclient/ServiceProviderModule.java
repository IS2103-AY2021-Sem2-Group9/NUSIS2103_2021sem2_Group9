package easyappointmentclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AppointmentEntity;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import java.util.Scanner;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UpdateServiceProviderException;
import java.util.List;
import util.exception.AppointmentNotFoundException;

public class ServiceProviderModule {
    
    private ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote;
    private ServiceProviderEntity currentServiceProviderEntity; 
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote; 
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;

    public ServiceProviderModule(ServiceProviderEntitySessionBeanRemote serviceProviderEntitySessionBeanRemote, ServiceProviderEntity currentServiceProviderEntity, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote) {
        this.serviceProviderEntitySessionBeanRemote = serviceProviderEntitySessionBeanRemote;
        this.currentServiceProviderEntity = currentServiceProviderEntity;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
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
                response = sc.nextInt();
                if (response == 1) {
                   doViewProfile();
                   System.out.println("Enter 0 to go back to the previous menu");
                   System.out.print("> ");
                   Integer viewResponse = 0; 
                   viewResponse = sc.nextInt();
                   if (viewResponse == 0) {
                       break;
                   } else {
                       continue;
                   }
                } else if (response == 2) {
                    doEditProfile(currentServiceProviderEntity);
                    System.out.println("Profile successfully updated!");
                } else if (response == 3) {
                    System.out.println("*** Service provider terminal :: View Appointments ***\n");
                    doViewAllAppointments(currentServiceProviderEntity);
                    System.out.println("Enter 0 to go back to the previous menu");
                    System.out.print("> ");
                    Integer viewResponse = 0; 
                    if (viewResponse == 0) {
                        break;
                    }
                } else if (response == 4) {
                    doCancelAppointment(currentServiceProviderEntity);
                } else if(response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please key in 1 ~ 5 only.");
                }
            }
            if (response == 5) {
                break;
            }
        }
        
    }
    
    public void doViewProfile() { 
        System.out.println("*** Service Provider Terminal :: View Profile ***");
        
        System.out.println("Name: " + currentServiceProviderEntity.getName());
        System.out.println("Category: " + currentServiceProviderEntity.getCategory().getCategoryName());
        System.out.println("Business Registration Number: " + currentServiceProviderEntity.getUen());
        System.out.println("City: " + currentServiceProviderEntity.getCity());
        System.out.println("Phone: " + currentServiceProviderEntity.getPhoneNumber());
        System.out.println("Business Address: " + currentServiceProviderEntity.getAddress());
        System.out.println("Email: " + currentServiceProviderEntity.getEmail() + "\n");  
    }
    
    
   public void doEditProfile(ServiceProviderEntity currentServiceProviderEntity) {
       System.out.println("*** Service Provider Terminal :: Edit Profile ***"); 
       
       Scanner scanner = new Scanner(System.in); 
       String input; 
       
        System.out.print("Enter Name (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setName(input);
        }
        
        System.out.printf("%11s%16s\n", "Category ID", "Category Name");

        List<BusinessCategoryEntity> businessCategories = businessCategorySessionBeanRemote.retrieveAllBusinessCategories();  
        for (BusinessCategoryEntity category : businessCategories)
        {
            System.out.printf("%11s%16s\n", category.getId(), category.getCategoryName());
        }
        System.out.print("Enter Business Category (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            for (BusinessCategoryEntity category : businessCategories) {  
                try {
                    long matchEntry = Long.valueOf(input);
                    if(category.getId() == matchEntry) {
                        currentServiceProviderEntity.setCategory(category);
                        break; 
                    }
                } catch (NumberFormatException ex) {
                System.err.println("Please input a password consisting of numbers only!");
                }            
            }
        }        
        System.out.print("Enter Business Registration Number (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setUen(input);
        }
        
        System.out.print("Enter City (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setCity(input);
        }
        
        System.out.print("Enter Phone (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setPhoneNumber(input);
        }
        
        System.out.print("Enter Business Address (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setAddress(input);
        }
        
        System.out.print("Enter Password (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            try {
                Integer newPassword = Integer.valueOf(input);
                currentServiceProviderEntity.setPassword(newPassword);
            } catch (NumberFormatException ex) {
                System.err.println("Please input a password consisting of numbers only!");
            }
        }
     
        
        try {
            serviceProviderEntitySessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
            System.out.println("Service Provider Profile updated successfully!\n");
        } catch(ServiceProviderEntityNotFoundException | UpdateServiceProviderException ex) {
            System.out.println("An error has occured while updating staff: " + ex.getMessage() + "\n");
        }
   }
    
   public void doViewAllAppointments(ServiceProviderEntity currentServiceProviderEntity) {
       System.out.println("Appointments: ");
       
       System.out.printf("%-22s%-20s%-20s%-15s\n", "Name", " | Date", " | Time", " | Appointment No.");
       List<AppointmentEntity> appointments = appointmentEntitySessionBeanRemote.retrieveAllAppointmentsForServiceProvider(currentServiceProviderEntity);
       for (AppointmentEntity appointment : appointments) {
           System.out.printf("%-22s%-20s%-20s%-15s\n", appointment.getCustomerEntity().getFirstName() + " " + appointment.getCustomerEntity().getLastName(), " | " + appointment.getAppointmentDate().toString(), " | " + appointment.getAppointmentTime().toString(), " | " + appointment.getAppointmentNum());
       }
       System.out.println(); 
       
   } 
   
   public void doCancelAppointment(ServiceProviderEntity currentProviderEntity) {
       Scanner sc = new Scanner(System.in);
       String appointmentNum; 
       System.out.println("*** Service provider terminal :: Cancel Appointment ***\n");
       doViewAllAppointments(currentProviderEntity);
       System.out.print("Enter Appointment ID> ");
       appointmentNum = sc.nextLine().trim(); 
       try {
        appointmentEntitySessionBeanRemote.cancelAppointment(appointmentNum);
        System.out.println("Appointment " + appointmentNum + " has been cancelled successfully.");
       } catch (AppointmentNotFoundException ex) {
           System.out.println("An error has occured cancelling the appointment: " + ex.getMessage());
       }
   }
   
}
