package ejb.session.stateless;

import entity.ServiceProviderEntity;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderAddressExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;

public interface ServiceProviderEntitySessionBeanLocal {

    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;

    public ServiceProviderEntity createServiceProviderEntity(ServiceProviderEntity newServiceProvider) throws ServiceProviderAddressExistException, UnknownPersistenceException;
    
}
