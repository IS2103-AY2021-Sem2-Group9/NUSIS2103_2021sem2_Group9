/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AdminEntitySessionBeanLocal;
import ejb.session.stateless.ServiceProviderEntitySessionBeanLocal;
import entity.AdminEntity;
import entity.ServiceProviderEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.exception.AdminNotFoundException;
import util.exception.ServiceProviderAddressExistException;
import util.exception.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup

public class DataInitializationSessionBean {

    @EJB
    private AdminEntitySessionBeanLocal adminEntitySessionBeanLocal;
    @EJB 
    private ServiceProviderEntitySessionBeanLocal serviceProviderEntitySessionBeanLocal;
    
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
        try {
            List<Boolean> availability =new ArrayList<Boolean>(Arrays.asList(new Boolean[10]));
            Collections.fill(availability, Boolean.TRUE);
            adminEntitySessionBeanLocal.createAdminEntity(new AdminEntity("terry@easyadmin.com", "password", "Terry", "Tan"));
            serviceProviderEntitySessionBeanLocal.createServiceProviderEntity(new ServiceProviderEntity("Kevin Paterson", "1", "1111001111", "Clementi", "93718799", "13, Clementi Road", "kevin@nuh.com.sg", 113322, availability));
        } catch (ServiceProviderAddressExistException | UnknownPersistenceException ex) {
            ex.printStackTrace();
        }
    }
}
