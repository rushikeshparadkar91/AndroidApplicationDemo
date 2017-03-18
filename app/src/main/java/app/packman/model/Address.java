package app.packman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Model Class for Address
 * Created by mlshah on 11/15/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address implements Serializable {

    private String street1;
    private String street2;
    private String landmark;
    private String city;
    private String state;
    private String pinCode;
    private String zoneId;
    private String country;

    public Address() {
    }

    public Address(String street1, String street2, String landmark, String city, String state,
                   String zoneId, String pinCode, String country) {
        this.street1 = street1;
        this.street2 = street2;
        this.landmark = landmark;
        this.city = city;
        this.state = state;
        this.zoneId = zoneId;
        this.pinCode = pinCode;
        this.country = country;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }


    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
