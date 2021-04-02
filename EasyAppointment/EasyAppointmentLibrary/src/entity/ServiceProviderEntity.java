package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import Enumeration.ServiceProviderStatus;


@Entity
public class ServiceProviderEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceProviderId;
    @Column(nullable = false, length = 32)
    private String name; 
    @Column(nullable = false, length = 32)
    private String category; 
    @Column(nullable = false, length = 32)
    private String uen;
    @Column(nullable = false, length = 32)
    private String city;
    @Column(nullable = false, length = 32)
    private String phoneNumber; 
    @Column(nullable = false, length = 128)
    private String address; 
    @Column(nullable = false, length = 64, unique = true)
    private String email; 
    @Column(nullable = false, length = 64)
    private Integer password; 
    @Column(nullable = false)
    private List<Boolean> availability;
    
    @Column (nullable = false)
    private ServiceProviderStatus status;

    public ServiceProviderEntity() {
    }

    public ServiceProviderEntity(String name, String category, String uen, String city, String phoneNumber, String address, String email, Integer password, List<Boolean> availability, ServiceProviderStatus status) {
        this.name = name;
        this.category = category;
        this.uen = uen;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
        this.availability = availability;
        this.status = status;
    }

    public ServiceProviderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceProviderStatus status) {
        this.status = status;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUen() {
        return uen;
    }

    public void setUen(String uen) {
        this.uen = uen;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public List<Boolean> getAvailability() {
        return availability;
    }

    public void setAvailability(List<Boolean> availability) {
        this.availability = availability;
    }
    
    

    public Long getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(Long serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceProviderId != null ? serviceProviderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the serviceProviderId fields are not set
        if (!(object instanceof ServiceProviderEntity)) {
            return false;
        }
        ServiceProviderEntity other = (ServiceProviderEntity) object;
        if ((this.serviceProviderId == null && other.serviceProviderId != null) || (this.serviceProviderId != null && !this.serviceProviderId.equals(other.serviceProviderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ServiceProviderEntity[ id=" + serviceProviderId + " ]";
    }
    
}
