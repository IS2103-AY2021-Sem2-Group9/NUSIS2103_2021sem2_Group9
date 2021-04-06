package ejb.session.stateless;

import entity.BusinessCategoryEntity;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.BusinessCategoryNotFoundException;

@Stateless
@Local(BusinessCategorySessionBeanLocal.class)
@Remote(BusinessCategorySessionBeanRemote.class)
public class BusinessCategorySessionBean implements BusinessCategorySessionBeanRemote, BusinessCategorySessionBeanLocal {

    @PersistenceContext(unitName = "EasyAppointment-ejbPU")
    private EntityManager em;

    @Override
    public String createBusinessCategoryEntity(BusinessCategoryEntity businessCategoryEntity) 
    {
        em.persist(businessCategoryEntity);
        em.flush();
        return businessCategoryEntity.getCategoryName();
    }

    @Override
    public List<BusinessCategoryEntity> retrieveAllBusinessCategories()
    {
        Query query = em.createQuery("SELECT b FROM BusinessCategoryEntity b");
        
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
    
    public BusinessCategoryEntity retrieveBusinessCategoryById(Long category) throws BusinessCategoryNotFoundException {
        Query query = em.createQuery("SELECT b from BusinessCategoryEntity b WHERE b.id = :category");
        query.setParameter("category", category);
        
        try
        {
            return (BusinessCategoryEntity)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new BusinessCategoryNotFoundException("Business Category " + category + " does not exist!");
        }
    }
}
