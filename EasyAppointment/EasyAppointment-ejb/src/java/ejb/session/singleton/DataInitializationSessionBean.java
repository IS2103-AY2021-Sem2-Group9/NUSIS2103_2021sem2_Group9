package ejb.session.singleton;

import Enumeration.ServiceProviderStatus;
import ejb.session.stateless.AdminEntitySessionBeanLocal;
import ejb.session.stateless.AppointmentEntitySessionBeanLocal;
import ejb.session.stateless.BusinessCategorySessionBeanLocal;
import ejb.session.stateless.ServiceProviderEntitySessionBeanLocal;
import entity.AdminEntity;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
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
            List<Boolean> availability =new ArrayList<Boolean>(Arrays.asList(new Boolean[10]));
            Collections.fill(availability, Boolean.TRUE);
            adminEntitySessionBeanLocal.createAdminEntity(new AdminEntity("terry@easyadmin.com", "password", "Terry", "Tan"));
            adminEntitySessionBeanLocal.createAdminEntity(new AdminEntity("1", "1", "Test", "Test"));
            businessCategorySessionBeanLocal.createBusinessCategoryEntity(new BusinessCategoryEntity("Health"));
            businessCategorySessionBeanLocal.createBusinessCategoryEntity(new BusinessCategoryEntity("Fashion"));
            businessCategorySessionBeanLocal.createBusinessCategoryEntity(new BusinessCategoryEntity("Education"));
            
            serviceProviderEntitySessionBeanLocal.registerNewServiceProvider(new ServiceProviderEntity("Kevin Paterson", "1111001111", "Clementi", "93718799", "13, Clementi Road", "kevin@nuh.com.sg", 113322, ServiceProviderStatus.APPROVED), 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
}
