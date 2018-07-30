package com.gy.mywifi.manager;

import android.os.Parcel;
import android.os.Parcelable;

public class HotspotClient implements Parcelable{

    /**
     * The MAC address of the client
     * @hide
     * @internal
     */
    public String deviceAddress;

    /**
     * The flag indicates whether this client is blocked or not
     * @hide
     * @internal
     */
    public boolean isBlocked = false;

    /**
     * @hide
     */
    public HotspotClient(String address, boolean blocked) {
        deviceAddress = address;
        isBlocked = blocked;
    }

    /**
     * @hide
     */
    public HotspotClient(HotspotClient source) {
        if (source != null) {
            deviceAddress = source.deviceAddress;
            isBlocked = source.isBlocked;
        }
    }

    /**
     * @hide
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(" deviceAddress: ").append(deviceAddress);
        sbuf.append('\n');
        sbuf.append(" isBlocked: ").append(isBlocked);
        sbuf.append("\n");
        return sbuf.toString();
    }

    /** Implement the Parcelable interface {@hide} */
    public int describeContents() {
        return 0;
    }

    /** Implement the Parcelable interface {@hide} */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceAddress);
        dest.writeByte(isBlocked ? (byte)1 : (byte)0);
    }

    /** Implement the Parcelable interface {@hide} */
    public static final Creator<HotspotClient> CREATOR =
            new Creator<HotspotClient>() {
                public HotspotClient createFromParcel(Parcel in) {
                    HotspotClient result = new HotspotClient(in.readString(), in.readByte() == 1 ? true : false);
                    return result;
                }

                public HotspotClient[] newArray(int size) {
                    return new HotspotClient[size];
                }
            };

}
