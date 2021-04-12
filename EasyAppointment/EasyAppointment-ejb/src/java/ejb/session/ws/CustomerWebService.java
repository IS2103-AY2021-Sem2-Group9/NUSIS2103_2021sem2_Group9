package ejb.session.ws;

import ejb.session.stateless.AppointmentEntitySessionBeanLocal;
import ejb.session.stateless.BusinessCategorySessionBeanLocal;
import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import ejb.session.stateless.ServiceProviderEntitySessionBeanLocal;
import entity.AppointmentEntity;
import entity.BusinessCategoryEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AppointmentCannotBeCancelledException;
import util.exception.AppointmentExistException;
import util.exception.AppointmentNotCompletedException;
import util.exception.AppointmentNotFoundException;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;

@WebService(serviceName = "CustomerWebService")
@Stateless
public class CustomerWebService {

    @EJB(name = "AppointmentEntitySessionBeanLocal")
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal;

    @EJB(name = "ServiceProviderEntitySessionBeanLocal")
    private ServiceProviderEntitySessionBeanLocal serviceProviderEntitySessionBeanLocal;

    @EJB(name = "CustomerEntitySessionBeanLocal")
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;

    @EJB(name = "BusinessCategorySessionBeanLocal")
    private BusinessCategorySessionBeanLocal businessCategorySessionBeanLocal;

    @WebMethod(operationName = "createCustomerEntity")
    public CustomerEntity createCustomerEntity(@WebParam(name = "customerEntity") CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException, InvalidPasswordFormatException {
        return customerEntitySessionBeanLocal.createCustomerEntity(customerEntity);
    }

    @WebMethod(operationName = "customerLogin")
    public CustomerEntity customerLogin(@WebParam(name = "email") String email,
            @WebParam(name = "password") Integer password) throws InvalidLoginCredentialException {
        CustomerEntity cust = customerEntitySessionBeanLocal.customerLogin(email, password);

        // Marshalling
        em.detach(cust);
        for (AppointmentEntity appt : cust.getAppointments()) {
            em.detach(appt);
            appt.setCustomerEntity(null);
            em.detach(appt.getServiceProviderEntity());
            appt.getServiceProviderEntity().getAppointmentEntities().clear();
        }
        return cust;
    }

    @WebMethod(operationName = "retrieveCustomerEntityById")
    public CustomerEntity retrieveCustomerEntityById(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException {
        CustomerEntity cust = customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerId);

        // Marshalling
        em.detach(cust);
        for (AppointmentEntity appt : cust.getAppointments()) {
            em.detach(appt);
            appt.setCustomerEntity(null);
            em.detach(appt.getServiceProviderEntity());
            appt.getServiceProviderEntity().getAppointmentEntities().clear();
        }
        return cust;
    }

    @WebMethod(operationName = "retrieveAllBusinessCategories")
    public List<BusinessCategoryEntity> retrieveAllBusinessCategories() {
        return this.businessCategorySessionBeanLocal.retrieveAllBusinessCategories();
    }

    @WebMethod(operationName = "retrieveAllAvailableServiceProvidersForTheDay")
    public List<ServiceProviderEntity> retrieveAllAvailableServiceProvidersForTheDay(@WebParam(name = "appointmentDate") String appointmentDate,
            @WebParam(name = "category") Long category,
            @WebParam(name = "city") String city) throws BusinessCategoryNotFoundException {
        LocalDate apptDate = LocalDate.parse(appointmentDate);
        List<ServiceProviderEntity> spList = this.serviceProviderEntitySessionBeanLocal.retrieveAllAvailableServiceProvidersForTheDay(apptDate, category, city);

        // Marshalling
        for (ServiceProviderEntity spEntity : spList) {
            em.detach(spEntity);
            for (AppointmentEntity appt : spEntity.getAppointmentEntities()) {
                em.detach(appt);
                appt.setServiceProviderEntity(null);
                em.detach(appt.getCustomerEntity());
                appt.getCustomerEntity().getAppointments().clear();
            }
        }

        return spList;
    }

    @WebMethod(operationName = "retrieveServiceProviderAvailabilityForTheDay")
    public List<String> retrieveServiceProviderAvailabilityForTheDay(@WebParam(name = "spEntity") ServiceProviderEntity spEntity,
            @WebParam(name = "appointmentDate") String appointmentDate) {
        LocalDate apptDate = LocalDate.parse(appointmentDate);
        List<LocalTime> timeslotList = this.serviceProviderEntitySessionBeanLocal.retrieveServiceProviderAvailabilityForTheDay(spEntity, apptDate);
        List<String> timeslotStrList = new ArrayList<>();
        for (int i = 0; i < timeslotList.size(); i++) {
            timeslotStrList.add(timeslotList.get(i).toString());
        }
        return timeslotStrList;
    }

    @WebMethod(operationName = "generateOverallRating")
    public double generateOverallRating(@WebParam(name = "spEntity") ServiceProviderEntity spEntity) {
        return this.serviceProviderEntitySessionBeanLocal.generateOverallRating(spEntity);
    }

    @WebMethod(operationName = "retrieveServiceProviderByServiceProviderId")
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(@WebParam(name = "serviceProviderId") Long serviceProviderId) throws ServiceProviderEntityNotFoundException {
        ServiceProviderEntity spEntity = this.serviceProviderEntitySessionBeanLocal.retrieveServiceProviderByServiceProviderId(serviceProviderId);

        // Marshalling
        em.detach(spEntity);
        for (AppointmentEntity appt : spEntity.getAppointmentEntities()) {
            em.detach(appt);
            appt.setServiceProviderEntity(null);
            em.detach(appt.getCustomerEntity());
            appt.getCustomerEntity().getAppointments().clear();
        }

        return spEntity;
    }

    @WebMethod(operationName = "createAppointmentEntity")
    public AppointmentEntity createAppointmentEntity(@WebParam(name = "appointmentDate") String apptDate, @WebParam(name = "apptTime") String apptTime,
            @WebParam(name = "customerId") Long customerId,
            @WebParam(name = "spId") Long spId) throws UnknownPersistenceException, AppointmentExistException, CustomerNotFoundException, ServiceProviderEntityNotFoundException {
        ServiceProviderEntity spEntity = this.serviceProviderEntitySessionBeanLocal.retrieveServiceProviderByServiceProviderId(spId);
        CustomerEntity customerEntity = this.customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerId);
        AppointmentEntity apptEntity = new AppointmentEntity(LocalDate.parse(apptDate), LocalTime.parse(apptTime), customerEntity, spEntity);

        AppointmentEntity appt = this.appointmentEntitySessionBeanLocal.createAppointmentEntity(apptEntity);
        return appt;
    }

    @WebMethod(operationName = "addAppointment")
    public void addAppointment(@WebParam(name = "dateStr") String dateStr, @WebParam(name = "timeStr") String timeStr,
            @WebParam(name = "apptNum") String apptNum, @WebParam(name = "spId") Long spId) throws ServiceProviderEntityNotFoundException, CustomerNotFoundException, AppointmentNotFoundException {

        ServiceProviderEntity spEntity = this.serviceProviderEntitySessionBeanLocal.retrieveServiceProviderByServiceProviderId(spId);

        AppointmentEntity appt = this.appointmentEntitySessionBeanLocal.retrieveAppointmentByAppointmentNum(apptNum);
        List<AppointmentEntity> appts = spEntity.getAppointmentEntities();
        appts.add(appt);
        spEntity.setAppointmentEntities(appts);
    }

    @WebMethod(operationName = "retrieveCustomerEntityAppointments")
    public List<AppointmentEntity> retrieveCustomerEntityAppointments(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException {
        List<AppointmentEntity> appts = this.customerEntitySessionBeanLocal.retrieveCustomerEntityAppointments(customerId);
        return appts;
    }

    @WebMethod(operationName = "cancelAppointment")
    public void cancelAppointment(@WebParam(name = "appointmentNum") String appointmentNum) throws AppointmentNotFoundException, AppointmentCannotBeCancelledException {
        this.appointmentEntitySessionBeanLocal.cancelAppointment(appointmentNum);
    }

    @WebMethod(operationName = "rateAppointment")
    public void rateAppointment(@WebParam(name = "apptEntityId") long apptEntityId, @WebParam(name = "rating") int rating) throws AppointmentNotCompletedException {
        this.appointmentEntitySessionBeanLocal.rateAppointment(apptEntityId, rating);
    }

    @WebMethod(operationName = "retrieveAppointmentDateWithApptNum")
    public String retrieveAppointmentDateWithApptNum(@WebParam(name = "apptNum") String apptNum) throws AppointmentNotFoundException {
        return this.appointmentEntitySessionBeanLocal.retrieveAppointmentDateWithApptNum(apptNum);
    }

    @WebMethod(operationName = "retrieveAppointmentTimeWithApptNum")
    public String retrieveAppointmentTimeWithApptNum(@WebParam(name = "apptNum") String apptNum) throws AppointmentNotFoundException {
        return this.appointmentEntitySessionBeanLocal.retrieveAppointmentTimeWithApptNum(apptNum);
    }

    @WebMethod(operationName = "getApptStatus")
    public String getApptStatus(@WebParam(name = "apptId") Long apptId) {
        AppointmentEntity appt = em.find(AppointmentEntity.class, apptId);
        
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        LocalDate apptDate = appt.getAppointmentDate();
        LocalTime apptTime = appt.getAppointmentTime();
        
        int compare = apptDate.compareTo(currentDate);
        double hourDiff = Math.floor(ChronoUnit.HOURS.between(currentTime, apptTime) + ChronoUnit.MINUTES.between(currentTime, apptTime)/60);
        
        if(compare > 0 ) { 
            return "UPCOMING";
        } else if (compare == 0 && hourDiff > 0) {
            return "UPCOMING"; 
        } else if (compare == 0 && hourDiff >= -1 && hourDiff <= 0) {
            return "ONGOING";
        } else {
            return "COMPLETED";
        }
    }
}
