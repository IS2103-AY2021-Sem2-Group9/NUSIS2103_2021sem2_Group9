/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ServiceProviderEntity;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;


public interface ServiceProviderEntitySessionBeanRemote {
    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;
    
    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, UnknownPersistenceException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;
    
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;

    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException;
}
