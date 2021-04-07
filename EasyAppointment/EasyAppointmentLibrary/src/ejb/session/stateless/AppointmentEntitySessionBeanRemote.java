package ejb.session.stateless;

import entity.AppointmentEntity;
import java.util.List;
import util.exception.AppointmentNumberNotFoundException;

public interface AppointmentEntitySessionBeanRemote {
    
    public AppointmentEntity createAppointmentEntity(AppointmentEntity appointmentEntity);

    public List<AppointmentEntity> retrieveAllAppointments();

    public void cancelAppointment(String appointmentNum) throws AppointmentNumberNotFoundException;

    public AppointmentEntity retrieveAppointmentByAppointmentNum(String appointmentNum) throws AppointmentNumberNotFoundException;
    
}
