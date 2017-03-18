package app.packman.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Model Class for User.
 * Created by RushikeshParadkar on 11/15/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private long userId;

    private Collection<Address> address = new ArrayList<Address>();

    private Address defaultAddress;

    private Person person;

    public User() {
    }

    public User(Person person) {
        this.person = person;
    }

    public Collection<Address> getAddress() {
        return address;
    }

    public void setAddress(Collection<Address> address) {
        this.address = address;
    }

    public long getUserId() {
        return userId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Address getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Address defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
