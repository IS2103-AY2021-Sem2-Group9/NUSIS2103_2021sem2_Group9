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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.AdminNotFoundException;
import util.exception.InvalidLoginException;

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
    
    @Override
    public AdminEntity retrieveAdminEntityByAdminEmail(String email) throws AdminNotFoundException
    {
        Query query = entityManager.createQuery("SELECT a FROM AdminEntity a WHERE a.adminEmail = :admin");
        query.setParameter("admin", email);
        try
        {
            return (AdminEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new AdminNotFoundException("Admin Email: " + email + " does not exist!");
        }
    }
    
    @Override
    public AdminEntity adminLogin(String email, String password) throws InvalidLoginException
    {
        try
        {
            AdminEntity adminEntity = retrieveAdminEntityByAdminEmail(email);
            
            if (adminEntity.getPassword().equals(password))
            {
                return adminEntity;
            }
            else
            {
                throw new InvalidLoginException("Invalid login credentials!");
            }
        }
        catch (AdminNotFoundException ex)
        {
            throw new InvalidLoginException("Invalid Login: " + ex.getMessage());
        }
    }
}
