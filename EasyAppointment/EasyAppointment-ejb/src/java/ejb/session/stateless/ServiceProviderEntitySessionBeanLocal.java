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
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;

public interface ServiceProviderEntitySessionBeanLocal {

    public ServiceProviderEntity serviceProviderLogin(String address, Integer password) throws InvalidLoginCredentialException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException;

    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider, Long categoryId) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, UnknownPersistenceException;

    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;

    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException;

    public List<ServiceProviderEntity> retrieveAllServiceProviders();

    public List<ServiceProviderEntity> retrieveServiceProvidersByStatus(ServiceProviderStatus status);

    public String approveServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyApprovedException;

    public String blockServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyBlockedException;

    public List<ServiceProviderEntity> retrieveAllAvailableServiceProvidersForTheDay(LocalDate appointmentDate, Long category, String city) throws BusinessCategoryNotFoundException;

    public List<LocalTime> retrieveServiceProviderAvailabilityForTheDay(ServiceProviderEntity spEntity, LocalDate appointmentDate);

    public void addAppointment(AppointmentEntity appt, ServiceProviderEntity spEntity);

    public List<AppointmentEntity> retrieveAppointmentsOfServiceProviderById(Long serviceProviderId) throws ServiceProviderEntityNotFoundException;
  
    public double generateOverallRating(ServiceProviderEntity spEntity);
    
}
