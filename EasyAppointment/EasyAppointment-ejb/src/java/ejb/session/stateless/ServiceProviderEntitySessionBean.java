package ejb.session.stateless;

import Enumeration.ServiceProviderStatus;
import entity.ServiceProviderEntity;
import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
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

    @Override
    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider) throws ServiceProviderEmailExistException, UnknownPersistenceException {
        try {
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
                    throw new ServiceProviderEmailExistException("Try again, service provider email address exists");
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
            throw new InvalidLoginCredentialException("Email address does not exist or invalid password");
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
    
}