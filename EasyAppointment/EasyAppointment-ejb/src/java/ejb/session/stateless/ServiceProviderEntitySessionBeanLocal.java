package ejb.session.stateless;

import entity.ServiceProviderEntity;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;

public interface ServiceProviderEntitySessionBeanLocal {

    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;

    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, UnknownPersistenceException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;

    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException;
    
}
