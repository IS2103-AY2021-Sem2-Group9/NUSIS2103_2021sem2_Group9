package ejb.session.ws;

import ejb.session.stateless.CustomerEntitySessionBeanLocal;
import entity.CustomerEntity;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.CustomerNotFoundException;

@WebService(serviceName = "CustomerWebService")
@Stateless()
public class CustomerWebService {

    @EJB
    private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;

    @WebMethod
    public CustomerEntity retrieveCustomerEntityById(@WebParam Long customerId) throws CustomerNotFoundException 
    {
        return customerEntitySessionBeanLocal.retrieveCustomerEntityById(customerId);
    }
}
