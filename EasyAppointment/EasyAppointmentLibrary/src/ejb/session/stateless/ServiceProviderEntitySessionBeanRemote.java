/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Enumeration.ServiceProviderStatus;
import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;


public interface ServiceProviderEntitySessionBeanRemote 
{
    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;
    
    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider, Long categoryId) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, InvalidPasswordFormatException, UnknownPersistenceException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;
    
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;

    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException, InvalidPasswordFormatException;
    
    public List<ServiceProviderEntity> retrieveAllServiceProviders();
    
    public List<ServiceProviderEntity> retrieveServiceProvidersByStatus(ServiceProviderStatus status);
    
    public String approveServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyApprovedException;
    
    public String blockServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyBlockedException;
    
    public List<ServiceProviderEntity> retrieveAllAvailableServiceProvidersForTheDay(LocalDate appointmentDate, Long category, String city) throws BusinessCategoryNotFoundException;

    public List<LocalTime> retrieveServiceProviderAvailabilityForTheDay(ServiceProviderEntity spEntity, LocalDate appointmentDate);
    
    public void addAppointment(AppointmentEntity appt, ServiceProviderEntity spEntity);
    
    public List<AppointmentEntity> retrieveUnratedAppointmentsForServiceProvider(ServiceProviderEntity serviceProviderEntity);

    public List<AppointmentEntity> retrieveAllAppointmentsForServiceProvider(ServiceProviderEntity serviceProviderEntity);
         
    public List<AppointmentEntity> retrieveAppointmentsOfServiceProviderById(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;

    public double generateOverallRating(ServiceProviderEntity spEntity);
    
    public List<AppointmentEntity> retrieveUpcomingAppointmentsForServiceProvider(ServiceProviderEntity serviceProviderEntity);

    public boolean checkEmail(String email);
    
    public boolean checkUen(String uen);
    
    public boolean checkPhoneNumber(String phone);
}
