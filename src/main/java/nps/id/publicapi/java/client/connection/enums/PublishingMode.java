package nps.id.publicapi.java.client.connection.enums;

public enum PublishingMode {
    /// <summary>
    /// Published all outgoing messages as soon as they appear.
    /// </summary>
    STREAMING,

    /// <summary>
    /// Aggregates messages for given time interval publishing only latest versions.
    /// </summary>
    CONFLATED
}
