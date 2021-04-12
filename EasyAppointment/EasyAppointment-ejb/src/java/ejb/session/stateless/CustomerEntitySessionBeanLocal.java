package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import java.util.List;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.DeleteCustomerException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
import util.exception.UnknownPersistenceException;

public interface CustomerEntitySessionBeanLocal {
    public CustomerEntity createCustomerEntity(CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException, InvalidPasswordFormatException;
    
    public CustomerEntity retrieveCustomerEntityById(Long customerId) throws CustomerNotFoundException;
    
    public CustomerEntity retrieveCustomerEntityByEmail(String email) throws CustomerNotFoundException;
    
    public CustomerEntity customerLogin(String email, Integer password) throws InvalidLoginCredentialException;

    public void deleteCustomerEntity(Long CustomerId) throws CustomerNotFoundException, DeleteCustomerException;

    public List<AppointmentEntity> retrieveCustomerEntityAppointments(Long customerId) throws CustomerNotFoundException;

    public List<AppointmentEntity> retrieveCustomerEntityUpcomingAppointments(Long customerId) throws CustomerNotFoundException;
}
