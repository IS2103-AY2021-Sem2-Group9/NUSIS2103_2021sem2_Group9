package ejb.session.stateless;

import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.util.concurrent.Future;

public interface EmailSessionBeanRemote 
{
    public Boolean emailCheckoutNotificationSync(CustomerEntity customerEntity, String fromEmailAddress, String toEmailAddress);
    
    public Future<Boolean> emailCheckoutNotificationAsync(CustomerEntity customerEntity, String fromEmailAddress, String toEmailAddress) throws InterruptedException;
}