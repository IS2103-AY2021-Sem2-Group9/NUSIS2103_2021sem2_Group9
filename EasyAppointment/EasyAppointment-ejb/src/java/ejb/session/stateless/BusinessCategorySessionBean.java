package ejb.session.stateless;

import entity.BusinessCategoryEntity;
import entity.ServiceProviderEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.BusinessCategoryExistException;
import util.exception.BusinessCategoryNotFoundException;

@Stateless
@Local(BusinessCategorySessionBeanLocal.class)
@Remote(BusinessCategorySessionBeanRemote.class)
public class BusinessCategorySessionBean implements BusinessCategorySessionBeanRemote, BusinessCategorySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;

    @Override
    public String createBusinessCategoryEntity(BusinessCategoryEntity businessCategoryEntity) throws BusinessCategoryExistException
    {
        try
        {
            BusinessCategoryEntity category = retrieveBusinessCategoryByName(businessCategoryEntity.getCategoryName());
            throw new BusinessCategoryExistException("Business Category already exists!");
        }
        catch(BusinessCategoryNotFoundException ex)
        {
            em.persist(businessCategoryEntity);
            em.flush();    
        }
        return businessCategoryEntity.getCategoryName();
    }

    @Override
    public List<BusinessCategoryEntity> retrieveAllBusinessCategories()
    {
        Query query = em.createQuery("SELECT b FROM BusinessCategoryEntity b ORDER BY b.id");
        
        return query.getResultList();
    }
    
    @Override
    public BusinessCategoryEntity retrieveBusinessCategoryByName(String name) throws BusinessCategoryNotFoundException
    {
        Query query = em.createQuery("SELECT c FROM BusinessCategoryEntity c WHERE c.categoryName = :name");
        query.setParameter("name", name);
        
        try
        {
            return (BusinessCategoryEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new BusinessCategoryNotFoundException("Business Category " + name + " does not exist!");
        }
    }
    
    @Override
    public String deleteBusinessCategory(String businessCategoryName) throws BusinessCategoryNotFoundException
    {
        BusinessCategoryEntity businessCategoryEntity = retrieveBusinessCategoryByName(businessCategoryName);
        em.remove(businessCategoryEntity);
        
        return businessCategoryEntity.getCategoryName();
    }
    
    @Override
    public BusinessCategoryEntity retrieveBusinessCategoryById(Long categoryId) throws BusinessCategoryNotFoundException {
        Query query = em.createQuery("SELECT b from BusinessCategoryEntity b WHERE b.id = :categoryId");
        query.setParameter("categoryId", categoryId);
        
        try
        {
            return (BusinessCategoryEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new BusinessCategoryNotFoundException("Business Category " + categoryId + " does not exist!");
        }
    }
    
    @Override
    public List<ServiceProviderEntity> retrieveServiceProvidersByBusinessCategory(Long categoryId) throws BusinessCategoryNotFoundException
    {
            BusinessCategoryEntity businessCategory = retrieveBusinessCategoryById(categoryId);
            List<ServiceProviderEntity> spEntities = businessCategory.getServiceProviderEntities();
            spEntities.size();
            return spEntities;
    }
}
