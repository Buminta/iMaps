package model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by me866chuan on 8/24/14.
 */

public class MyItem implements ClusterItem {
    public final String name;
    public final int profilePhoto;
    private final LatLng mPosition;

    public MyItem(LatLng position, String name, int pictureResource) {
        this.name = name;
        profilePhoto = pictureResource;
        mPosition = position;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
