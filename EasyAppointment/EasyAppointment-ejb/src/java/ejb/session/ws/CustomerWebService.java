package ejb.session.ws;

import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import entity.CustomerEntity;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.CustomerExistException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

@WebService(serviceName = "CustomerWebService")
@Stateless
public class CustomerWebService {

    @EJB
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;

    @WebMethod(operationName = "createCustomerEntity")
    public CustomerEntity createCustomerEntity(@WebParam(name = "customerEntity") CustomerEntity customerEntity) throws UnknownPersistenceException, CustomerExistException
    {
        return customerEntitySessionBeanLocal.createCustomerEntity(customerEntity);
    }
    
    @WebMethod(operationName = "customerLogin")
    public CustomerEntity customerLogin(@WebParam(name = "email") String email, 
                                        @WebParam(name = "password") Integer password) throws InvalidLoginCredentialException 
    {
        return customerEntitySessionBeanLocal.customerLogin(email, password);
    }
    
    @WebMethod(operationName = "retrieveCustomerEntityById")
    public CustomerEntity retrieveCustomerEntityById(@WebParam(name = "customerId") Long customerId) throws CustomerNotFoundException 
    {
        return customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerId);
    }
}
