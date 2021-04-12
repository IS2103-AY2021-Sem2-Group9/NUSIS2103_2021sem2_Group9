package easyappointmentcustomerclient;

import java.util.InputMismatchException;
import java.util.Scanner;
import ws.client.CustomerEntity;
import ws.client.CustomerExistException_Exception;
import ws.client.InvalidLoginCredentialException_Exception;
import ws.client.InvalidPasswordFormatException_Exception;
import ws.client.UnknownPersistenceException_Exception;

public class CustomerTerminal {

    private CustomerEntity loggedInCustomerEntity;

    public CustomerTerminal() {
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
                try {
                    System.out.print("> ");
                    response = scanner.nextInt();
                } catch (InputMismatchException ex) {
                    System.err.println("Please input digits only.");
                    scanner.next();
                    continue;
                }

                if (response == 1) {

                    try {
                        doRegistration();
                        System.out.println("Registration successful!\n");
                        CustomerModule customerModule = new CustomerModule(loggedInCustomerEntity);
                        customerModule.mainMenu();
                    } catch (CustomerExistException_Exception | UnknownPersistenceException_Exception | InvalidPasswordFormatException_Exception ex) {
                        System.err.println("An error occured while registering: " + ex.getMessage());
                        System.out.println();
                    }

                } else if (response == 2) {
                    try {
                        doLogin();
                        System.out.println("Login successful!\n");
                        CustomerModule customerModule = new CustomerModule(loggedInCustomerEntity);
                        customerModule.mainMenu();
                    } catch (InvalidLoginCredentialException_Exception ex) {
                        System.err.println("An error occured while logging in: " + ex.getMessage());
                    }
                } else if (response == 3) {
                    break;
                } else {
                    System.err.println("Please key in 1 ~ 3 only.");
                }
            }

            if (response == 3) {
                System.out.println("Thank you!\n");
                break;
            }
        }
    }

    public void doRegistration() throws UnknownPersistenceException_Exception, CustomerExistException_Exception, InvalidPasswordFormatException_Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Registration ***\n");

        String iDNum;
        while (true) {
            System.out.print("Enter Identity Number> ");
            iDNum = scanner.nextLine().trim();
            if (iDNum.length() > 0) {
                break;
            }
            System.err.println("Please input an Identity Number.");
        }

        String email;
        while (true) {
            System.out.print("Enter Email> ");
            email = scanner.nextLine().trim();
            if (email.length() > 0) {
                break;
            }
            System.err.println("Please input an Email.");
        }

        String password;
        while (true) {
            System.out.print("Enter Password> ");
            password = scanner.nextLine().trim();
            if (password.length() > 0) {
                break;
            }
            System.err.println("Please input a password.");
        }

        String firstName;
        while (true) {
            System.out.print("Enter First Name> ");
            firstName = scanner.nextLine().trim();
            if (firstName.length() > 0) {
                break;
            }
            System.err.println("Please input a First Name.");
        }

        String lastName;
        while (true) {
            System.out.print("Enter Last Name> ");
            lastName = scanner.nextLine().trim();
            if (lastName.length() > 0) {
                break;
            }
            System.err.println("Please input a Last Name.");
        }

        String gender;
        while (true) {
            System.out.print("Enter Gender> ");
            gender = scanner.nextLine().trim().toUpperCase();
            if (gender.length() > 0) {
                if (gender.equals("M") || gender.equals("F")) {
                    break;
                } else {
                    System.err.println("Please enter 'M' or 'F' only.");
                }
            } else {
                System.err.println("Please enter 'M' or 'F'.");
            }
        }

        Integer age;
        while (true) {
            try {
                System.out.print("Enter Age> ");
                age = scanner.nextInt();
                break;
            } catch (InputMismatchException ex) {
                System.err.println("Please input a number.");
                scanner.next();
            }
        }

        scanner.nextLine();

        String phoneNum;
        while (true) {
            System.out.print("Enter Phone Number> ");
            phoneNum = scanner.nextLine().trim();
            if (phoneNum.length() > 0) {
                break;
            }
            System.err.println("Please input a Phone Number.");
        }

        String address;
        while (true) {
            System.out.print("Enter Address> ");
            address = scanner.nextLine().trim();
            if (address.length() > 0) {
                break;
            }
            System.err.println("Please input an Address.");
        }

        String city;
        while (true) {
            System.out.print("Enter City> ");
            city = scanner.nextLine().trim();
            if (city.length() > 0) {
                break;
            }
            System.err.println("Please input a City.");
        }

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

    private void doLogin() throws InvalidLoginCredentialException_Exception {
        Scanner scanner = new Scanner(System.in);
        String email = "";
        Integer password; // check length is 6

        System.out.println("*** Customer terminal :: Login ***\n");

        while (true) {
            System.out.print("Enter Email Address> ");
            email = scanner.nextLine().trim();
            if (email.length() > 0) {
                break;
            }
            System.err.println("Please input login email.");
        }

        while (true) {
            try {
                System.out.print("Enter password> ");
                password = scanner.nextInt();
                if (password.toString().length() != 6) {
                    System.err.println("Please input 6 digits");
                    continue;
                }
                break;
            } catch (InputMismatchException ex) {
                System.err.println("Please input a number.");
                scanner.next();
            }
        }

            loggedInCustomerEntity = customerLogin(email, password);
    }

    /************************************************************** Web Services **************************************************************/
    private static CustomerEntity createCustomerEntity(ws.client.CustomerEntity customerEntity) throws InvalidPasswordFormatException_Exception, CustomerExistException_Exception, UnknownPersistenceException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.createCustomerEntity(customerEntity);
    }

    private static CustomerEntity customerLogin(java.lang.String email, java.lang.Integer password) throws InvalidLoginCredentialException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.customerLogin(email, password);
    }
}
