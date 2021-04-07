package util.email;

import entity.AppointmentEntity;
import entity.CustomerEntity;
import entity.ServiceProviderEntity;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailManager 
{
    private final String emailServerName = "smtp.gmail.com";
    private final String mailer = "JavaMailer";
    private String smtpAuthUser;
    private String smtpAuthPassword;
    
    public EmailManager()
    {
    }   
    
    public EmailManager(String smtpAuthUser, String smtpAuthPassword)
    {
        this.smtpAuthUser = smtpAuthUser;
        this.smtpAuthPassword = smtpAuthPassword;
    }
    
    public Boolean emailAppointmentNotification(CustomerEntity customerEntity, AppointmentEntity appointmentEntity, String fromEmailAddress, String toEmailAddress)
    {
        ServiceProviderEntity serviceProvider = appointmentEntity.getServiceProviderEntity();
        
        String emailBody = "";
        
        emailBody += "Dear " + customerEntity.getFirstName() + ", \n\n";
        emailBody += "This is a reminder!\nYou have an upcoming appointment: "  +  "\n\n";
               
        String apptNum = String.format("%s: " + appointmentEntity.getAppointmentNum() + "\n", "Appointment Number");
        String serviceProviderName = String.format("%s: " + serviceProvider.getName() + "\n", "Service Provider's Name"); 
        String apptDate = String.format("%s: " + appointmentEntity.getAppointmentDate() + "\n", "Appointment Date"); 
        String apptTime = String.format("%s: " + appointmentEntity.getAppointmentTime() + "\n", "Appointment Time"); 
        String address = String.format("%s: " + serviceProvider.getAddress() + "\n", "Address"); 
        
        emailBody += apptNum;
        emailBody += serviceProviderName;
        emailBody += apptDate;
        emailBody += apptTime;
        emailBody += address;
        
        emailBody += "\n\n\n";
        emailBody += "Thank you for using EasyAppointment!";
                 
        try 
        {
            Properties props = new Properties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.host", emailServerName);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");            
            props.put("mail.smtp.debug", "true");            
            javax.mail.Authenticator auth = new SMTPAuthenticator(smtpAuthUser, smtpAuthPassword);
            Session session = Session.getInstance(props, auth);
            session.setDebug(true);            
            Message msg = new MimeMessage(session);
                                    
            if (msg != null)
            {
                msg.setFrom(InternetAddress.parse(fromEmailAddress, false)[0]);
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress, false));
                msg.setSubject("Reminder for your Appointment!");
                msg.setText(emailBody);
                msg.setHeader("X-Mailer", mailer);
                
                Date timeStamp = new Date();
                msg.setSentDate(timeStamp);
                
                Transport.send(msg);
                
                return true;
            }
            else
            {
                return false;
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            
            return false;
        }
    }
}
