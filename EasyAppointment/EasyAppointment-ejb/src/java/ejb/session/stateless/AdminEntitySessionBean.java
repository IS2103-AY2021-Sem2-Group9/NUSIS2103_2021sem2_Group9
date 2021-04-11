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
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidPasswordFormatException;
import util.password.PasswordEncrypt;

@Stateless
@Local(AdminEntitySessionBeanLocal.class)
@Remote(AdminEntitySessionBeanRemote.class)
public class AdminEntitySessionBean implements AdminEntitySessionBeanRemote, AdminEntitySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager entityManager;
    
    private final PasswordEncrypt passwordEncrypt = new PasswordEncrypt();

    @Override
    public Long createAdminEntity(AdminEntity adminEntity) throws InvalidPasswordFormatException
    {
        try 
        {
            String newPassword = adminEntity.getPassword();
            if (newPassword.length() != 6)
            {
                throw new InvalidPasswordFormatException("Password length is not 6!");
            }
            else
            {
                Integer intPassword = Integer.valueOf(newPassword);
                String salt = passwordEncrypt.getSalt(30);
                String encryptedPassword = passwordEncrypt.generateSecurePassword(adminEntity.getPassword(), salt);
                adminEntity.setPassword(salt + encryptedPassword);
                entityManager.persist(adminEntity);
                entityManager.flush();
            }
            return adminEntity.getId();
        }
        catch (NumberFormatException ex)
        {
            throw new InvalidPasswordFormatException("Password can only be digits!");
        }
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
    public AdminEntity adminLogin(String email, String password) throws InvalidLoginCredentialException
    {
        try
        {
            AdminEntity adminEntity = retrieveAdminEntityByAdminEmail(email);
            String saltAndPassword = adminEntity.getPassword();
            String salt = saltAndPassword.substring(0, 30);
            String encryptedPassword = saltAndPassword.substring(31);
            Boolean passwordVerification = passwordEncrypt.verifyUserPassword(password, encryptedPassword, salt);
            
            if (passwordVerification)
            {
                return adminEntity;
            }
            else
            {
                throw new InvalidLoginCredentialException("Invalid login credentials!");
            }
        }
        catch (AdminNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Invalid Login: " + ex.getMessage());
        }
    }
}
