package ejb.session.stateless;

import Enumeration.ServiceProviderStatus;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import entity.AppointmentEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;

@Stateless
@Local(ServiceProviderEntitySessionBeanLocal.class)
@Remote(ServiceProviderEntitySessionBeanRemote.class)
public class ServiceProviderEntitySessionBean implements ServiceProviderEntitySessionBeanRemote, ServiceProviderEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;
            
    @EJB
    private BusinessCategorySessionBeanLocal businessCategorySessionBeanLocal;
    
    @Override
    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider, int category) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, UnknownPersistenceException 
    {
        try 
        {
            List<BusinessCategoryEntity> categoryList = businessCategorySessionBeanLocal.retrieveAllBusinessCategories();
            for (BusinessCategoryEntity categoryEntity : categoryList) {
                long matchEntry = Long.valueOf(category);
                if(categoryEntity.getId() == matchEntry) {
                    newServiceProvider.setCategory(categoryEntity);
                    break; 
                } else {
                    throw new BusinessCategoryNotFoundException("Business Category Not Found");
                }
            }
            em.persist(newServiceProvider);
            em.flush();
            return newServiceProvider;
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new BusinessCategoryNotFoundException("Error! Businesss category cannot be found!");
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
        
    }
            
            
    @Override
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.email = :inEmail");
        query.setParameter("inEmail", email);
        try {
            return (ServiceProviderEntity)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new ServiceProviderEntityNotFoundException("Service Provider email " + email + " does not exist!");
        }
    }
   
   
    @Override
    public ServiceProviderEntity serviceProviderLogin(String email, Integer password) throws InvalidLoginCredentialException {
        try {
            ServiceProviderEntity currentServiceProviderEntity = retrieveServiceProviderByServiceProviderAddress(email);
            if(currentServiceProviderEntity.getPassword().equals(password) && currentServiceProviderEntity.getStatus() == ServiceProviderStatus.APPROVED) {
                return currentServiceProviderEntity;

            }  else if (currentServiceProviderEntity.getPassword().equals(password) && currentServiceProviderEntity.getStatus() == ServiceProviderStatus.PENDING) {
                throw new InvalidLoginCredentialException("Your account is still pending administrator's approval. Please try again later!");
            } else if (currentServiceProviderEntity.getPassword().equals(password) && currentServiceProviderEntity.getStatus() == ServiceProviderStatus.BLOCKED) {
                throw new InvalidLoginCredentialException("Your account has been blocked by an administrator.");
            } else {
                throw new InvalidLoginCredentialException("Email address does not exist or invalid password.");
            }
        } catch (ServiceProviderEntityNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid Login : " + ex.getMessage());
        }
    }

    @Override
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException {
        ServiceProviderEntity serviceProviderEntity = em.find(ServiceProviderEntity.class, serviceProviderId);
        if(serviceProviderEntity != null) {
            return serviceProviderEntity;
        }
        else {
            throw new ServiceProviderEntityNotFoundException("Service Provier ID " + serviceProviderId + " does not exist");
        }
    }
    
    @Override
    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException {
        if(serviceProviderEntity.getServiceProviderId() != null) {
            ServiceProviderEntity serviceProviderEntityToUpdate = retrieveServiceProviderByServiceProviderId(serviceProviderEntity.getServiceProviderId());
            
            if(serviceProviderEntityToUpdate.getEmail().equals(serviceProviderEntity.getEmail())) {
                serviceProviderEntityToUpdate.setName(serviceProviderEntity.getName());
                serviceProviderEntityToUpdate.setCategory(serviceProviderEntity.getCategory());
                serviceProviderEntityToUpdate.setUen(serviceProviderEntity.getUen());
                serviceProviderEntityToUpdate.setCity(serviceProviderEntity.getCity());
                serviceProviderEntityToUpdate.setPhoneNumber(serviceProviderEntity.getPhoneNumber());
                serviceProviderEntityToUpdate.setAddress(serviceProviderEntity.getAddress());
                serviceProviderEntityToUpdate.setPassword(serviceProviderEntity.getPassword());

            } else {
                throw new UpdateServiceProviderException("Username of service provider to be updated does not match the existing record");
            }
        } else {
            throw new ServiceProviderEntityNotFoundException("Service Provider does not exist!");   
        }   
    }
    
    @Override
    public List<ServiceProviderEntity> retrieveAllServiceProviders()
    {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s");
        
        return query.getResultList();
    }
    
    @Override
    public List<ServiceProviderEntity> retrieveServiceProvidersByStatus(ServiceProviderStatus status)
    {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.status = :currStatus");
        query.setParameter("currStatus", status);
        
        return query.getResultList();
    }
    
    @Override
    public String approveServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyApprovedException
    {
        ServiceProviderEntity sp = retrieveServiceProviderByServiceProviderId(id);
        if (sp.getStatus() == ServiceProviderStatus.APPROVED) 
        {
            throw new ServiceProviderAlreadyApprovedException("Service Provider has already been approved!");
        } 
        else
        {
            sp.setStatus(ServiceProviderStatus.APPROVED);
        }
        
        return sp.getName();
    }
    
    @Override
    public String blockServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyBlockedException
    {
        ServiceProviderEntity sp = retrieveServiceProviderByServiceProviderId(id);
        if (sp.getStatus() == ServiceProviderStatus.BLOCKED) 
        {
            throw new ServiceProviderAlreadyBlockedException("Service Provider has already been blocked!");
        } 
        else
        {
            sp.setStatus(ServiceProviderStatus.BLOCKED);
        }
        
        return sp.getName();
    }
    
    @Override
    public List<ServiceProviderEntity> retrieveAllAvailableServiceProvidersForTheDay(LocalDate appointmentDate, Long category, String city) throws BusinessCategoryNotFoundException {
        BusinessCategoryEntity bcEntity = businessCategorySessionBeanLocal.retrieveBusinessCategoryById(category); 
        Query query = em.createQuery("SELECT sp FROM ServiceProviderEntity sp WHERE sp.category = :bcEntity AND sp.city = :city");
        query.setParameter("bcEntity", bcEntity); 
        query.setParameter("city", city);

        List<ServiceProviderEntity> results = query.getResultList(); 
        List<ServiceProviderEntity> availableServiceProviders = new ArrayList<>(); 

        for(ServiceProviderEntity serviceProvider : results) {
            List<LocalTime> serviceProviderAvailability = retrieveServiceProviderAvailabilityForTheDay(serviceProvider, appointmentDate); 

            if (!serviceProviderAvailability.isEmpty()) {
                availableServiceProviders.add(serviceProvider);
            }
        }
        return availableServiceProviders;
    }
    
    @Override
    public List<LocalTime> retrieveServiceProviderAvailabilityForTheDay(ServiceProviderEntity spEntity, LocalDate appointmentDate) {
        
        LocalTime[] timeSlots = {LocalTime.of(8, 30, 00), LocalTime.of(9, 30, 00), LocalTime.of(10, 30, 00), LocalTime.of(11, 30, 00), LocalTime.of(12, 30, 00),
                                 LocalTime.of(13, 30, 00), LocalTime.of(14, 30, 00), LocalTime.of(15, 30, 00), LocalTime.of(16, 30, 00), LocalTime.of(17, 30, 00)};
        
        List<LocalTime> workingTimeSlots = Arrays.asList(timeSlots);
        List<LocalTime> availableTimeSlots = new ArrayList<>();
        
        List<AppointmentEntity> apptEntities = spEntity.getAppointmentEntities();
        
        for(AppointmentEntity appointment : apptEntities) {
            if (appointment.getAppointmentDate().equals(appointmentDate)) { 
                for(LocalTime time : workingTimeSlots) {
                    if (!time.equals(appointment.getAppointmentTime())) {
                        availableTimeSlots.add(time);
                    }
                }
            }
        }
        return availableTimeSlots;        
    }
 
    @Override
    public void addAppointment(AppointmentEntity appt, ServiceProviderEntity spEntity) {
        spEntity.getAppointmentEntities().add(appt);
        em.merge(spEntity);
    }
}