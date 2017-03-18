package app.packman.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import app.packman.utils.JsonDateDeserializer;
import app.packman.utils.JsonDateSerializer;
import app.packman.utils.enums.ShipmentStatus;


/**
 * Model Class for Shipment.
 * Created by sujaysudheendra on 11/16/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@jsonShipmentId")
public class Shipment implements Serializable {

    private long shipmentId;
    private User sender;
    private User receiver;
    private Double price;
    private String currency;
    private long trackingId;
    private String status;
    private Box box;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private LocalDateTime pickupTime;

    private Collection<Agent> agentList = new ArrayList<Agent>();

    private Address toAddress;

    private Address fromAddress;

    public Shipment(User sender, User receiver, Address toAddress, Address fromAddress, Double price,
                    String currency, ShipmentStatus status, LocalDateTime pickupTime, Box box) {
        this.sender = sender;
        this.receiver = receiver;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.price = price;
        this.currency = currency;
        this.status = status.getStatus();
        this.pickupTime = pickupTime;
        this.box = box;
    }

    public Shipment(User sender, Address toAddress, Address fromAddress, Double price,
                    String currency, ShipmentStatus status, LocalDateTime pickupTime, Box box) {
        this.sender = sender;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.price = price;
        this.currency = currency;
        this.status = status.getStatus();
        this.pickupTime = pickupTime;
        this.box = box;
    }

    public Shipment() {
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTrackingId(long trackingId) {
        this.trackingId = trackingId;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Address getToAddress() {
        return toAddress;
    }

    public void setToAddress(Address toAddress) {
        this.toAddress = toAddress;
    }

    public Address getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(Address fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(ShipmentStatus status) {
        this.status = status.getStatus();
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalDateTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public long getShipmentId() {
        return shipmentId;
    }


    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public Collection<Agent> getAgentList() {
        return agentList;
    }

    public void setAgentList(Collection<Agent> agent) {
        this.agentList = agent;
    }

    public long getTrackingId() {
        return trackingId;
    }
}
