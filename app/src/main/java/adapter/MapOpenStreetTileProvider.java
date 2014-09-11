package adapter;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by me866chuan on 9/9/14.
 */
public class MapOpenStreetTileProvider extends UrlTileProvider{
    private static final String FORMAT;

    static {
//        FORMAT = "http://otile1.mqcdn.com/tiles/1.0.0/osm/%d/%d/%d.png";
        FORMAT = "http://tile.opencyclemap.org/cycle/%d/%d/%d.png";
        //FOTMAT = "http://tile.openstreetmap.org/%d/%d/%d.png";
    }

    // ------------------------------------------------------------------------
    // Instance Variables
    // ------------------------------------------------------------------------

    private int x;
    private int y;
    private int z;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public MapOpenStreetTileProvider() {
        super(256, 256);
    }

    // ------------------------------------------------------------------------
    // Public Methods
    // ------------------------------------------------------------------------

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
            return new URL(String.format(FORMAT, z, x, y));
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
