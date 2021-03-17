package easyappointmentclient;

import ejb.session.stateless.AdminEntitySessionBeanRemote;
import entity.AdminEntity;
import javax.ejb.EJB;
import util.exception.AdminNotFoundException;

public class Main {

    @EJB
    private static AdminEntitySessionBeanRemote adminEntitySessionBeanRemote;
    
    public static void main(String[] args) throws AdminNotFoundException {
        AdminEntity admin = adminEntitySessionBeanRemote.retrieveAdminEntityByAdminId(Long.valueOf("1"));
        System.out.println("admin email: " + admin.getAdminEmail() + " admin password: " + admin.getPassword());
    }
    
}
