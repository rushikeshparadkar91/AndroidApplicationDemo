package app.packman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by mlshah on 4/10/16.
 * model class to store device registeration ids
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceRegisteration implements Serializable {

    private String registerationId;

    public DeviceRegisteration() {}

    public DeviceRegisteration(String registerationId) {
        this.registerationId = registerationId;
    }

    public String getRegisterationId() {
        return registerationId;
    }

    public void setRegisterationId(String registerationId) {
        this.registerationId = registerationId;
    }
}
