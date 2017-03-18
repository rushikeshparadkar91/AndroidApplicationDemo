package app.packman.utils.enums;

/**
 * Enumeration for Shipment STATUS
 *
 * Created by RushikeshParadkar on 11/23/15.
 */
public enum ShipmentStatus {
    DRAFT("DRAFT"),
    READY("READY"),
    ASSIGNED("ASSIGNED"),
    PICKEDUP("PICKEDUP"),
    PACKED("PACKED"),
    INTRANSIT("INTRANSIT"),
    DELIVERED("DELIVERED");

    private String status;

    private ShipmentStatus(String s) {
        status = s;
    }

    public String getStatus() {
        return status;
    }
}
