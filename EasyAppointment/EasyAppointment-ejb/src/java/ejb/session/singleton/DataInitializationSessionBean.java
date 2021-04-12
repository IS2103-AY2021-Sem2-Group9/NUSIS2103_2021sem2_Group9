package ejb.session.singleton;

import Enumeration.AppointmentStatusEnum;
import Enumeration.ServiceProviderStatus;
import ejb.session.stateless.AdminEntitySessionBeanLocal;
import ejb.session.stateless.AppointmentEntitySessionBeanLocal;
import ejb.session.stateless.BusinessCategorySessionBeanLocal;
import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import ejb.session.stateless.ServiceProviderEntitySessionBeanLocal;
import entity.AdminEntity;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import entity.CustomerEntity;
import entity.AppointmentEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.exception.AdminNotFoundException;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup
public class DataInitializationSessionBean {

    @EJB
    private AdminEntitySessionBeanLocal adminEntitySessionBeanLocal;
    @EJB 
    private ServiceProviderEntitySessionBeanLocal serviceProviderEntitySessionBeanLocal;
    @EJB 
    private BusinessCategorySessionBeanLocal businessCategorySessionBeanLocal;
    @EJB
    private AppointmentEntitySessionBeanLocal appointmentEntitySessionBeanLocal;
    @EJB 
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;
    
    public DataInitializationSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        try
        {
            adminEntitySessionBeanLocal.retrieveAdminEntityByAdminId(Long.valueOf(1));
        }
        catch(AdminNotFoundException ex)
        {
            initializeData();
        }
    }
    
    public void initializeData() 
    {
        try {
            adminEntitySessionBeanLocal.createAdminEntity(new AdminEntity("terry@easyadmin.com", "654321", "Terry", "Tan"));
            adminEntitySessionBeanLocal.createAdminEntity(new AdminEntity("1", "123456", "Test", "Test"));
            businessCategorySessionBeanLocal.createBusinessCategoryEntity(new BusinessCategoryEntity("Health"));
            businessCategorySessionBeanLocal.createBusinessCategoryEntity(new BusinessCategoryEntity("Fashion"));
            businessCategorySessionBeanLocal.createBusinessCategoryEntity(new BusinessCategoryEntity("Education"));
            
            CustomerEntity testCustomer = new CustomerEntity("S9898100B", "test@gmail.com", "123456", "John", "Doe", "M", 21, "97381199", "123A Temasek Hall", "Singapore");
            CustomerEntity newCustomer = new CustomerEntity("S9892011A", "1", "123456", "Test", "Customer", "M", 21, "90000009", "321A Eusoff Hall", "Clementi");
            customerEntitySessionBeanLocal.createCustomerEntity(testCustomer);
            customerEntitySessionBeanLocal.createCustomerEntity(newCustomer);
            
            LocalDate testDate = LocalDate.of(2021,04,06);
            LocalTime testTime = LocalTime.of(10, 30);
            ServiceProviderEntity testSP = new ServiceProviderEntity("1", "1", "1", "1", "1", "1", "123456", ServiceProviderStatus.APPROVED);
            Long categoryId = 1L;
            serviceProviderEntitySessionBeanLocal.registerNewServiceProvider(testSP, categoryId);
            serviceProviderEntitySessionBeanLocal.registerNewServiceProvider(new ServiceProviderEntity("Kevin Paterson", "1111001111", "Clementi", "93718799", "13, Clementi Road", "kevin@nuh.com.sg", "654321", ServiceProviderStatus.APPROVED), categoryId);
            AppointmentEntity testAppointment = new AppointmentEntity(testDate, testTime, newCustomer, testSP);
            appointmentEntitySessionBeanLocal.createAppointmentEntity(testAppointment); 
            serviceProviderEntitySessionBeanLocal.addAppointment(testAppointment, testSP);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
}
