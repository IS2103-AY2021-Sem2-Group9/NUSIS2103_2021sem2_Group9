package easyappointmentcustomerclient;

import java.util.Scanner;
import ws.client.CustomerEntity;

public class CustomerModule {

    private CustomerEntity loggedInCustomerEntity;
    
    public CustomerModule() 
    {
    }
    
    public CustomerModule(CustomerEntity loggedInCustomerEntity) 
    {
        this.loggedInCustomerEntity = loggedInCustomerEntity;
    }
    
    public void mainMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** Customer Terminal :: Main ***\n");
            System.out.println("You are now logged in as " + loggedInCustomerEntity.getFirstName() + " " + loggedInCustomerEntity.getLastName() + "\n");
            System.out.println("1: Search Operation");
            System.out.println("2: Add Appointments");
            System.out.println("3: View Appointments");
            System.out.println("4: Cancel Appointments");
            System.out.println("5: Rate Service Provider");
            System.out.println("6: Logout");

            response = 0;

            while (response < 1 || response > 6) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                    //searchOperation();
                } else if (response == 2) {
                    //addAppointments();
                } else if (response == 3) {
                    //viewAppointments();
                } else if (response == 4) {
                    //cancelAppointments();
                } else if (response == 5) {
                    //rateServiceProvider();
                } else if (response == 6) {
                    break;
                } else {
                    System.out.println("Please key in 1 ~ 6 only.");
                }
            }

            if (response == 6) {
                System.out.println("Thank you! Logging out...\n");
                break;
            }
        }
    }
}
