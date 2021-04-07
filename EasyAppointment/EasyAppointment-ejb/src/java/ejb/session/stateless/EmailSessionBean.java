/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import util.email.EmailManager;

@Stateless
@Local(EmailSessionBeanLocal.class)
@Remote(EmailSessionBeanRemote.class)
public class EmailSessionBean implements EmailSessionBeanRemote, EmailSessionBeanLocal
{
    private final String GMAIL_USERNAME = "easyappointment.group9@gmail.com";
    private final String GMAIL_PASSWORD = "NUSIS2103Group9";
    
    
    
    @Override
    public Boolean emailCheckoutNotificationSync(CustomerEntity customerEntity, AppointmentEntity appointmentEntity, String fromEmailAddress, String toEmailAddress)
    {
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailAppointmentNotification(customerEntity, appointmentEntity, fromEmailAddress, toEmailAddress);
        
        return result;
    } 
    
    
    
    @Asynchronous
    @Override
    public Future<Boolean> emailCheckoutNotificationAsync(CustomerEntity customerEntity, AppointmentEntity appointmentEntity, String fromEmailAddress, String toEmailAddress) throws InterruptedException
    {        
        EmailManager emailManager = new EmailManager(GMAIL_USERNAME, GMAIL_PASSWORD);
        Boolean result = emailManager.emailAppointmentNotification(customerEntity, appointmentEntity, fromEmailAddress, toEmailAddress);
        
        return new AsyncResult<>(result);
    }
}
