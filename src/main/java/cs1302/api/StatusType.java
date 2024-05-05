package cs1302.api;

import com.google.gson.annotations.SerializedName;

/** Status Type of the POI.
 */
public class StatusType {

    @SerializedName("IsOperational")
    boolean isOperational;

    /** Default Constructor.
     */
    public StatusType () {

    }
}
