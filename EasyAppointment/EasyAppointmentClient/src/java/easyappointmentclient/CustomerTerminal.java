package easyappointmentclient;

import ejb.session.stateless.AppointmentEntitySessionBeanRemote;
import ejb.session.stateless.BusinessCategorySessionBeanRemote;
import ejb.session.stateless.CustomerEntitySessionBeanRemote;
import ejb.session.stateless.ServiceProviderEntitySessionBeanRemote;
import entity.CustomerEntity;
import java.util.Scanner;
import util.exception.CustomerExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
import util.exception.UnknownPersistenceException;

public class CustomerTerminal {

    private CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote;
    private ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote;
    private BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote;
    private AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote;

    private CustomerEntity loggedInCustomerEntity;
    private CustomerModule customerModule;

    public CustomerTerminal() {
    }

    public CustomerTerminal(CustomerEntitySessionBeanRemote customerEntitySessionBeanRemote, ServiceProviderEntitySessionBeanRemote serviceProviderSessionBeanRemote, BusinessCategorySessionBeanRemote businessCategorySessionBeanRemote, AppointmentEntitySessionBeanRemote appointmentEntitySessionBeanRemote) 
    {
        this.customerEntitySessionBeanRemote = customerEntitySessionBeanRemote;
        this.serviceProviderSessionBeanRemote = serviceProviderSessionBeanRemote;
        this.businessCategorySessionBeanRemote = businessCategorySessionBeanRemote;
        this.appointmentEntitySessionBeanRemote = appointmentEntitySessionBeanRemote;
    }

    public void runApp() {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to the Customer Terminal ***\n");
            System.out.println("1: Registration");
            System.out.println("2: Login");
            System.out.println("3: Exit");
            response = 0;

            while (response < 1 || response > 3) {
                System.out.print("> ");
                response = scanner.nextInt();

                if (response == 1) {
                     
                    try {
                        doRegistration();
                        System.out.println("Registration successful!\n");
                        
                        customerModule = new CustomerModule(appointmentEntitySessionBeanRemote, customerEntitySessionBeanRemote, serviceProviderSessionBeanRemote, businessCategorySessionBeanRemote, loggedInCustomerEntity);
                        customerModule.mainMenu();
                    } catch (InvalidLoginCredentialException | CustomerExistException | InvalidPasswordFormatException | UnknownPersistenceException ex) {
                        System.out.println(ex.getMessage());
                        System.out.println();
                    }
                    

                } else if (response == 2) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");

                        customerModule = new CustomerModule(appointmentEntitySessionBeanRemote, customerEntitySessionBeanRemote, serviceProviderSessionBeanRemote, businessCategorySessionBeanRemote, loggedInCustomerEntity);
                        customerModule.mainMenu();
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("An error has occurred while logging in: " + ex.getMessage() + "\n");
                    }
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Please key in 1 ~ 3 only.");
                }
            }

            if (response == 3) {
                System.out.println("Thank you!\n");
                break;
            }
        }
    }

    public void doRegistration() throws InvalidLoginCredentialException, CustomerExistException, InvalidPasswordFormatException, UnknownPersistenceException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Registration ***\n");

        System.out.print("Enter Identity Number> ");
        String iDNum = scanner.nextLine().trim();
        System.out.print("Enter Email> ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password> ");
        String password = scanner.nextLine().trim();
        System.out.print("Enter First Name> ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter Last Name> ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Enter Gender> ");
        String gender = scanner.nextLine().trim();
        System.out.print("Enter Age> ");
        Integer age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Phone Number> ");
        String phoneNum = scanner.nextLine().trim();
        System.out.print("Enter Address> ");
        String address = scanner.nextLine().trim();
        System.out.print("Enter City> ");
        String city = scanner.nextLine().trim();
        
        if (iDNum.length() > 0 && email.length() > 0 && password.length() > 0
                && firstName.length() > 0 && lastName.length() > 0 && gender.length() > 0
                && age.toString().length() > 0 && phoneNum.length() > 0 && address.length() > 0
                && city.length() > 0) {

            CustomerEntity customerEntity = new CustomerEntity(iDNum, email, password, firstName, lastName, gender, age, phoneNum, address, city);
            loggedInCustomerEntity = customerEntitySessionBeanRemote.createCustomerEntity(customerEntity);
            
        } else {
            throw new InvalidLoginCredentialException("Missing Account Credentials");
        }

    }

    private void doLogin() throws InvalidLoginCredentialException {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        Integer password;

        System.out.println("*** Customer terminal :: Login ***\n");
        System.out.print("Enter Email Address> ");
        email = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextInt();

        if (email.length() > 0 && password.toString().length() > 0) {
            loggedInCustomerEntity = customerEntitySessionBeanRemote.customerLogin(email, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential");
        }

    }
}
