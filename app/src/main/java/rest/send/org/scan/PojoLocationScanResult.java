package rest.send.org.scan;

/**
 * Created by d069020 on 22/03/2017.
 */

public class PojoLocationScanResult {

    private LocationScanResult[] locationScanResult ;


    public LocationScanResult[] getLocationScanResult() {

        return this.locationScanResult;
    }

    public void setLocationScanResult(LocationScanResult[] locationScanResult) {

        this.locationScanResult = locationScanResult;
    }

    @Override
    public String toString() {
        String myString  = "{\"locationScanResult\":[";
        for (LocationScanResult lsr:this.locationScanResult)
            myString = myString + lsr.toString() + ",";
        myString = myString.substring(0, myString.length()-1) + "]}";
        return myString;
    }
}
