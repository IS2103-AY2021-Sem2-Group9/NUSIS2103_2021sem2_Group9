/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AdminEntitySessionBeanLocal;
import entity.AdminEntity;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.exception.AdminNotFoundException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @EJB
    private AdminEntitySessionBeanLocal adminEntitySessionBeanLocal;
    
    public DataInitializationSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        try
        {
            adminEntitySessionBeanLocal.retrieveAdminEntityByAdminId(Long.valueOf(1));
        }
        catch(AdminNotFoundException ex)
        {
            initializeData();
        }
    }
    
    public void initializeData() 
    {
        adminEntitySessionBeanLocal.createAdminEntity(new AdminEntity("terry@easyadmin.com", "password"));
    }
}
