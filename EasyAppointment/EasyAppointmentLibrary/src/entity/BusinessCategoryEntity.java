package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class BusinessCategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 32, unique = true)
    private String categoryName;
    @OneToMany(mappedBy = "category", orphanRemoval = true)
    @JoinColumn(nullable = false)
    private List<ServiceProviderEntity> serviceProviderEntities; 

    public BusinessCategoryEntity() {
        this.serviceProviderEntities = new ArrayList<>();
    }

    public BusinessCategoryEntity(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BusinessCategoryEntity)) {
            return false;
        }
        BusinessCategoryEntity other = (BusinessCategoryEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BusinessCategoryEntity[ id=" + id + " ]";
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @XmlTransient
    public List<ServiceProviderEntity> getServiceProviderEntities() {
        return serviceProviderEntities;
    }

    public void setServiceProviderEntities(List<ServiceProviderEntity> serviceProviderEntities) {
        this.serviceProviderEntities = serviceProviderEntities;
    }
    
}
