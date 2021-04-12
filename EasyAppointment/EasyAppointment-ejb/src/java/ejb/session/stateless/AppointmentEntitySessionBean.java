package ejb.session.stateless;

import Enumeration.AppointmentStatusEnum;
import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AppointmentCannotBeCancelledException;
import util.exception.AppointmentExistException;
import util.exception.AppointmentNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.ServiceProviderEntityNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
@Local(AppointmentEntitySessionBeanLocal.class)
@Remote(AppointmentEntitySessionBeanRemote.class)
public class AppointmentEntitySessionBean implements AppointmentEntitySessionBeanRemote, AppointmentEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;
    
    @EJB
    private CustomerEntitySessionBeanLocal customerEntitysessionBeanLocal;
    @EJB
    private ServiceProviderEntitySessionBeanLocal serviceProviderEntitysessionBeanLocal;

    @Override
    public List<AppointmentEntity> retrieveAllAppointments() {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a");

        return query.getResultList();
    }

    @Override
    public AppointmentEntity retrieveAppointmentByAppointmentNum(String appointmentNum) throws AppointmentNotFoundException {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a where a.appointmentNum = :appointmentNum");
        query.setParameter("appointmentNum", appointmentNum);

        try {
            return (AppointmentEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AppointmentNotFoundException("Appointment number " + appointmentNum + " does not exist!");
        }
    }

    @Override
    public void cancelAppointment(String appointmentNum) throws AppointmentNotFoundException, AppointmentCannotBeCancelledException {
        AppointmentEntity appt = retrieveAppointmentByAppointmentNum(appointmentNum);
        
        LocalDate dateNow= LocalDate.now(); 
        LocalTime timeNow = LocalTime.now(); 
        
        LocalDate apptDate = appt.getAppointmentDate();
        LocalTime apptTime = appt.getAppointmentTime();
        
        Long yearDiff = ChronoUnit.YEARS.between(dateNow, apptDate); 
        Long monthDiff = ChronoUnit.MONTHS.between(dateNow, apptDate);
        Long dayDiff = ChronoUnit.DAYS.between(dateNow, apptDate);
        
        System.out.println(dayDiff);
        
        int compareDate = dateNow.compareTo(apptDate); 
        double hoursDiff = ChronoUnit.HOURS.between(timeNow, apptTime);
        
        System.out.println(hoursDiff); // testing
        
        if (dayDiff > 1) {
            appt.getCustomerEntity().getAppointments().remove(appt);
            appt.getServiceProviderEntity().getAppointmentEntities().remove(appt);
            em.remove(appt);
        } else if (dayDiff == 1 && hoursDiff >= 0) { // accounting for same date and > 24 hours 
            appt.getCustomerEntity().getAppointments().remove(appt);
            appt.getServiceProviderEntity().getAppointmentEntities().remove(appt);
            em.remove(appt);
        } else {
            throw new AppointmentCannotBeCancelledException("Appointment number " + appointmentNum + " cannot be cancelled as it is less than 24 hours before the scheduled time.");
        }
    }

    
    @Override
    public AppointmentEntity createAppointmentEntity(AppointmentEntity apptEntity) throws UnknownPersistenceException, AppointmentExistException {
        try {
            ServiceProviderEntity spEntity = em.find(ServiceProviderEntity.class, apptEntity.getServiceProviderEntity().getServiceProviderId());
            spEntity.getAppointmentEntities().add(apptEntity);
            CustomerEntity custEntity = em.find(CustomerEntity.class, apptEntity.getCustomerEntity().getId());
            custEntity.getAppointments().add(apptEntity);
            apptEntity.setCustomerEntity(custEntity);
            apptEntity.setServiceProviderEntity(spEntity);
            em.persist(apptEntity);
            em.flush();
            return apptEntity;
        } catch (PersistenceException ex) {
            if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) { // A database-related exception
                if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) { // To get the internal error
                    throw new AppointmentExistException("Error creating appointment");
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } else {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }

    @Override
    public AppointmentEntity updateAppointmentEntity(AppointmentEntity apptEntity) {
        em.merge(apptEntity);
        em.flush();
        return apptEntity;
    }

    @Override
    public List<AppointmentEntity> retrieveUpcomingAppointmentsForServiceProvider(ServiceProviderEntity serviceProviderEntity) {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a WHERE a.serviceProviderEntity.serviceProviderId = :serviceProviderEntityId AND a.appointmentStatusEnum = :status");
        query.setParameter("serviceProviderEntityId", serviceProviderEntity.getServiceProviderId());
        query.setParameter("status", AppointmentStatusEnum.UPCOMING);
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
    public void rateAppointment(long appointmentEntityId, int rating) {
        AppointmentEntity appt = em.find(AppointmentEntity.class,appointmentEntityId);
        appt.setRating(rating);
    }
    
    @Override
    public String retrieveAppointmentDateWithApptNum(String apptNum) throws AppointmentNotFoundException {
        AppointmentEntity apptEntity = this.retrieveAppointmentByAppointmentNum(apptNum);
        String dateStr = apptEntity.getAppointmentDate().toString();
        
        return dateStr;
    }
    
    @Override
    public String retrieveAppointmentTimeWithApptNum(String apptNum) throws AppointmentNotFoundException {
        AppointmentEntity apptEntity = this.retrieveAppointmentByAppointmentNum(apptNum);
        String timeStr = apptEntity.getAppointmentTime().toString();
        
        return timeStr;
    }
    
    @Override
    public String getStatus(AppointmentEntity appointmentEntity) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        LocalDate apptDate = appointmentEntity.getAppointmentDate();
        LocalTime apptTime = appointmentEntity.getAppointmentTime();
        
        int compare = apptDate.compareTo(currentDate);
        double hourDiff = Math.floor(ChronoUnit.HOURS.between(currentTime, apptTime) + ChronoUnit.MINUTES.between(currentTime, apptTime)/60);
        
        if(compare > 0 ) { 
            return "UPCOMING";
        } else if (compare == 0 && hourDiff > 0) {
            return "UPCOMING"; 
        } else if (compare == 0 && hourDiff >= -1 && hourDiff <= 0) {
            return "ONGOING";
        } else {
            return "COMPLETED";
        }
    }
}
