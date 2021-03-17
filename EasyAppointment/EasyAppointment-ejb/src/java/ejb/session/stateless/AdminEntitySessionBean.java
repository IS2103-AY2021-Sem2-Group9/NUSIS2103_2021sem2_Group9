/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminEntity;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.AdminNotFoundException;

/**
 *
 * @author Lawson
 */
@Stateless
@Local(AdminEntitySessionBeanLocal.class)
@Remote(AdminEntitySessionBeanRemote.class)
public class AdminEntitySessionBean implements AdminEntitySessionBeanRemote, AdminEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager entityManager;

    @Override
    public Long createAdminEntity(AdminEntity adminEntity) 
    {
        entityManager.persist(adminEntity);
        entityManager.flush();
        return adminEntity.getId();
    }
    
    @Override
    public AdminEntity retrieveAdminEntityByAdminId(Long adminId) throws AdminNotFoundException
    {
        AdminEntity adminEntity = entityManager.find(AdminEntity.class, adminId);
        if(adminEntity != null)
        {
            return adminEntity;
        } 
        else 
        {
            throw new AdminNotFoundException("Admin ID " + adminId + " does not exist!\n"); 
        }
    }
}
