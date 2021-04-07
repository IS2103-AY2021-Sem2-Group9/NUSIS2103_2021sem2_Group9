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
        
        //String header = String.format("%-20s | %-25s | %-18s | %-18s | %-28s", "Appointment No.", "Service Provider's Name", "Appointment Date", "Appointment Time", "Address");
        //String information = String.format("%-20s | %-25s | %-18s | %-18s | %-28s", appointmentEntity.getAppointmentNum(), serviceProvider.getName(), appointmentEntity.getAppointmentDate(), appointmentEntity.getAppointmentTime(), serviceProvider.getAddress());
        
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
            
//        for(SaleTransactionLineItemEntity saleTransactionLineItemEntity:saleTransactionEntity.getSaleTransactionLineItemEntities())
//        {
//            emailBody += saleTransactionLineItemEntity.getSerialNumber()
//                + "     " + saleTransactionLineItemEntity.getProductEntity().getSkuCode()
//                + "     " + saleTransactionLineItemEntity.getProductEntity().getName()
//                + "     " + saleTransactionLineItemEntity.getQuantity()
//                + "     " + NumberFormat.getCurrencyInstance().format(saleTransactionLineItemEntity.getUnitPrice())
//                + "     " + NumberFormat.getCurrencyInstance().format(saleTransactionLineItemEntity.getSubTotal()) + "\n";
//        }
//            
//        emailBody += "\nTotal Line Item: " + saleTransactionEntity.getTotalLineItem() + ", Total Quantity: " + saleTransactionEntity.getTotalQuantity() + ", Total Amount: " + NumberFormat.getCurrencyInstance().format(saleTransactionEntity.getTotalAmount()) + "\n";
        
        
        
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
