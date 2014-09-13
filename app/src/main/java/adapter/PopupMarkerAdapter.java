package adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import net.windjs.imaps.R;

/**
 * Created by me866chuan on 9/12/14.
 */
public class PopupMarkerAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup = null;
    private LayoutInflater inflater = null;

    public PopupMarkerAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (popup == null) {
            popup = inflater.inflate(R.layout.info_marker, null);
        }

        TextView tv = (TextView) popup.findViewById(R.id.textName);

        tv.setText("Test");
        tv = (TextView) popup.findViewById(R.id.textLocation);
        tv.setText("Snip");

        return (popup);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
