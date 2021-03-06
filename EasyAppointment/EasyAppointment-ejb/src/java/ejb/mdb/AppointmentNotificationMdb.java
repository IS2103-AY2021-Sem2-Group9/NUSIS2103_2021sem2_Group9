package ejb.mdb;

import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import ejb.session.stateless.EmailSessionBeanLocal;
import entity.AppointmentEntity;
import entity.CustomerEntity;
import java.util.List;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import util.exception.CustomerNotFoundException;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queueAppointmentNotification"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class AppointmentNotificationMdb implements MessageListener 
{
    
    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;
    @EJB
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;
    
    public AppointmentNotificationMdb() 
    {
    }
    
    @Override
    public void onMessage(Message message) 
    {
        try
        {
            if (message instanceof MapMessage)
            {
                MapMessage mapMessage = (MapMessage)message;              
                String fromEmailAddress = mapMessage.getString("fromEmailAddress");
                Long customerEntityId = (Long)mapMessage.getLong("customerEntityId");
                
                CustomerEntity customerEntity = customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerEntityId);
                List<AppointmentEntity> appointments = customerEntitySessionBeanLocal.retrieveCustomerEntityUpcomingAppointments(customerEntityId);
                AppointmentEntity appointment = appointments.get(0);
                String toEmailAddress = customerEntity.getEmail();
                
                emailSessionBeanLocal.emailCheckoutNotificationSync(customerEntity, appointment, fromEmailAddress, toEmailAddress);
            }
        }
        catch(CustomerNotFoundException | JMSException ex)
        {
            System.err.println("Error sending Email: " + ex.getMessage());
        }
    }
    
}
