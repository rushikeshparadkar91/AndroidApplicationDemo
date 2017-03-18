package app.packman.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.io.Serializable;

/**
 * Model Class for Agent.
 * Created by sujaysudheendra on 11/16/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@jsonId")
public class Agent implements Serializable {

    private long agentId;
    private Person person;
    private Address address;

    public Agent(Person person, Address address) {
        this.person = person;
        this.address = address;
    }

    public Agent() {
    }

    public long getAgentId() {
        return agentId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
