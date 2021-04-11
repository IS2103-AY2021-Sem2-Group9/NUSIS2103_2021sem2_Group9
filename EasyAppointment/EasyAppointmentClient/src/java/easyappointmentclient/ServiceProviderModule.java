package easyappointmentclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.AppointmentEntity;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import java.util.InputMismatchException;
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
            
            
            while (response < 1 || response > 5) {
                System.out.print("> ");
                
                try {
                response = sc.nextInt();
                } 
                catch (InputMismatchException ex) {
                    System.err.println("Please only input digits 1-5");
                    sc.next();
                }
                if (response == 1) {
                   doViewProfile();
                } else if (response == 2) {
                    doEditProfile(currentServiceProviderEntity);
                } else if (response == 3) {
                    doViewAllAppointments(currentServiceProviderEntity);
                } else if (response == 4) {
                    doCancelAppointment(currentServiceProviderEntity);
                } else if(response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please key in 1 ~ 5 only.");
                }
            }
            if (response == 5) {
                System.out.println("Thank you! Logging out... ");
                break;
            }
        }
        
    }
    
    public void doViewProfile() { 
        Scanner scanner = new Scanner(System.in);
        Integer response = 1;
        
        System.out.println("*** Service Provider Terminal :: View Profile ***");
        
        System.out.println("Name: " + currentServiceProviderEntity.getName());
        System.out.println("Category: " + currentServiceProviderEntity.getCategory().getCategoryName());
        System.out.println("Business Registration Number: " + currentServiceProviderEntity.getUen());
        System.out.println("City: " + currentServiceProviderEntity.getCity());
        System.out.println("Phone: " + currentServiceProviderEntity.getPhoneNumber());
        System.out.println("Business Address: " + currentServiceProviderEntity.getAddress());
        System.out.println("Email: " + currentServiceProviderEntity.getEmail() + "\n"); 
        
        while (true) {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("> ");
            
            try {
                    response = scanner.nextInt();
                }   catch (InputMismatchException ex) {
                    System.err.println("Please input digits only.");
                    scanner.next();
                }
            if (response == 0) {
                System.out.println("Heading back to Service Provider Main Menu...");
                break;
            }
        }
    }
    
    
   public void doEditProfile(ServiceProviderEntity currentServiceProviderEntity) {
       
       System.out.println("*** Service Provider Terminal :: Edit Profile ***"); 
       
       Scanner scanner = new Scanner(System.in); 
       String input; 
       
       
       System.out.print("Enter City (blank if no change)> ");
       input = scanner.nextLine().trim();
       if(input.length() > 0) {
            currentServiceProviderEntity.setCity(input);
        }

        System.out.print("Enter Business Address (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setAddress(input);
        }

        System.out.print("Enter Email Address (blank if no change)> ");
        input = scanner.nextLine().trim(); 
        if (input.length() > 0) {
            currentServiceProviderEntity.setEmail(input);
        }

        System.out.print("Enter Phone (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            currentServiceProviderEntity.setPhoneNumber(input);
        }
        System.out.print("Enter Password (blank if no change)> ");
        input = scanner.nextLine().trim();
        if(input.length() > 0) {
            try {
                Integer newPassword = Integer.valueOf(input);
                currentServiceProviderEntity.setPassword(newPassword);
            } catch (NumberFormatException ex) {
                System.err.println("Please input a password consisting of numbers only!");
                scanner.next();
            }
        }
        System.out.println();

        try {
            serviceProviderEntitySessionBeanRemote.updateServiceProvider(currentServiceProviderEntity);
            System.out.println("Service Provider Profile updated successfully!\n");
        } catch(ServiceProviderEntityNotFoundException | UpdateServiceProviderException ex) {
            System.out.println("An error has occured while updating staff: " + ex.getMessage() + "\n");
        }

        while(true) {
            Integer response = 1; 
            System.out.println("Enter 0 to go back to the previous menu"); 
            System.out.print("> ");
            try {
                    response = scanner.nextInt();
                }   catch (InputMismatchException ex) {
                    System.err.println("Please input digits only.");
                    scanner.next();
                }
            if(response == 0) {
                System.out.println("Heading back to Service Provider Terminal...");
                break;
            }  
        }
   }
    
   public void doViewAllAppointments(ServiceProviderEntity currentServiceProviderEntity) {
       Scanner scanner = new Scanner(System.in);
       Integer response = 1;
       System.out.println("*** Service provider terminal :: View Appointments ***\n");
       System.out.println("Appointments: ");
       
       System.out.printf("%-22s%-15s%-10s%-20s%-15s\n", "Name", " | Date", " | Time", " | Appointment No.", " | Status");

       List<AppointmentEntity> appointments = serviceProviderEntitySessionBeanRemote.retrieveAllAppointmentsForServiceProvider(currentServiceProviderEntity);
       for (AppointmentEntity appointment : appointments) {
            
            System.out.printf("%-22s%-15s%-10s%-20s%-15s\n", appointment.getCustomerEntity().getFirstName() + " " + appointment.getCustomerEntity().getLastName(), " | " + appointment.getAppointmentDate().toString(), " | " + appointment.getAppointmentTime().toString(), " | " + appointment.getAppointmentNum(), " | " + appointmentEntitySessionBeanRemote.getStatus(appointment));
            
       }
       System.out.println();     
       while (true) {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("> ");
            try {
                    response = scanner.nextInt();
                }   catch (InputMismatchException ex) {
                    System.err.println("Please input digits only.");
                    scanner.next();
                }
            if (response == 0) {
                System.out.println("Heading back to Service Provider Main Menu...");
                break;
            }
        }
   } 
   
   public void doCancelAppointment(ServiceProviderEntity currentProviderEntity) {
       Scanner sc = new Scanner(System.in);
       String appointmentNum; 
       System.out.println("*** Service provider terminal :: Cancel Appointment ***\n");
       System.out.println("Appointments: ");
       
       System.out.printf("%-22s%-15s%-10s%-20s\n", "Name", " | Date", " | Time", " | Appointment No.");
       List<AppointmentEntity> appointments = serviceProviderEntitySessionBeanRemote.retrieveUpcomingAppointmentsForServiceProvider(currentServiceProviderEntity);
       for (AppointmentEntity appointment : appointments) {
           System.out.printf("%-22s%-15s%-10s%-20s\n", appointment.getCustomerEntity().getFirstName() + " " + appointment.getCustomerEntity().getLastName(), " | " + appointment.getAppointmentDate().toString(), " | " + appointment.getAppointmentTime().toString(), " | " + appointment.getAppointmentNum()); 
       }
       System.out.println();
       while(true) {
           System.out.println("Enter 0 to go back to the previous menu.");
           System.out.print("Enter Appointment ID> ");
            try {
                appointmentNum = sc.nextLine().trim();
                System.out.println(); 
                if(appointmentNum.equals("0")) {
                    System.out.println("Heading back to Service Provider Main Menu...");
                    break;
                } else {
                    appointmentEntitySessionBeanRemote.cancelAppointment(appointmentNum);
                    System.err.println("Appointment " + appointmentNum + " has been cancelled successfully.");
                    sc.next();
                }
            } catch (AppointmentNotFoundException ex) {
                System.out.println("An error has occured cancelling the appointment: " + ex.getMessage());
            } 
        }
   }
   
}
