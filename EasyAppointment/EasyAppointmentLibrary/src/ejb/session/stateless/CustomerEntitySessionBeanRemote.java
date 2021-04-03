package ejb.session.stateless;

import entity.CustomerEntity;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

public interface CustomerEntitySessionBeanRemote {
    
    public CustomerEntity createCustomerEntity(CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException;
    
    public CustomerEntity retrieveCustomerEntityById(Long customerId) throws CustomerNotFoundException;
    
    public CustomerEntity retrieveCustomerEntityByEmail(String email) throws CustomerNotFoundException;
            
    public CustomerEntity customerLogin(String email, Integer password) throws InvalidLoginCredentialException;
}
