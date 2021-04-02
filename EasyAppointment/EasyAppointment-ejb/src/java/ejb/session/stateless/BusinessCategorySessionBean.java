package ejb.session.stateless;

import entity.BusinessCategoryEntity;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

    
}
