package app.packman.utils.enums;

/**
 * Created by mlshah on 11/26/15.
 */
public enum NotificationMessage {

    PICKUPASSIGNED("PICKUPASSIGNED"),
    CANCELPICKUP("CANCELPICKUP");

    private String message;

    NotificationMessage(String s) {
        message = s;
    }

    public String getMessage() {
        return message;
    }
}
