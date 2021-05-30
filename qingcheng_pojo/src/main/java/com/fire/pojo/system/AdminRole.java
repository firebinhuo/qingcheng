package com.fire.pojo.system;

import java.io.Serializable;
import java.util.List;

public class AdminRole implements Serializable {

    private Admin admin;
    private List<Role> role;

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }
}
