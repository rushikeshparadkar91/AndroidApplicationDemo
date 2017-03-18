package app.packman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Model Class for Person.
 * Created by rushikeshparadkar on 11/15/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Person implements Serializable {

    private long personId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private byte[] profilePic;
    private String profilePicUrl;
    private String socialId;

    private Collection<DeviceRegisteration> deviceRegisteration = new ArrayList<DeviceRegisteration>();

    public Person() {
    }

    public Person(String lastName, String firstName, String email, String phone) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getPersonId() {
        return personId;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] image) {
        this.profilePic = image;
    }

    public Collection<DeviceRegisteration> getDeviceRegisteration() {
        return deviceRegisteration;
    }

    public void setDeviceRegisteration(Collection<DeviceRegisteration> deviceRegisteration) {
        this.deviceRegisteration = deviceRegisteration;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
