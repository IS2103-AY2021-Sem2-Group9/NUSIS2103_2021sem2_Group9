package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
import util.exception.InvalidPasswordFormatException;
import util.exception.UnknownPersistenceException;
import util.password.PasswordEncrypt;


@Stateless
@Local(CustomerEntitySessionBeanLocal.class)
@Remote(CustomerEntitySessionBeanRemote.class)
public class CustomerEntitySessionBean implements CustomerEntitySessionBeanLocal, CustomerEntitySessionBeanRemote {
    
    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;
    
    private final PasswordEncrypt passwordEncrypt = new PasswordEncrypt();
    
    @Override
    public CustomerEntity createCustomerEntity(CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException, InvalidPasswordFormatException {
        try 
        {
            String unencryptedPassword = customerEntity.getPassword();
            if (unencryptedPassword.length() != 6)
            {
                throw new InvalidPasswordFormatException("Password length is not 6!");
            }
            else 
            {
                Integer intPassword = Integer.valueOf(unencryptedPassword);
                String salt = passwordEncrypt.getSalt(30);
                String encryptedPassword = passwordEncrypt.generateSecurePassword(unencryptedPassword, salt);
                customerEntity.setPassword(salt + encryptedPassword);
                em.persist(customerEntity);            
                em.flush(); 
            }   
            return customerEntity;   
        }
        catch (NumberFormatException ex)
        {
            throw new InvalidPasswordFormatException("Password can only be digits!");
        }
        catch (PersistenceException ex) 
        {
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
    public CustomerEntity customerLogin(String email, Integer password) throws InvalidLoginCredentialException 
    {
        try 
        {
            String stringPassword = password.toString();
            CustomerEntity customerEntity = retrieveCustomerEntityByEmail(email);
            String saltAndPassword = customerEntity.getPassword();
            String salt = saltAndPassword.substring(0, 30);
            String encryptedPassword = saltAndPassword.substring(31);
            Boolean passwordVerification = passwordEncrypt.verifyUserPassword(stringPassword, encryptedPassword, salt);
            if(passwordVerification) 
            {
                customerEntity.getAppointments().size();
                return customerEntity;
            }  
            else 
            {
                throw new InvalidLoginCredentialException("Email address does not exist or invalid password");
            }
        } 
        catch (CustomerNotFoundException ex) 
        {
            throw new InvalidLoginCredentialException("Invalid Login: " + ex.getMessage());
        }
    }
    
    @Override
    public void deleteCustomerEntity(Long CustomerId) throws CustomerNotFoundException, DeleteCustomerException
    {
        CustomerEntity CustomerEntityToRemove = retrieveCustomerEntityById(CustomerId);
        em.remove(CustomerEntityToRemove);
    }
    
    
    @Override
    public List<AppointmentEntity> retrieveCustomerEntityAppointments(Long customerId) throws CustomerNotFoundException
    {
            CustomerEntity customerEntity = retrieveCustomerEntityById(customerId);
            List<AppointmentEntity> appointments = customerEntity.getAppointments();
            appointments.size();
            return appointments;
    }
    
    public List<AppointmentEntity> retrieveCustomerEntityUpcomingAppointments(Long customerId) throws CustomerNotFoundException
    {
        List<AppointmentEntity> allAppointments = retrieveCustomerEntityAppointments(customerId); 
        List<AppointmentEntity> result = new ArrayList<>();
        for(AppointmentEntity appt : allAppointments) {
            LocalTime now = LocalTime.now(); 
            LocalTime time = appt.getAppointmentTime();
            Long timeDiff = ChronoUnit.HOURS.between(now, time) + ChronoUnit.MINUTES.between(now, time);
            System.out.println(timeDiff);
            int compare = appt.getAppointmentDate().compareTo(LocalDate.now());
            System.out.println(compare);
            if(appt.getRating() == 0 && compare > 0 ) {
                result.add(appt);
            } else if (appt.getRating() == 0 && compare == 0 && timeDiff > 0) {
                result.add(appt);
            }
        }
        return result;
    }
}
