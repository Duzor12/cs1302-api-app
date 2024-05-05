package cs1302.api;

import com.google.gson.annotations.SerializedName;

/** Represents one result from Open Charge API response.
 */
public class ChargeResult {

    @SerializedName("AddressInfo")
    AddressInfo addressInfo;

    @SerializedName("StatusType")
    StatusType statusType;

    /** Default Constructor.
     */
    public ChargeResult() {
    }
}
