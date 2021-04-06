package ejb.session.stateless;

import entity.AppointmentEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AppointmentNumberNotFoundException;

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
    public AppointmentEntity retrieveAppointmentByAppointmentNum(String appointmentNum) throws AppointmentNumberNotFoundException {
        Query query = em.createQuery("SELECT a FROM AppointmentEntity a where a.appointmentNum = :appointmentNum");
        query.setParameter("appointmentNum", appointmentNum);
        
        try {
            return(AppointmentEntity)query.getSingleResult(); 
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AppointmentNumberNotFoundException("Appointment number " + appointmentNum + " does not exist!");
        }
    }
    
    @Override
    public void cancelAppointment(String appointmentNum) throws AppointmentNumberNotFoundException {
        AppointmentEntity appointment = retrieveAppointmentByAppointmentNum(appointmentNum);
        em.remove(appointment);   
    }
}
