/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Enumeration.ServiceProviderStatus;
import entity.ServiceProviderEntity;
import java.util.List;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;


public interface ServiceProviderEntitySessionBeanRemote {
    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;
    
    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider, int category) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, UnknownPersistenceException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;
    
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;

    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException;
    
    public List<ServiceProviderEntity> retrieveAllServiceProviders();
    
    public List<ServiceProviderEntity> retrieveServiceProvidersByStatus(ServiceProviderStatus status);
    
    public String approveServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyApprovedException;
    
    public String blockServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyBlockedException;
}
