/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ServiceProviderEntity;
import util.exception.InvalidLoginCredentialException;
import util.exception.ServiceProviderAddressExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;


public interface ServiceProviderEntitySessionBeanRemote {
    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;
    public ServiceProviderEntity createServiceProviderEntity(ServiceProviderEntity newServiceProvider) throws ServiceProviderAddressExistException, UnknownPersistenceException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;
}
