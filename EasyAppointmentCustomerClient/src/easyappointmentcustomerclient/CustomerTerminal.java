package easyappointmentcustomerclient;

import java.util.InputMismatchException;
import java.util.Scanner;
import ws.client.CustomerEntity;
import ws.client.CustomerExistException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.UnknownPersistenceException_Exception;

public class CustomerTerminal 
{
    private CustomerEntity loggedInCustomerEntity;

    public CustomerTerminal() 
    {
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) 
        {
            System.out.println("*** Welcome to the Customer Terminal ***\n");
            System.out.println("1: Registration");
            System.out.println("2: Login");
            System.out.println("3: Exit");
            response = 0;

            while (response < 1 || response > 3) {
                try
                {
                    System.out.print("> ");
                    response = scanner.nextInt();                   
                }
                catch (InputMismatchException ex)
                {
                    System.err.println("Please input digits only.");
                    scanner.next();
                    continue;
                }

                if (response == 1) {                  

                    try 
                    {
                        doRegistration();
                        System.out.println("Registration successful!\n");
                        CustomerModule customerModule = new CustomerModule(loggedInCustomerEntity);
                        customerModule.mainMenu();
                    } 
                    catch (CustomerExistException_Exception | UnknownPersistenceException_Exception ex) 
                    {
                        System.out.println("An error occured while registering: " + ex.getMessage());
                        System.out.println();
                    }


                } 
                else if (response == 2) 
                {
                    try 
                    {
                        doLogin();
                        System.out.println("Login successful!\n");
                        CustomerModule customerModule = new CustomerModule(loggedInCustomerEntity);
                        customerModule.mainMenu();
                    } 
                    catch (InvalidLoginCredentialException_Exception ex) 
                    {
                        System.err.println("An error occured while logging in: " + ex.getMessage());
                    }
                } 
                else if (response == 3) 
                {
                    break;
                } 
                else 
                {
                    System.err.println("Please key in 1 ~ 3 only.");
                }
            }

            if (response == 3) 
            {
                System.out.println("Thank you!\n");
                break;
            }
        }
    }
    
    public void doRegistration() throws UnknownPersistenceException_Exception, CustomerExistException_Exception 
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Registration ***\n");

        System.out.print("Enter Identity Number> ");
        String iDNum = scanner.nextLine().trim();
        System.out.print("Enter Email> ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        Integer password = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter First Name> ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter Last Name> ");
        String lastName = scanner.nextLine().trim();
        String gender;
        while (true)
        {
            System.out.print("Enter Gender> ");
            gender = scanner.nextLine().trim().toUpperCase();
            if (gender.length() > 0)
            {
                if (gender.equals("M") || gender.equals("F"))
                {
                    break;
                }
                else
                {
                    System.err.println("Please enter 'M' or 'F' only.");
                }
            }
            else 
            {
                System.err.println("Please enter 'M' or 'F'.");
            }
        }   
        System.out.print("Enter Age> ");
        Integer age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Phone Number> ");
        String phoneNum = scanner.nextLine().trim();
        System.out.print("Enter Address> ");
        String address = scanner.nextLine().trim();
        System.out.print("Enter City> ");
        String city = scanner.nextLine().trim();
        
        if (iDNum.length() > 0 && email.length() > 0 && password.toString().length() > 0
                && firstName.length() > 0 && lastName.length() > 0 && gender.length() > 0
                && age.toString().length() > 0 && phoneNum.length() > 0 && address.length() > 0
                && city.length() > 0) 
        {
            CustomerEntity customerEntity = new CustomerEntity();
            customerEntity.setIdentityNumber(iDNum);
            customerEntity.setEmail(email);
            customerEntity.setPassword(password);
            customerEntity.setFirstName(firstName);
            customerEntity.setLastName(lastName);
            customerEntity.setGender(gender);
            customerEntity.setAge(age);
            customerEntity.setPhoneNumber(phoneNum);
            customerEntity.setAddress(address);
            customerEntity.setCity(city);
            
            loggedInCustomerEntity = createCustomerEntity(customerEntity);
        } 
        else 
        {
            System.out.println("Please fill in all credentials to register.");
        }

    }
    
    private void doLogin() throws InvalidLoginCredentialException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        Integer password;

        System.out.println("*** Customer terminal :: Login ***\n");
        System.out.print("Enter Email Address> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextInt();

        if (email.length() > 0 && password.toString().length() > 0) {
            loggedInCustomerEntity = customerLogin(email, password);
        } else {
            System.out.println("Please fill in all credentials to login.");
        }
    }


    private static CustomerEntity customerLogin(java.lang.String email, java.lang.Integer password) throws InvalidLoginCredentialException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.customerLogin(email, password);
    }
    
    private static CustomerEntity createCustomerEntity(ws.client.CustomerEntity customerEntity) throws UnknownPersistenceException_Exception, CustomerExistException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.createCustomerEntity(customerEntity);
    }

}
