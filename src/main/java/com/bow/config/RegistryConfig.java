package com.bow.config;

/**
 * @author vv
 * @since 2016/8/19.
 */
public class RegistryConfig {

    private String id;

    /**
     * zookeeper, hessian
     */
    private String name;

    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
