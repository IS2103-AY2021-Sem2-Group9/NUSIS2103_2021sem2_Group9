package entity;

import Enumeration.AppointmentStatusEnum;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class AppointmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String appointmentNum;
    @Column(nullable = false)    
    private LocalDate appointmentDate;
    @Column(nullable = false)
    private LocalTime appointmentTime;
    @Column(nullable = false)
    private AppointmentStatusEnum appointmentStatusEnum;
    
    @ManyToOne
    private CustomerEntity customerEntity;
    @ManyToOne
    private ServiceProviderEntity serviceProviderEntity;

    public AppointmentEntity() {
    }

    public AppointmentEntity(String appointmentNum, LocalDate appointmentDate, LocalTime appointmentTime, AppointmentStatusEnum appointmentStatusEnum, CustomerEntity customerEntity, ServiceProviderEntity serviceProviderEntity) {
        this.appointmentNum = appointmentNum;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.appointmentStatusEnum = appointmentStatusEnum;
        this.customerEntity = customerEntity;
        this.serviceProviderEntity = serviceProviderEntity;
    }  
    
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
     * @return the appointmentDate
     */
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    /**
     * @param appointmentDate the appointmentDate to set
     */
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * @return the appointmentTime
     */
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    /**
     * @param appointmentTime the appointmentTime to set
     */
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
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
    
    /**
     * @return the appointmentStatusEnum
     */
    public AppointmentStatusEnum getAppointmentStatusEnum() {
        return appointmentStatusEnum;
    }

    /**
     * @param appointmentStatusEnum the appointmentStatusEnum to set
     */
    public void setAppointmentStatusEnum(AppointmentStatusEnum appointmentStatusEnum) {
        this.appointmentStatusEnum = appointmentStatusEnum;
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