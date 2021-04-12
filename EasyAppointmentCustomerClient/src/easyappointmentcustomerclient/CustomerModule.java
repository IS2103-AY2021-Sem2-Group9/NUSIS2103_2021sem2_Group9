package easyappointmentcustomerclient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import ws.client.AppointmentCannotBeCancelledException_Exception;
import ws.client.AppointmentEntity;
import ws.client.BusinessCategoryEntity;
import ws.client.CustomerEntity;
import ws.client.ServiceProviderEntity;
import ws.client.AppointmentExistException_Exception;
import ws.client.AppointmentNotFoundException_Exception;
import ws.client.BusinessCategoryNotFoundException_Exception;
import ws.client.CustomerNotFoundException_Exception;
import ws.client.ServiceProviderEntityNotFoundException_Exception;
import ws.client.ServiceProviderStatus;
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

        Long category;

        while (true) {
            try {
                System.out.print("Enter business category> ");
                category = sc.nextLong();
                break;
            } catch (InputMismatchException ex) {
                System.err.println("Please input a number.");
                sc.next();
            }
        }

        sc.nextLine();

        String city;
        while (true) {
            System.out.print("Enter city> ");
            city = sc.nextLine();
            if (city.length() > 0) {
                System.out.println("city is not empty");
                break;
            }
        }

        LocalDate date = LocalDate.now();
        String dateStr = "";
        while (true) {
            try {
                System.out.print("Enter date> ");
                dateStr = sc.nextLine().trim();
                date = LocalDate.parse(dateStr);
                break;
            } catch (DateTimeParseException ex) {
                System.err.println("Invalid date input. Please try again.");
            }
        }

        System.out.println();

        // Print headers
        System.out.printf("%-20s | %-20s | %-20s | %-20s | %s", "Service Provider Id", "Name", "First available Time", "Address", "Overall rating");
        System.out.println("\n");

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

    public LocalDate searchForAddingAppt(Long category, String city) { // Strictly for addAppointments()
        Scanner sc = new Scanner(System.in);

        // Init
        int compare = 1;
        LocalDate date = LocalDate.now();
        String dateStr = "";

        while (compare > 0) { // compare must be <= 0

            while (true) {
                try {

                    while (true) {
                        System.out.print("Enter date> ");
                        dateStr = sc.nextLine().trim();
                        if (dateStr.length() > 0) {
                            break;
                        }
                    }

                    date = LocalDate.parse(dateStr);
                    break;
                } catch (DateTimeParseException ex) {
                    System.err.println("Invalid date input. Please try again.");
                }
            }

            LocalDate todaysDate = LocalDate.now();
            compare = todaysDate.compareTo(date); // Returns -1 if today is before "date", 0 if same day, 1 if today is after "date"
            if (compare > 0) {
                System.out.println("Date has already passed. Enter another valid date.");
            }
        }

        // Print headers
        System.out.printf("%-20s | %-20s | %-20s | %-20s | %s", "Service Provider Id", "Name", "First available Time", "Address", "Overall rating");
        System.out.println("\n");
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

        Long category;
        Long spId;
        ServiceProviderEntity spEntity;

        while (true) {
            try {
                System.out.print("Enter business category> ");
                category = sc.nextLong();
                break;
            } catch (InputMismatchException ex) {
                System.err.println("Please input a number.");
                sc.next();
            }
        }

        sc.nextLine();

        String city;
        while (true) {
            System.out.print("Enter city> ");
            city = sc.nextLine().trim();
            if (city.length() > 0) {
                break;
            }
        }

        LocalDate date = this.searchForAddingAppt(category, city);

        try {
            while (true) {
                try {
                    System.out.println("Enter 0 to go back to the previous menu.");
                    System.out.print("Service provider Id> ");
                    spId = sc.nextLong();
                    if (spId == 0) {
                        return; // Break out of the method
                    }

                    spEntity = retrieveServiceProviderByServiceProviderId(spId);

                    // Only allow user to enter spid that is shown - SP must be approved/City must be correct/Category must be correct
                    if (!spEntity.getStatus().equals(ServiceProviderStatus.APPROVED) || !spEntity.getCity().equals(city) || spEntity.getCategory().getId() != category) {
                        System.err.println("No such Service Provider ID. Please enter another.");
                        continue;
                    }
                    System.out.println();
                    sc.nextLine();
                    break;
                } catch (InputMismatchException ex) {
                    System.err.println("Please input a number");
                    sc.next();
                } catch (ServiceProviderEntityNotFoundException_Exception ex) {
                    System.err.println("No such Service Provider ID. Please enter another.");
                }
            }

            List<String> availableTimeSlotsStr = retrieveServiceProviderAvailabilityForTheDay(spEntity, date.toString());
            List<LocalTime> availableTimeSlots = new ArrayList<>();
            for (int i = 0; i < availableTimeSlotsStr.size(); i++) {
                availableTimeSlots.add(LocalTime.parse(availableTimeSlotsStr.get(i)));
            }

            List<String> avaiList = new ArrayList<>(); // To add all the available time slots for the service provider, factoring in the same day 2h in advance condition

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
                    System.err.println("Invalid input, please try again!");
                    System.out.print("Exit> ");
                    response = sc.nextLine();
                    if (response.equals("0")) {
                        return; // Break out of method
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

            String timeStr;

            while (true) {
                System.out.print("Enter time> ");
                timeStr = sc.nextLine(); // e.g. 11:30
                if (timeStr.equals("0")) {
                    return; // Break out of method
                }

                // Check through available time slot list -> only can select if match e.g. only can select 08:30 if 08:30 is in the list
                if (!avaiList.contains(timeStr)) {
                    System.err.println("Time slot is unavailable. Please select another.");
                    continue;
                }
                System.out.println();
                break;
            }

            createAppointmentEntity(date.toString(), timeStr, loggedInCustomerEntity.getId(), spEntity.getServiceProviderId());

            System.out.printf("The appointment with %s %s at %s on %s is confirmed.", this.loggedInCustomerEntity.getFirstName(), this.loggedInCustomerEntity.getLastName(), timeStr, date.toString());
            System.out.println();

            System.out.println("Enter 0 to go back to previous menu");
            System.out.print("Exit> ");
            String response = sc.nextLine();
            while (!response.equals("0")) {
                System.err.println("Invalid input, please try again!");
                System.out.print("Exit> ");
                response = sc.nextLine();
                if (response.equals("0")) {
                    return;
                }
            }
        } catch (UnknownPersistenceException_Exception | ServiceProviderEntityNotFoundException_Exception | AppointmentExistException_Exception | CustomerNotFoundException_Exception ex) {
            System.err.println("Error occurred when creating appointment: " + ex.getMessage());
        }
    }

    public void viewAppointments() {
        System.out.println("*** Customer Terminal :: View Appointments ***\n");

        try {
            List<AppointmentEntity> appointments = retrieveCustomerEntityAppointments(this.loggedInCustomerEntity.getId());

            if (!appointments.isEmpty()) {

                // Print headers
                System.out.printf("%-20s | %-20s | %-20s | %-20s | %s", "Appointment Number", "Appointment Date", "Appointment Time", "Service Provider", "Status");
                System.out.println("\n");
                for (int i = 0; i < appointments.size(); i++) {
                    AppointmentEntity appt = appointments.get(i);
                    String apptNum = appt.getAppointmentNum();
                    String dateStr = retrieveAppointmentDateWithApptNum(apptNum);
                    String timeStr = retrieveAppointmentTimeWithApptNum(apptNum);
                    String apptSPName = appt.getServiceProviderEntity().getName();
                    String apptStatus = getApptStatus(appt.getId());

                    // Print records
                    System.out.printf("%-20s | %-20s | %-20s | %-20s | %s", apptNum, dateStr, timeStr, apptSPName, apptStatus);
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

    public boolean viewForCancellingAppt() {
        boolean haveAppts = false;
        try {
            List<AppointmentEntity> appointments = retrieveCustomerEntityAppointments(this.loggedInCustomerEntity.getId());

            if (!appointments.isEmpty()) {
                haveAppts = true;
                // Print headers
                System.out.printf("%-20s | %-20s | %-20s | %-20s | %s", "Appointment Number", "Appointment Date", "Appointment Time", "Service Provider", "Status");
                System.out.println("\n");
                for (int i = 0; i < appointments.size(); i++) {
                    AppointmentEntity appt = appointments.get(i);
                    String apptNum = appt.getAppointmentNum();
                    String dateStr = retrieveAppointmentDateWithApptNum(apptNum);
                    String timeStr = retrieveAppointmentTimeWithApptNum(apptNum);
                    String apptSPName = appt.getServiceProviderEntity().getName();
                    String apptStatus = getApptStatus(appt.getId());

                    // Print records
                    System.out.printf("%-20s | %-20s | %-20s | %-20s | %s", apptNum, dateStr, timeStr, apptSPName, apptStatus);
                    System.out.println();
                }
            } else {
                System.out.println("There are currently no appointments.");
            }
            System.out.println();
        } catch (CustomerNotFoundException_Exception | AppointmentNotFoundException_Exception ex) {
            System.err.println("Error occurred when retrieving customer: " + ex.getMessage());
        }

        return haveAppts;
    }

    public void cancelAppointments() {
        Scanner sc = new Scanner(System.in);

        String appointmentNum;
        System.out.println("*** Customer terminal :: Cancel Appointment ***\n");
        boolean haveAppt = this.viewForCancellingAppt();

        if (haveAppt) {
            while (true) {

                while (true) {
                    System.out.println("Enter 0 to go back to previous menu");
                    System.out.print("Enter Appointment Number> ");
                    appointmentNum = sc.nextLine().trim();
                    if (appointmentNum.equals("0")) {
                        return;
                    }
                    if (appointmentNum.length() > 0) {
                        break;
                    }
                }

                try {
                    cancelAppointment(appointmentNum);
                    System.out.println("Appointment " + appointmentNum + " has been cancelled successfully.\n");
                    break;
                } catch (AppointmentNotFoundException_Exception | AppointmentCannotBeCancelledException_Exception ex) {
                    System.err.println("An error has occured when cancelling the appointment: " + ex.getMessage());
                }
            }
        }

    }

    public void rateServiceProvider() {
        Scanner sc = new Scanner(System.in);

        System.out.println("*** Customer Terminal :: Rate Service Provider ***\n");

        List<AppointmentEntity> apptsToRate = new ArrayList<>();

        try {
            List<AppointmentEntity> appts = retrieveCustomerEntityAppointments(this.loggedInCustomerEntity.getId());

            if (appts.isEmpty()) {
                System.out.println("There is no appointment to rate.\n");
            } else {
                Long spId;
                boolean haveApptWithThisSP = false;

                while (true) {
                    try {
                        System.out.println("Enter 0 to go back to previous menu");
                        System.out.print("Enter Service Provider Id> ");
                        spId = sc.nextLong(); // need to check if there is such sp
                        if (spId == 0) {
                            return;
                        }
                        retrieveServiceProviderByServiceProviderId(spId);
                        //if all retrievedSPId != spId no appt with this sp, try again
                        for (int i = 0; i < appts.size(); i++) {
                            AppointmentEntity apptEntity = appts.get(i);
                            Long retrievedSPId = apptEntity.getServiceProviderEntity().getServiceProviderId();
                            if (spId.equals(retrievedSPId)) { // if rating == 0 means not yet rated, rating != 0 means rated, dont rate again!
                                haveApptWithThisSP = true; // Will remain false if input spId does not have any matching appt with same spId
                                if (apptEntity.getRating() == 0) {
                                    apptsToRate.add(apptEntity);
                                }
                            }
                        }
                        if (!haveApptWithThisSP) {
                            System.err.println("You do not have any appointments with this Service Provider. Please try again.");
                            continue;
                        }
                        break;
                    } catch (InputMismatchException ex) {
                        System.err.println("Please input a number.");
                        sc.next();
                    } catch (ServiceProviderEntityNotFoundException_Exception ex) {
                        System.err.println("There is no such Service Provider. Please try again.");
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
                    System.out.println("You have rated all appointments.\n");
                } else {
                    Integer index;

                    while (true) {
                        try {
                            System.out.print("Enter index number of the appointment to rate> ");
                            index = sc.nextInt();
                            break;
                        } catch (InputMismatchException ex) {
                            System.err.println("Please input a number.");
                            sc.next();
                        }
                    }

                    while (index < 1 || index > apptsToRate.size()) {
                        while (true) {
                            try {
                                System.err.print("Invalid index. Enter index again> ");
                                index = sc.nextInt();
                                break;
                            } catch (InputMismatchException ex) {
                                System.err.println("Please input a number.");
                                sc.next();
                            }
                        }
                    }

                    Integer rating;
                    while (true) {
                        try {
                            System.out.print("Enter rating> ");
                            rating = sc.nextInt();
                            break;
                        } catch (InputMismatchException ex) {
                            System.err.println("Please input a digit.");
                            sc.next();
                        }
                    }

                    while (rating < 1 || rating > 5) {
                        while (true) {
                            try {
                                System.err.print("Invalid rating. Enter rating again> ");
                                rating = sc.nextInt();
                                break;
                            } catch (InputMismatchException ex) {
                                System.err.println("Please input a digit.");
                                sc.next();
                            }
                        }
                    }

                    System.out.println();
                    AppointmentEntity apptEntity = apptsToRate.get(index - 1);
                    rateAppointment(apptEntity.getId(), rating);
                    System.out.println("You have rated Appointment " + apptEntity.getAppointmentNum() + " a rating of " + rating + ".");
                }
            }

        } catch (CustomerNotFoundException_Exception | AppointmentNotFoundException_Exception ex) {
            System.err.println("An error has occured when retrieving the appointments: " + ex.getMessage());
        }

    }

    /**
     * *********************************************************** Web Services *************************************************************
     */
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

    private static void cancelAppointment(java.lang.String appointmentNum) throws AppointmentNotFoundException_Exception, AppointmentCannotBeCancelledException_Exception {
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

    private static String getApptStatus(java.lang.Long apptId) {
        ws.client.CustomerWebService_Service service = new ws.client.CustomerWebService_Service();
        ws.client.CustomerWebService port = service.getCustomerWebServicePort();
        return port.getApptStatus(apptId);
    }
}
