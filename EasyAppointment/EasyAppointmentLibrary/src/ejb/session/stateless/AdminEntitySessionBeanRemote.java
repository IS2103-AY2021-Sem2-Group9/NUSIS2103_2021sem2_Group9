package ejb.session.stateless;

import entity.AdminEntity;
import util.exception.AdminNotFoundException;

public interface AdminEntitySessionBeanRemote {
    public Long createAdminEntity(AdminEntity adminEntity);
    
    public AdminEntity retrieveAdminEntityByAdminId(Long adminId) throws AdminNotFoundException;

}
