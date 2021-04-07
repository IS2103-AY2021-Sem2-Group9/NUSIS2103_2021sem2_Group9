package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.DeleteCustomerException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;


@Stateless
@Local(CustomerEntitySessionBeanLocal.class)
@Remote(CustomerEntitySessionBeanRemote.class)
public class CustomerEntitySessionBean implements CustomerEntitySessionBeanLocal, CustomerEntitySessionBeanRemote {
    
    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;
    
    @Override
    public CustomerEntity createCustomerEntity(CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException {
        try {
            System.out.println("FIRSTLINE");
            em.persist(customerEntity);
            System.out.println("PERSISTED");
            em.flush();
            System.out.println("FLUSHED");
            return customerEntity;
            
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) { // A database-related exception
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) { // To get the internal error
                    throw new CustomerExistException("ID Number/Contact Number/Username already exists!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @Override
    public CustomerEntity retrieveCustomerEntityById(Long customerId) throws CustomerNotFoundException {
        CustomerEntity customerEntity = em.find(CustomerEntity.class, customerId);
        
        if (customerEntity != null) {
            return customerEntity;
        } else {
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist!\n");
        }
    }
    
    @Override
    public CustomerEntity retrieveCustomerEntityByEmail(String email) throws CustomerNotFoundException {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.email = :email");
        query.setParameter("email", email);
        
        try {
            return (CustomerEntity)query.getSingleResult();
        } catch (NoResultException ex) {
            throw new CustomerNotFoundException("Customer email " + email + " does not exist!\n");
        }
    }
    
    @Override
    public CustomerEntity customerLogin(String email, Integer password) throws InvalidLoginCredentialException {
        
        try {
            CustomerEntity customerEntity = retrieveCustomerEntityByEmail(email);
            if(customerEntity.getPassword().equals(password)) {
                return customerEntity;
                
            }  else {
                throw new InvalidLoginCredentialException("Email address does not exist or invalid password");
            }
            
           
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid Login: " + ex.getMessage());
        }
    }
    
    @Override
    public void deleteCustomerEntity(Long CustomerId) throws CustomerNotFoundException, DeleteCustomerException
    {
        CustomerEntity CustomerEntityToRemove = retrieveCustomerEntityById(CustomerId);
        
        if(CustomerEntityToRemove.getAppointments().isEmpty())
        {
            em.remove(CustomerEntityToRemove);
        }
        else
        {
            throw new DeleteCustomerException("Customer ID " + CustomerId + " is associated with existing sale transaction(s) and cannot be deleted!");
        }
    }
    
    
    @Override
    public List<AppointmentEntity> retrieveCustomerEntityAppointments(Long customerId) throws CustomerNotFoundException
    {
            CustomerEntity customerEntity = retrieveCustomerEntityById(customerId);
            List<AppointmentEntity> appointments = customerEntity.getAppointments();
            appointments.size();
            return appointments;
    }
}
