package ejb.session.stateless;

import entity.BusinessCategoryEntity;
import java.util.List;
import util.exception.BusinessCategoryExistException;
import util.exception.BusinessCategoryNotFoundException;

public interface BusinessCategorySessionBeanLocal {
    public String createBusinessCategoryEntity(BusinessCategoryEntity businessCategoryEntity) throws BusinessCategoryExistException;

    public List<BusinessCategoryEntity> retrieveAllBusinessCategories();
    
    public BusinessCategoryEntity retrieveBusinessCategoryByName(String name) throws BusinessCategoryNotFoundException;

    public String deleteBusinessCategory(String businessCategoryName) throws BusinessCategoryNotFoundException;

    public BusinessCategoryEntity retrieveBusinessCategoryById(Long category) throws BusinessCategoryNotFoundException;
}
