/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AdminEntity;
import util.exception.AdminNotFoundException;

public interface AdminEntitySessionBeanLocal {
    public Long createAdminEntity(AdminEntity adminEntity);
    
    public AdminEntity retrieveAdminEntityByAdminId(Long adminId) throws AdminNotFoundException;
}
