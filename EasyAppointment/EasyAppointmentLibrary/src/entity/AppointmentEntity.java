package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class AppointmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 32)
    private String appointmentNum;
    @Column(nullable = false, length = 32)
    @Temporal(TemporalType.TIMESTAMP)
    private Date appointmentTimestamp;
    @Column(nullable = false, length = 32)
    private Boolean ongoing;
    
    @ManyToOne
    private CustomerEntity customerEntity;
    @ManyToOne
    private ServiceProviderEntity serviceProviderEntity;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    @Override
    public String toString() {
        return "entity.AppointmentEntity[ id=" + id + " ]";
    }

    /**
     * @return the appointmentNum
     */
    public String getAppointmentNum() {
        return appointmentNum;
    }

    /**
     * @param appointmentNum the appointmentNum to set
     */
    public void setAppointmentNum(String appointmentNum) {
        this.appointmentNum = appointmentNum;
    }

    /**
     * @return the appointmentTimestamp
     */
    public Date getAppointmentTimestamp() {
        return appointmentTimestamp;
    }

    /**
     * @param appointmentTimestamp the appointmentTimestamp to set
     */
    public void setAppointmentTimestamp(Date appointmentTimestamp) {
        this.appointmentTimestamp = appointmentTimestamp;
    }

    /**
     * @return the ongoing
     */
    public Boolean getOngoing() {
        return ongoing;
    }

    /**
     * @param ongoing the ongoing to set
     */
    public void setOngoing(Boolean ongoing) {
        this.ongoing = ongoing;
    }

    /**
     * @return the customerEntity
     */
    public CustomerEntity getCustomerEntity() {
        return customerEntity;
    }

    /**
     * @param customerEntity the customerEntity to set
     */
    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    /**
     * @return the serviceProviderEntity
     */
    public ServiceProviderEntity getServiceProviderEntity() {
        return serviceProviderEntity;
    }

    /**
     * @param serviceProviderEntity the serviceProviderEntity to set
     */
    public void setServiceProviderEntity(ServiceProviderEntity serviceProviderEntity) {
        this.serviceProviderEntity = serviceProviderEntity;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        
        if (!(object instanceof AppointmentEntity)) {
            return false;
        }
        AppointmentEntity other = (AppointmentEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
    
}
