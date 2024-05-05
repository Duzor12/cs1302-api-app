package cs1302.api;

import com.google.gson.annotations.SerializedName;

/** Address Info of POI.
 */
public class AddressInfo {

    @SerializedName("AddressLine1")
    String addressLine1;

    @SerializedName("AddressLine2")
    String addressLine2;

    @SerializedName("Town")
    String town;

    @SerializedName("Postcode")
    String postCode;

    @SerializedName("Country")
    Country country;



    /** Default Constructor.
     */
    public AddressInfo() {
    }
}
