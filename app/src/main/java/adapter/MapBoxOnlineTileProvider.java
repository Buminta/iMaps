package adapter;

import com.google.android.gms.maps.model.UrlTileProvider;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by me866chuan on 8/27/14.
 */
public class MapBoxOnlineTileProvider extends UrlTileProvider {

    private static final String FORMAT;

    static {
//        FORMAT = "http://api.tiles.mapbox.com/v3/%s/%d/%d/%d.png";
        FORMAT = "http://222.255.28.13:8080/wmslight/wms.ashx?layer=app&z=%d&x=%d&y=%d";
    }

    // ------------------------------------------------------------------------
    // Instance Variables
    // ------------------------------------------------------------------------

    private String mMapIdentifier;
    private int x;
    private int y;
    private int z;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    public MapBoxOnlineTileProvider() {
        super(256, 256);
    }

    // ------------------------------------------------------------------------
    // Public Methods
    // ------------------------------------------------------------------------

    @Override
    public URL getTileUrl(int x, int y, int z) {
        try {
//            return new URL(String.format(FORMAT, this.mMapIdentifier, z, x, y));
            return new URL(String.format(FORMAT, z, x, flip_y(z, y)));
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    public int flip_y(int z, int y){
        return (int) (Math.pow(2, z) - 1 - y);
    }

}