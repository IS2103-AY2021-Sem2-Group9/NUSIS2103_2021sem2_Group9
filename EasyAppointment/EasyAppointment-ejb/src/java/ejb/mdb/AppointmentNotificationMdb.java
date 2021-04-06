package ejb.mdb;

import ejb.session.stateless.EmailSessionBeanLocal;
import ejb.session.stateless.ServiceProviderEntitySessionBeanLocal;
import entity.ServiceProviderEntity;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import util.exception.ServiceProviderEntityNotFoundException;

/**
 *
 * @author Lawson
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/queueAppointmentNotification"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class AppointmentNotificationMdb implements MessageListener 
{
    
    @EJB
    private EmailSessionBeanLocal emailSessionBeanLocal;
    @EJB
    private ServiceProviderEntitySessionBeanLocal serviceProviderEntitySessionBeanLocal;
    
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
                String toEmailAddress = mapMessage.getString("toEmailAddress");
                String fromEmailAddress = mapMessage.getString("fromEmailAddress");
                Long serviceProviderEntityId = (Long)mapMessage.getLong("serviceProviderEntityId");
                ServiceProviderEntity serviceProviderEntity = serviceProviderEntitySessionBeanLocal.retrieveServiceProviderByServiceProviderId(serviceProviderEntityId);
                
                emailSessionBeanLocal.emailCheckoutNotificationSync(serviceProviderEntity, fromEmailAddress, toEmailAddress);
            }
        }
        catch(ServiceProviderEntityNotFoundException | JMSException ex)
        {
            System.err.println("Error sending Email: " + ex.getMessage());
        }
    }
    
}
