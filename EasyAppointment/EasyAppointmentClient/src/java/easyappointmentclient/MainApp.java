package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import java.util.Scanner;

public class MainApp {
    private AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;

    public MainApp() 
    {
    }

    public MainApp(AdminEntitySessionBeanRemote adminEntitySessionBeanRemote) 
    {
        this.adminEntitySessionBeanRemote = adminEntitySessionBeanRemote;
    }
    
    public void runApp() 
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** EasyAppointment ***\n");
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
                    //doLogin();
                    System.out.println("Logging in..");
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Please key in 1 or 2 only.");
                }
            }
            
            if (response == 2) 
            {
                System.out.println("Thank you!");
                break;
            }
        }
        
    }
}
