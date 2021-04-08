package ejb.session.stateless;

import Enumeration.AppointmentStatusEnum;
import entity.AppointmentEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AppointmentExistException;
import util.exception.AppointmentNotFoundException;
import util.exception.UnknownPersistenceException;

@Stateless
@Local(AppointmentEntitySessionBeanLocal.class)
@Remote(AppointmentEntitySessionBeanRemote.class)
public class AppointmentEntitySessionBean implements AppointmentEntitySessionBeanRemote, AppointmentEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;

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
    public void cancelAppointment(String appointmentNum) throws AppointmentNotFoundException {
        AppointmentEntity appointment = retrieveAppointmentByAppointmentNum(appointmentNum);
        em.remove(appointment);
    }

    
    @Override
    public AppointmentEntity createAppointmentEntity(AppointmentEntity apptEntity) throws UnknownPersistenceException, AppointmentExistException {
//        em.persist(apptEntity);
//        em.flush();
//        return apptEntity;
        
        try {
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
}
