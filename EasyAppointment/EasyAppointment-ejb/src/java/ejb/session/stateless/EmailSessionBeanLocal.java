package ejb.session.stateless;

import entity.ServiceProviderEntity;
import java.util.concurrent.Future;

public interface EmailSessionBeanLocal 
{

    public Boolean emailCheckoutNotificationSync(ServiceProviderEntity serviceProviderEntity, String fromEmailAddress, String toEmailAddress);

    public Future<Boolean> emailCheckoutNotificationAsync(ServiceProviderEntity serviceProviderEntity, String fromEmailAddress, String toEmailAddress) throws InterruptedException;
    
}
