package ejb.session.stateless;

import entity.AdminEntity;
import util.exception.AdminNotFoundException;
import util.exception.InvalidLoginCredentialException;

public interface AdminEntitySessionBeanRemote {
    public Long createAdminEntity(AdminEntity adminEntity);
    
    public AdminEntity retrieveAdminEntityByAdminId(Long adminId) throws AdminNotFoundException;

    public AdminEntity retrieveAdminEntityByAdminEmail(String email) throws AdminNotFoundException;
    
    public AdminEntity adminLogin(String email, String password) throws InvalidLoginCredentialException;
}
