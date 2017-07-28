package rest.send.org.scan;

/**
 * Created by d069020 on 22/03/2017.
 */

public class LocationScanResult {

    private String bssid;
    private String level;

    public String getBSSID() {

        return bssid;
    }

    public void setBSSID(String bSSID) {

        this.bssid = bSSID;
    }

    public String getLevel() {

        return level;
    }

    public void setLevel(String level) {

        this.level = level;
    }

    @Override
    public String toString() {
        return "{\"BSSID\":\""+ this.bssid + "\",\"Level\":\"" + this.level + "\"}";
    }
}
