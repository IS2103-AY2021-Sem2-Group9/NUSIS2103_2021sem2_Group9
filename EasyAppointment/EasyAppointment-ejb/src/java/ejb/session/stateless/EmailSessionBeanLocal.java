package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.util.concurrent.Future;

public interface EmailSessionBeanLocal 
{

    public Boolean emailCheckoutNotificationSync(CustomerEntity customerEntity, AppointmentEntity appointmentEntity, String fromEmailAddress, String toEmailAddress);

    public Future<Boolean> emailCheckoutNotificationAsync(CustomerEntity customerEntity, AppointmentEntity appointmentEntity, String fromEmailAddress, String toEmailAddress) throws InterruptedException;
    
}
