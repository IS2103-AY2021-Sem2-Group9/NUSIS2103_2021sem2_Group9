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
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.AppointmentExistException;
import util.exception.AppointmentNotFoundException;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
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

    @EJB(name = "BusinessCategorySessionBeanLocal")
    private BusinessCategorySessionBeanLocal businessCategorySessionBeanLocal;
    
    

    @WebMethod(operationName = "createCustomerEntity")
    public CustomerEntity createCustomerEntity(@WebParam(name = "customerEntity") CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException
    {
        return customerEntitySessionBeanLocal.createCustomerEntity(customerEntity);
    }
    
    @WebMethod(operationName = "customerLogin")
    public CustomerEntity customerLogin(@WebParam(name = "email") String email, 
                                        @WebParam(name = "password") Integer password) throws InvalidLoginCredentialException 
    {
        return customerEntitySessionBeanLocal.customerLogin(email, password);
    }
    
    @WebMethod(operationName = "retrieveCustomerEntityById")
    public CustomerEntity retrieveCustomerEntityById(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException 
    {
        return customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerId);
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
        return this.serviceProviderEntitySessionBeanLocal.retrieveAllAvailableServiceProvidersForTheDay(apptDate, category, city);
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
        return this.serviceProviderEntitySessionBeanLocal.retrieveServiceProviderByServiceProviderId(serviceProviderId);
    }
    
    @WebMethod(operationName = "createAppointmentEntity")
    public AppointmentEntity createAppointmentEntity(@WebParam(name = "appointmentDate") String apptDate, @WebParam(name = "apptTime") String apptTime, 
                                                        @WebParam(name = "customerId") Long customerId, 
                                                        @WebParam(name = "spId") Long spId) throws UnknownPersistenceException, AppointmentExistException, CustomerNotFoundException, ServiceProviderEntityNotFoundException {
        ServiceProviderEntity spEntity = this.serviceProviderEntitySessionBeanLocal.retrieveServiceProviderByServiceProviderId(spId);
        CustomerEntity customerEntity = this.customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerId);
        AppointmentEntity apptEntity = new AppointmentEntity(LocalDate.parse(apptDate), LocalTime.parse(apptTime), customerEntity, spEntity);
        return this.appointmentEntitySessionBeanLocal.createAppointmentEntity(apptEntity);
    }
    
    @WebMethod(operationName = "addAppointment")
    public void addAppointment(@WebParam(name = "appt") AppointmentEntity appt, @WebParam(name = "spEntity") ServiceProviderEntity spEntity) {
        this.serviceProviderEntitySessionBeanLocal.addAppointment(appt, spEntity);
    }
    
    @WebMethod(operationName = "retrieveCustomerEntityAppointments")
    public List<AppointmentEntity> retrieveCustomerEntityAppointments(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException {
        return this.customerEntitySessionBeanLocal.retrieveCustomerEntityAppointments(customerId);
    }
    
    @WebMethod(operationName = "cancelAppointment")
    public void cancelAppointment(@WebParam(name = "appointmentNum") String appointmentNum) throws AppointmentNotFoundException {
        this.appointmentEntitySessionBeanLocal.cancelAppointment(appointmentNum);
    }
    
    @WebMethod(operationName = "rateAppointment")
    public void rateAppointment(@WebParam(name = "appointmentEntity") AppointmentEntity appointmentEntity) {
        this.appointmentEntitySessionBeanLocal.rateAppointment(appointmentEntity);
    }
}
