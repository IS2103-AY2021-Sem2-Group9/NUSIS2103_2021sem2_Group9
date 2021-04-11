package ejb.session.stateless;

import Enumeration.ServiceProviderStatus;
import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import entity.AppointmentEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.BusinessCategoryNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
import util.exception.ServiceProviderAlreadyApprovedException;
import util.exception.ServiceProviderAlreadyBlockedException;
import util.exception.ServiceProviderEmailExistException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateServiceProviderException;
import util.password.PasswordEncrypt;

@Stateless
@Local(ServiceProviderEntitySessionBeanLocal.class)
@Remote(ServiceProviderEntitySessionBeanRemote.class)
public class ServiceProviderEntitySessionBean implements ServiceProviderEntitySessionBeanRemote, ServiceProviderEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;

    @EJB
    private BusinessCategorySessionBeanLocal businessCategorySessionBeanLocal;
    
    private final PasswordEncrypt passwordEncrypt = new PasswordEncrypt();

    @Override
    public ServiceProviderEntity registerNewServiceProvider(ServiceProviderEntity newServiceProvider, Long categoryId) throws BusinessCategoryNotFoundException, ServiceProviderEmailExistException, InvalidPasswordFormatException, UnknownPersistenceException 
    {
        try 
        {
            BusinessCategoryEntity categoryEntity = businessCategorySessionBeanLocal.retrieveBusinessCategoryById(categoryId);
            newServiceProvider.setCategory(categoryEntity);
            
            String newPassword = newServiceProvider.getPassword();
            if (newPassword.length() != 6)
            {
                throw new InvalidPasswordFormatException("Password length is not 6!");
            }
            else
            {
                Integer intPassword = Integer.valueOf(newPassword);
                String salt = passwordEncrypt.getSalt(30);
                String encryptedPassword = passwordEncrypt.generateSecurePassword(newServiceProvider.getPassword(), salt);
                newServiceProvider.setPassword(salt + encryptedPassword);
                em.persist(newServiceProvider);
                em.flush();
            }
            return newServiceProvider;
        }
        catch (NumberFormatException ex)
        {
            throw new InvalidPasswordFormatException("Password can only be digits!");
        }
        catch (PersistenceException ex) 
        {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new BusinessCategoryNotFoundException("Error! Businesss category cannot be found!");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }

    }

    @Override
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderAddress(String email) throws ServiceProviderEntityNotFoundException {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.email = :inEmail");
        query.setParameter("inEmail", email);
        try {
            return (ServiceProviderEntity) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new ServiceProviderEntityNotFoundException("Service Provider email " + email + " does not exist!");
        }
    }

    @Override
    public ServiceProviderEntity serviceProviderLogin(String email, Integer password) throws InvalidLoginCredentialException {
        try {
            String stringPassword = password.toString();
            ServiceProviderEntity currentServiceProviderEntity = retrieveServiceProviderByServiceProviderAddress(email);
            String saltAndPassword = currentServiceProviderEntity.getPassword();
            String salt = saltAndPassword.substring(0, 30);
            String encryptedPassword = saltAndPassword.substring(31);
            Boolean passwordVerification = passwordEncrypt.verifyUserPassword(stringPassword, encryptedPassword, salt);
            if (passwordVerification && currentServiceProviderEntity.getStatus() == ServiceProviderStatus.APPROVED) {
                return currentServiceProviderEntity;

            } else if (passwordVerification && currentServiceProviderEntity.getStatus() == ServiceProviderStatus.PENDING) {
                throw new InvalidLoginCredentialException("Your account is still pending administrator's approval. Please try again later!");
            } else if (passwordVerification && currentServiceProviderEntity.getStatus() == ServiceProviderStatus.BLOCKED) {
                throw new InvalidLoginCredentialException("Your account has been blocked by an administrator.");
            } else {
                throw new InvalidLoginCredentialException("Email address does not exist or invalid password.");
            }
        } catch (ServiceProviderEntityNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid Login : " + ex.getMessage());
        }
    }

    @Override
    public ServiceProviderEntity retrieveServiceProviderByServiceProviderId(Long serviceProviderId) throws ServiceProviderEntityNotFoundException {
        ServiceProviderEntity serviceProviderEntity = em.find(ServiceProviderEntity.class, serviceProviderId);
        if (serviceProviderEntity != null) {
            return serviceProviderEntity;
        } else {
            throw new ServiceProviderEntityNotFoundException("Service Provider ID " + serviceProviderId + " does not exist");
        }
    }

    @Override
    public void updateServiceProvider(ServiceProviderEntity serviceProviderEntity) throws ServiceProviderEntityNotFoundException, UpdateServiceProviderException {
        if (serviceProviderEntity.getServiceProviderId() != null) {
            ServiceProviderEntity serviceProviderEntityToUpdate = retrieveServiceProviderByServiceProviderId(serviceProviderEntity.getServiceProviderId());

            if (serviceProviderEntityToUpdate.getEmail().equals(serviceProviderEntity.getEmail())) {
                serviceProviderEntityToUpdate.setName(serviceProviderEntity.getName());
                serviceProviderEntityToUpdate.setCategory(serviceProviderEntity.getCategory());
                serviceProviderEntityToUpdate.setUen(serviceProviderEntity.getUen());
                serviceProviderEntityToUpdate.setCity(serviceProviderEntity.getCity());
                serviceProviderEntityToUpdate.setPhoneNumber(serviceProviderEntity.getPhoneNumber());
                serviceProviderEntityToUpdate.setAddress(serviceProviderEntity.getAddress());
                serviceProviderEntityToUpdate.setPassword(serviceProviderEntity.getPassword());

            } else {
                throw new UpdateServiceProviderException("Username of service provider to be updated does not match the existing record");
            }
        } else {
            throw new ServiceProviderEntityNotFoundException("Service Provider does not exist!");
        }
    }

    @Override
    public List<ServiceProviderEntity> retrieveAllServiceProviders() {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s");

        return query.getResultList();
    }

    @Override
    public List<ServiceProviderEntity> retrieveServiceProvidersByStatus(ServiceProviderStatus status) {
        Query query = em.createQuery("SELECT s FROM ServiceProviderEntity s WHERE s.status = :currStatus");
        query.setParameter("currStatus", status);

        return query.getResultList();
    }

    @Override
    public String approveServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyApprovedException {
        ServiceProviderEntity sp = retrieveServiceProviderByServiceProviderId(id);
        if (sp.getStatus() == ServiceProviderStatus.APPROVED) {
            throw new ServiceProviderAlreadyApprovedException("Service Provider has already been approved!");
        } else {
            sp.setStatus(ServiceProviderStatus.APPROVED);
        }

        return sp.getName();
    }

    @Override
    public String blockServiceProviderById(Long id) throws ServiceProviderEntityNotFoundException, ServiceProviderAlreadyBlockedException {
        ServiceProviderEntity sp = retrieveServiceProviderByServiceProviderId(id);
        if (sp.getStatus() == ServiceProviderStatus.BLOCKED) {
            throw new ServiceProviderAlreadyBlockedException("Service Provider has already been blocked!");
        } else {
            sp.setStatus(ServiceProviderStatus.BLOCKED);
        }

        return sp.getName();
    }

    @Override
    public List<ServiceProviderEntity> retrieveAllAvailableServiceProvidersForTheDay(LocalDate appointmentDate, Long category, String city) throws BusinessCategoryNotFoundException {
        BusinessCategoryEntity bcEntity = businessCategorySessionBeanLocal.retrieveBusinessCategoryById(category);
        Query query = em.createQuery("SELECT sp FROM ServiceProviderEntity sp WHERE sp.category = :bcEntity AND sp.city = :city");
        query.setParameter("bcEntity", bcEntity);
        query.setParameter("city", city);

        List<ServiceProviderEntity> results = query.getResultList();
        List<ServiceProviderEntity> availableServiceProviders = new ArrayList<>();
        for (ServiceProviderEntity serviceProvider : results) {
            List<LocalTime> serviceProviderAvailability = retrieveServiceProviderAvailabilityForTheDay(serviceProvider, appointmentDate);

            if (!serviceProviderAvailability.isEmpty()) {
                availableServiceProviders.add(serviceProvider);
            }
        }
        return availableServiceProviders;
    }

    @Override
    public List<LocalTime> retrieveServiceProviderAvailabilityForTheDay(ServiceProviderEntity spEntity, LocalDate appointmentDate) {

        List<LocalTime> workingTimeSlots = new ArrayList<>();
        for (int i = 8; i <= 17; i++) {
            workingTimeSlots.add(LocalTime.of(i, 30, 00));
        }

        List<AppointmentEntity> apptEntities = new ArrayList<>();

        try {
            spEntity = retrieveServiceProviderByServiceProviderId(spEntity.getServiceProviderId());
            apptEntities = spEntity.getAppointmentEntities();
            apptEntities.size();
        } catch (ServiceProviderEntityNotFoundException ex) {
            System.err.println("Error occurred when retrieving service provider: " + ex.getMessage());
        }

        Boolean anyApptOnDate = false;
        if (!apptEntities.isEmpty()) {
            for (AppointmentEntity appointment : apptEntities) {
                if (appointment.getAppointmentDate().equals(appointmentDate)) {
                    anyApptOnDate = true;
                    boolean removed = workingTimeSlots.remove(appointment.getAppointmentTime());
                    System.out.println(removed);

                }
            }
        }

        if (anyApptOnDate) {
            return workingTimeSlots;
//return availableTimeSlots;
        } else {
            return workingTimeSlots;
        }
    }

    @Override
    public void addAppointment(AppointmentEntity appt, ServiceProviderEntity spEntity) {
        try {
            ServiceProviderEntity svcpEntity = this.retrieveServiceProviderByServiceProviderId(spEntity.getServiceProviderId());
            List<AppointmentEntity> appts = svcpEntity.getAppointmentEntities();
            appts.add(appt);
            svcpEntity.setAppointmentEntities(appts);
        } catch (ServiceProviderEntityNotFoundException ex) {
            System.err.println("Error occurred when retrieving service provider: " + ex.getMessage());
        }

    }

    @Override
    public double generateOverallRating(ServiceProviderEntity spEntity) {
        List<AppointmentEntity> appointments = retrieveAllAppointmentsForServiceProvider(spEntity);
        List<Integer> listOfRatings = new ArrayList<>();
        for (AppointmentEntity appt : appointments) {
            System.out.println("appt.getRating()1:");
            System.out.println(appt.getRating());
            if (appt.getRating() > 0) {
                listOfRatings.add(appt.getRating());
                System.out.println("appt.getRating()2:");
                System.out.println(appt.getRating());
            }
        }
        double totalRating = 0;
        double overallRating;
        for (Integer rating : listOfRatings) {
            totalRating += rating.doubleValue();
        }

        if (listOfRatings.isEmpty()) {
            return 0;
        } else {
            overallRating = totalRating / listOfRatings.size(); // no ratings cannot divide by 0 - output: "thrs no rating yet"
            return overallRating;
        }
    }

    @Override
    public List<AppointmentEntity> retrieveUpcomingAppointmentsForServiceProvider(ServiceProviderEntity serviceProviderEntity) {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.serviceProviderEntity.serviceProviderId = :serviceProviderEntityId AND a.rating = :rating");
        query.setParameter("serviceProviderEntityId", serviceProviderEntity.getServiceProviderId());
        query.setParameter("rating", 0);
        List<AppointmentEntity> result = query.getResultList();
        return result;
    }
    
    @Override
    public List<AppointmentEntity> retrieveAllAppointmentsForServiceProvider(ServiceProviderEntity serviceProviderEntity) {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.serviceProviderEntity.serviceProviderId = :serviceProviderEntityId");
        query.setParameter("serviceProviderEntityId", serviceProviderEntity.getServiceProviderId());
        List<AppointmentEntity> result = query.getResultList();
        return result;     
    }
    
    @Override
    public List<AppointmentEntity> retrieveAppointmentsOfServiceProviderById(Long serviceProviderId) throws ServiceProviderEntityNotFoundException {
        ServiceProviderEntity serviceProviderEntity = retrieveServiceProviderByServiceProviderId(serviceProviderId);
        List<AppointmentEntity> apptEntities = serviceProviderEntity.getAppointmentEntities();
        apptEntities.size();
        return apptEntities;
    }
}
