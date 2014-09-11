package net.windjs.imaps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import adapter.ImageAdapter;
import model.MyItem;

public class LocationsFragment extends Fragment {
    private View rootView;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.location_fragment, container, false);

        final MapsFragment mapFragment = ((MapsFragment) getFragmentManager().findFragmentById(R.id.frame_container));
        final StartActivity activity = (StartActivity) getActivity();


        ImageAdapter adapter = new ImageAdapter(getActivity());
        adapter.setmThumbIds(new Integer[]{
                R.drawable.rounded_border,
                R.drawable.rounded_border,
                R.drawable.rounded_border,
                R.drawable.rounded_border,
        });

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                LatLng myLocation = mapFragment.getMyLocation();
                double latValue = myLocation.latitude;
                double longValue = myLocation.longitude;

                mapFragment.clearCluster();

                for (int i = 0; i < Math.random() * 20; i++) {
                    LatLng tmp = new LatLng(latValue + 0.003 * Math.random(), longValue + 0.003 * Math.random());
                    MyItem icon = new MyItem(tmp, "My Item", R.drawable.ic_launcher);
                    mapFragment.addItemCluster(icon);
                }

                mapFragment.cluster();

                activity.placeHide();

                //Su dung khi co du lieu that
//                switch (position) {
//                    case 0: {
//                        break;
//                    }
//                    case 1: {
//                        break;
//                    }
//                    case 2: {
//                        break;
//                    }
//                    case 3: {
//                        break;
//                    }
//                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
