package app.packman.utils;

/**
 * Created by sujaysudheendra on 3/14/16.
 */
public class Constants {
    public static String PACKMAN_SERVICE_URL = "http://ec2-54-200-147-243.us-west-2.compute.amazonaws.com:8080/com-packman-application";
   // public static String PACKMAN_SERVICE_URL = "http://10.0.2.2:8080";
    public static String USER_OBJECT = "user_object";
    public static String BROADCAST_USER_ACTION = "user_service_broadcast";
    public static String BROADCAST_REPLY_USER_UPDATE = "user_data_update";
    public static String BROADCAST_USER_SERVCICE_ERROR = "user_service_error";

    public static String USER_URL = PACKMAN_SERVICE_URL + "/person/user/";

    public static String LOGIN_SERVICE_URL = PACKMAN_SERVICE_URL + "/person/token";

    public static String ACTION_POST = "post";
    public static String ACTION_UPDATE = "update";
    public static String ACTION_DELETE = "delete";
    public static String ACTION_GET_INCOMING = "get_incoming";
    public static String ACTION_GET_OUTGOING = "get_outgoing";
    public static String ACTION_GET_SINGLE_SHIPMENT = "get_single";

    public static String SHIPMENT_OBJECT = "Shipment_object";
    public static String BROADCAST_SHIPMENT_ACTION = "Shipment_service_broadcast";
    public static String BROADCAST_IN_SHIPMENT_ACTION = "Shipment_incoming_broadcast";
    public static String BROADCAST_OUT_SHIPMENT_ACTION = "Shipment_outoging_broadcast";
    public static String BROADCAST_INCOMING_SHIPMENT = "Incoming_shipment_data";
    public static String BROADCAST_OUTGOING_SHIPMENT = "Outgoing_shipment_data";
    public static String BROADCAST_SHIPMENT = "shipment_data";
    public static String BROADCAST_SHIPMENT_SERVCICE_ERROR = "shipment_service_error";
    public static String BROADCAST_SHIPMENT_SERVCICE_SUCCESS = "shipment_service_success";

    public static String INCOMING_SHIPMENTS = "incomingshipments";
    public static String OUTGOING_SHIPMENTS = "sentshipments";
    public static String SHIPMENT_ACTION = "shipment_action";
    public static String SHIPMENT_ID = "shipment_id";

    public static String ADDRESS_POSITION = "address_position";
    public static String SELECTED_ADDRESS = "selected_address";
    public static String BROADCAST_ADDRESS_DELETE_ACTION = "delete_address";
    public static String BROADCAST_ADDRESS_ADD_ACTION = "add_update_address";

    public static String USER_SERVICE_URL = PACKMAN_SERVICE_URL+"/person/user/";
    public static String SHIPMENT_SERVICE_URL = PACKMAN_SERVICE_URL+"/shipments/";

    public static String NO_INTERNET = "No Internet Connection";
    public static String USER_DETAILS_ERROR = "Please checked the details entered";

    public static String USER_ID = "user_id";
}
