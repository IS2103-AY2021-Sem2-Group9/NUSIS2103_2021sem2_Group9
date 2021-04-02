/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BusinessCategoryEntity;
import java.util.List;
import util.exception.BusinessCategoryNotFoundException;

public interface BusinessCategorySessionBeanRemote {
    public String createBusinessCategoryEntity(BusinessCategoryEntity businessCategoryEntity);
    
    public List<BusinessCategoryEntity> retrieveAllBusinessCategories();

    public BusinessCategoryEntity retrieveBusinessCategoryByName(String name) throws BusinessCategoryNotFoundException;

    public String deleteBusinessCategory(String businessCategoryName) throws BusinessCategoryNotFoundException;
    
}
