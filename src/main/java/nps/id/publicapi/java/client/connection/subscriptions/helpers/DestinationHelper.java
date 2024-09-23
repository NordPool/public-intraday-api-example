package nps.id.publicapi.java.client.connection.subscriptions.helpers;

import nps.id.publicapi.java.client.connection.enums.PublishingMode;

public class DestinationHelper {
    public static String composeDestination(String user, String version, PublishingMode publishingMode, String topic) {
        return composeDestination(user, version, publishingMode.toString().toLowerCase() + "/" + topic);
    }

    public static String composeDestination(String user, String version, String topic) {
        return "/user/" + user + "/" + version + "/" + topic;
    }

    public static String composeDestination(String version, String topic) {
        return "/" + version + "/" + topic;
    }
}
