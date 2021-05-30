package com.fire.pojo.system;

import java.io.Serializable;
import java.util.List;

public class RoleResource implements Serializable {
    private Role role;

    private List<Resource> resources;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
