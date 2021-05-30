package com.fire.pojo.system;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "tb_role_resource")
public class Role_Resource implements Serializable {
    @Column
    @Id
    private Integer role_id;
    @Column
    @Id
    private Integer resource_id;

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public Integer getResource_id() {
        return resource_id;
    }

    public void setResource_id(Integer resource_id) {
        this.resource_id = resource_id;
    }
}
