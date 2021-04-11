package easyappointmentcustomerclient;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.AppointmentEntity;
import ws.client.BusinessCategoryEntity;
import ws.client.CustomerEntity;
import ws.client.ServiceProviderEntity;
import ws.client.AppointmentExistException_Exception;
import ws.client.AppointmentNotFoundException_Exception;
import ws.client.BusinessCategoryNotFoundException_Exception;
import ws.client.CustomerExistException_Exception;
import ws.client.CustomerNotFoundException_Exception;
import ws.client.ServiceProviderEntityNotFoundException_Exception;
import ws.client.UnknownPersistenceException_Exception;

public class CustomerModule {

    private CustomerEntity loggedInCustomerEntity;

    public CustomerModule() {
    }

    public CustomerModule(CustomerEntity loggedInCustomerEntity) {
        this.loggedInCustomerEntity = loggedInCustomerEntity;
    }

    public void mainMenu() {
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
                    searchOperation();
                } else if (response == 2) {
                    addAppointments();
                } else if (response == 3) {
                    viewAppointments();
                } else if (response == 4) {
                    cancelAppointments();
                } else if (response == 5) {
                    rateServiceProvider();
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

    public LocalDate searchOperation() {
        Scanner sc = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Search Operation ***\n");

        // Get all the biz categories and print them
        List<BusinessCategoryEntity> businessCategories = retrieveAllBusinessCategories();
        for (int i = 0; i < businessCategories.size(); i++) {
            if (i == businessCategories.size() - 1) {
                System.out.print(i + 1);
                System.out.print(" ");
                System.out.println(businessCategories.get(i).getCategoryName());
            } else {
                System.out.print(i + 1);
                System.out.print(" ");
                System.out.print(businessCategories.get(i).getCategoryName());
                System.out.print(" | ");
            }

        }

        System.out.print("Enter business category> ");
        Long category = sc.nextLong();
        sc.nextLine();
        System.out.print("Enter city> ");
        String city = sc.nextLine();

        System.out.print("Enter date> ");
        String dateStr = sc.nextLine(); // e.g. yyyy-MM-dd
//        LocalDate date = LocalDate.parse(dateStr); 

        // Print headers
        System.out.printf("%-20s | %-15s | %-20s | %-15s | %-15s", "Service Provider Id", "Name", "First available Time", "Address", "Overall rating");
        System.out.println();

        try {
            List<ServiceProviderEntity> serviceProviders = retrieveAllAvailableServiceProvidersForTheDay(dateStr, category, city);

            // Print record rows
            for (int i = 0; i < serviceProviders.size(); i++) {
                ServiceProviderEntity currentSP = serviceProviders.get(i);
                Long spId = currentSP.getServiceProviderId();
                String name = currentSP.getName();
//                LocalTime temp = retrieveServiceProviderAvailabilityForTheDay(currentSP, dateStr).get(0);
                String firstAvaiTime = retrieveServiceProviderAvailabilityForTheDay(currentSP, dateStr).get(0);
                String address = currentSP.getAddress();
                Double rating = generateOverallRating(currentSP);

                System.out.printf("%-20d | %-15s | %-20s | %-15s | %.2f", spId, name, firstAvaiTime, address, rating);
                System.out.println("\n");
            }

            return LocalDate.parse(dateStr);
        } catch (BusinessCategoryNotFoundException_Exception ex) {
            System.err.println("An error has occurred while retrieving available service providers for the specified date: " + ex.getMessage() + "\n");
            return null;
        }

    }

    public LocalDate searchForAddingAppt() { // Strictly for addAppointments()
        Scanner sc = new Scanner(System.in);

        // Get all the biz categories and print them
        List<BusinessCategoryEntity> businessCategories = retrieveAllBusinessCategories();
        for (int i = 0; i < businessCategories.size(); i++) {
            if (i == businessCategories.size() - 1) {
                System.out.print(i + 1);
                System.out.print(" ");
                System.out.println(businessCategories.get(i).getCategoryName());
            } else {
                System.out.print(i + 1);
                System.out.print(" ");
                System.out.print(businessCategories.get(i).getCategoryName());
                System.out.print(" | ");
            }

        }

        System.out.print("Enter business category> ");
        Long category = sc.nextLong();
        sc.nextLine();
        System.out.print("Enter city> ");
        String city = sc.nextLine();

        // Init
        int compare = 1;
        LocalDate date = LocalDate.now();
        String dateStr = "";

        while (compare > 0) { // compare must be <= 0
            System.out.print("Enter date> ");
            dateStr = sc.nextLine();
            date = LocalDate.parse(dateStr);
            LocalDate todaysDate = LocalDate.now();
            compare = todaysDate.compareTo(date); // Returns -1 if today is before "date", 0 if same day, 1 if today is after "date"
            if (compare > 0) {
                System.out.println("Date has already passed. Enter another valid date.");
            }
        }

        try {
            List<ServiceProviderEntity> serviceProviders = retrieveAllAvailableServiceProvidersForTheDay(dateStr, category, city);

            // Print record rows
            for (int i = 0; i < serviceProviders.size(); i++) {
                ServiceProviderEntity currentSP = serviceProviders.get(i);
                Long spId = currentSP.getServiceProviderId();
                String name = currentSP.getName();
                String firstAvaiTime = retrieveServiceProviderAvailabilityForTheDay(currentSP, dateStr).get(0);
                String address = currentSP.getAddress();
                Double rating = generateOverallRating(currentSP);

                // Print headers
                System.out.printf("%-20s | %-20s | %-20s | %-20s | %-15s", "Service Provider Id", "Name", "First available Time", "Address", "Overall rating");
                System.out.println("\n");
                if (rating == 0) {
                    // Print records
                    System.out.printf("%-20d | %-20s | %-20s | %-20s | %s", spId, name, firstAvaiTime, address, "No Ratings");
                    System.out.println("\n");
                } else {
                    // Print records
                    System.out.printf("%-20d | %-20s | %-20s | %-20s | %.2f", spId, name, firstAvaiTime, address, rating);
                    System.out.println("\n");
                }

            }

            return date;
        } catch (BusinessCategoryNotFoundException_Exception ex) {
            System.err.println("An error has occurred while retrieving available service providers for the specified date: " + ex.getMessage() + "\n");
            return null;
        }

    }

    public void addAppointments() {
        Scanner sc = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Add Appointments ***\n");
        LocalDate date = this.searchForAddingAppt();

        try {
            System.out.println("Enter 0 to go back to the previous menu.");
            System.out.print("Service provider Id> ");
            Long spId = sc.nextLong();
            if (spId == 0) {
                return; // Break out of the method
            }
            System.out.println();
            sc.nextLine();

            ServiceProviderEntity spEntity = retrieveServiceProviderByServiceProviderId(spId);
            // check if service provider belongs to city
            List<String> availableTimeSlotsStr = retrieveServiceProviderAvailabilityForTheDay(spEntity, date.toString());
            List<LocalTime> availableTimeSlots = new ArrayList<>();
            for (int i = 0; i < availableTimeSlotsStr.size(); i++) {
                availableTimeSlots.add(LocalTime.parse(availableTimeSlotsStr.get(i)));
            }

            List<String> avaiList = new ArrayList<>();

            int compare = LocalDate.now().compareTo(date);

            for (int i = 0; i < availableTimeSlots.size(); i++) { // To add it into another arraylist
                LocalTime now = LocalTime.now();
                LocalTime time = availableTimeSlots.get(i); // Each available time slot
                Long hourDiff = ChronoUnit.HOURS.between(now, time); // Must be >= 2

                if (compare == 0) { // if date is today
                    if (hourDiff >= 2) { // check if it is 2hr in advance
                        avaiList.add(time.toString());
                    }
                } else {
                    avaiList.add(time.toString());
                }
            }

            if (avaiList.isEmpty()) {
                System.out.println("No more available appointment slots for today.");

                System.out.print("Exit> ");
                String response = sc.nextLine();
                while (!response.equals("0")) {
                    System.out.println("Invalid input, please try again!");
                    System.out.print("Exit> ");
                    response = sc.nextLine();
                    if (response.equals("0")) {
                        return;
                    }
                }
            } else {
                System.out.println("Available Appointment slots:");
            }

            for (int i = 0; i < avaiList.size(); i++) {
                if (i == availableTimeSlots.size() - 1) {
                    System.out.print(avaiList.get(i));
                } else {
                    System.out.printf("%s | ", avaiList.get(i));
                }
            }

            System.out.println();

            System.out.println("Enter 0 to go back to previous menu");

            System.out.print("Enter time> ");
            String timeStr = sc.nextLine(); // e.g. 11:30
            if (timeStr.equals("0")) {
                return; // Break out of method
            }
//            LocalTime selectedTimeSlot = LocalTime.parse(timeStr);
            System.out.println();

            AppointmentEntity appt = createAppointmentEntity(date.toString(), timeStr, loggedInCustomerEntity.getId(), spEntity.getServiceProviderId()); // customer side
            // addAppointment(date.toString(), timeStr, appt.getAppointmentNum(), spEntity.getServiceProviderId()); // sp side

            System.out.printf("The appointment with %s %s at %s on %s is confirmed.", this.loggedInCustomerEntity.getFirstName(), this.loggedInCustomerEntity.getLastName(), timeStr, date.toString());
            System.out.println();

        } catch (DateTimeException ex) {
            System.out.println("Error occurred when reading date: " + ex.getMessage() + "\n");
        } catch (ServiceProviderEntityNotFoundException_Exception | UnknownPersistenceException_Exception | AppointmentExistException_Exception | CustomerNotFoundException_Exception ex) {
            Logger.getLogger(CustomerModule.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Enter 0 to go back to previous menu");
        System.out.print("Exit> ");
        String response = sc.nextLine();
        while (!response.equals("0")) {
            System.out.println("Invalid input, please try again!");
            System.out.print("Exit> ");
            response = sc.nextLine();
            if (response.equals("0")) {
                return;
            }
        }
    }

    public void viewAppointments() {
        System.out.println("*** Customer Terminal :: View Appointments ***\n");

        try {
            List<AppointmentEntity> appointments = retrieveCustomerEntityAppointments(this.loggedInCustomerEntity.getId());

            if (!appointments.isEmpty()) {

                // Print headers
                System.out.printf("%-20s | %-20s | %-20s | %s", "Appointment Number", "Appointment Date", "Appointment Time", "Service Provider");
                System.out.println("\n");
                for (int i = 0; i < appointments.size(); i++) {
                    AppointmentEntity appt = appointments.get(i);
                    String apptNum = appt.getAppointmentNum();
                    String dateStr = retrieveAppointmentDateWithApptNum(apptNum);
                    String timeStr = retrieveAppointmentTimeWithApptNum(apptNum);
                    String apptSPName = appt.getServiceProviderEntity().getName();

                    // Print records
                    System.out.printf("%-20s | %-20s | %-20s | %s", apptNum, dateStr, timeStr, apptSPName);
                    System.out.println();
                }
            } else {
                System.out.println("There are currently no appointments.");
            }
            System.out.println();
        } catch (CustomerNotFoundException_Exception | AppointmentNotFoundException_Exception ex) {
            System.err.println("Error occurred when retrieving customer: " + ex.getMessage());
        }

    }

    public void viewForCancellingAppt() {
        try {
            List<AppointmentEntity> appointments = retrieveCustomerEntityAppointments(this.loggedInCustomerEntity.getId());

            if (!appointments.isEmpty()) {

                // Print headers
                System.out.printf("%-20s | %-20s | %-20s | %s", "Appointment Number", "Appointment Date", "Appointment Time", "Service Provider");
                System.out.println("\n");
                for (int i = 0; i < appointments.size(); i++) {
                    AppointmentEntity appt = appointments.get(i);
                    String apptNum = appt.getAppointmentNum();
                    String dateStr = retrieveAppointmentDateWithApptNum(apptNum);
                    String timeStr = retrieveAppointmentTimeWithApptNum(apptNum);
                    String apptSPName = appt.getServiceProviderEntity().getName();

                    // Print records
                    System.out.printf("%-20s | %-20s | %-20s | %s", apptNum, dateStr, timeStr, apptSPName);
                    System.out.println();
                }
            } else {
                System.out.println("There are currently no appointments.");
            }
            System.out.println();
        } catch (CustomerNotFoundException_Exception | AppointmentNotFoundException_Exception ex) {
            System.err.println("Error occurred when retrieving customer: " + ex.getMessage());
        }

    }

    public void cancelAppointments() {

        Scanner sc = new Scanner(System.in);
        String appointmentNum;
        System.out.println("*** Customer terminal :: Cancel Appointment ***\n");
        this.viewForCancellingAppt();
        System.out.print("Enter Appointment Number> ");
        appointmentNum = sc.nextLine().trim();
        try {
            cancelAppointment(appointmentNum); 
            System.out.println("Appointment " + appointmentNum + " has been cancelled successfully.\n");
        } catch (AppointmentNotFoundException_Exception ex) {
            System.out.println("An error has occured when cancelling the appointment: " + ex.getMessage());
        }
    }

    public void rateServiceProvider() {
        Scanner sc = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Rate Service Provider ***\n");

        System.out.print("Enter Service Provider Id> ");
        Long spId = sc.nextLong();
        List<AppointmentEntity> apptsToRate = new ArrayList<>();

        try {
            List<AppointmentEntity> appts = retrieveCustomerEntityAppointments(this.loggedInCustomerEntity.getId());

            if (appts.isEmpty()) {
                System.out.println("There are no appointments to rate.");
            } else {
                for (int i = 0; i < appts.size(); i++) {
                    AppointmentEntity apptEntity = appts.get(i);
                    Long retrievedSPId = apptEntity.getServiceProviderEntity().getServiceProviderId();
                    if (spId.equals(retrievedSPId) && apptEntity.getRating() == 0) { // if rating == 0 means not yet rated, rating != 0 means rated, dont rate again!
                        apptsToRate.add(apptEntity);
                    }
                }

                System.out.println("Appointments that you have yet to rate:");
                // Print Headers
                System.out.printf("%-5s | %-20s | %-20s | %-20s | %s", "Index", "Appointment Number", "Appointment Date", "Appointment Time", "Service Provider");
                System.out.println("\n");
                for (int i = 0; i < apptsToRate.size(); i++) {
                    AppointmentEntity apptEntity = apptsToRate.get(i);
                    String apptNum = apptEntity.getAppointmentNum();
                    String dateStr = retrieveAppointmentDateWithApptNum(apptNum);
                    String timeStr = retrieveAppointmentTimeWithApptNum(apptNum);
                    System.out.printf("%-5s | %-20s | %-20s | %-20s | %s", i + 1, apptNum, dateStr,
                            timeStr, apptEntity.getServiceProviderEntity().getName());
                    System.out.println();
                }

                if (apptsToRate.isEmpty()) {
                    System.out.println("You have no appointments to rate.");
                } else {
                    System.out.print("Enter index number of the appointment to rate> ");
                    Integer index = sc.nextInt();
                    while (index < 1 || index > apptsToRate.size()) {
                        System.out.print("Invalid index. Enter index again> ");
                        index = sc.nextInt();
                    }

                    System.out.print("Enter rating> ");
                    Integer rating = sc.nextInt();
                    while (rating < 1 || rating > 5) {
                        System.out.print("Invalid rating. Enter rating again> ");
                        rating = sc.nextInt();
                    }

                    System.out.println();
                    AppointmentEntity apptEntity = apptsToRate.get(index - 1);
                    //apptEntity.setRating(rating);
                    rateAppointment(apptEntity.getId(), rating);
                    System.out.println("You have rated Appointment " + apptEntity.getAppointmentNum() + " a rating of " + rating + ".");
                }
            }

        } catch (CustomerNotFoundException_Exception | AppointmentNotFoundException_Exception ex) {
            System.out.println("An error has occured when retrieving the appointments: " + ex.getMessage());
        }

    }

    // WS
    private static java.util.List<ws.client.BusinessCategoryEntity> retrieveAllBusinessCategories() {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAllBusinessCategories();
    }

    private static java.util.List<ws.client.ServiceProviderEntity> retrieveAllAvailableServiceProvidersForTheDay(java.lang.String appointmentDate, java.lang.Long category, java.lang.String city) throws BusinessCategoryNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAllAvailableServiceProvidersForTheDay(appointmentDate, category, city);
    }

    private static java.util.List<java.lang.String> retrieveServiceProviderAvailabilityForTheDay(ws.client.ServiceProviderEntity spEntity, java.lang.String appointmentDate) {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveServiceProviderAvailabilityForTheDay(spEntity, appointmentDate);
    }

    private static double generateOverallRating(ws.client.ServiceProviderEntity spEntity) {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.generateOverallRating(spEntity);
    }

    private static ServiceProviderEntity retrieveServiceProviderByServiceProviderId(java.lang.Long serviceProviderId) throws ServiceProviderEntityNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveServiceProviderByServiceProviderId(serviceProviderId);
    }

    private static void cancelAppointment(java.lang.String appointmentNum) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.cancelAppointment(appointmentNum);
    }

    private static java.util.List<ws.client.AppointmentEntity> retrieveCustomerEntityAppointments(java.lang.Long customerId) throws CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveCustomerEntityAppointments(customerId);
    }

    private static String retrieveAppointmentDateWithApptNum(java.lang.String apptNum) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAppointmentDateWithApptNum(apptNum);
    }

    private static String retrieveAppointmentTimeWithApptNum(java.lang.String apptNum) throws AppointmentNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.retrieveAppointmentTimeWithApptNum(apptNum);
    }

    private static void addAppointment(java.lang.String dateStr, java.lang.String timeStr, java.lang.String apptNum, java.lang.Long spId) throws AppointmentNotFoundException_Exception, ServiceProviderEntityNotFoundException_Exception, CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.addAppointment(dateStr, timeStr, apptNum, spId);
    }

    private static AppointmentEntity createAppointmentEntity(java.lang.String appointmentDate, java.lang.String apptTime, java.lang.Long customerId, java.lang.Long spId) throws UnknownPersistenceException_Exception, ServiceProviderEntityNotFoundException_Exception, AppointmentExistException_Exception, CustomerNotFoundException_Exception {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.createAppointmentEntity(appointmentDate, apptTime, customerId, spId);
    }

    private static void rateAppointment(long apptEntityId, int rating) {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        port.rateAppointment(apptEntityId, rating);
    }

}
