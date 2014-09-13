package net.windjs.imaps;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ApiDataAdapter;
import adapter.DirectionsJSONParser;
import adapter.MapBoxMixedTileProvider;
import adapter.PlaceDetailsJSONParser;
import adapter.PlaceJSONParser;
import adapter.PopupMarkerAdapter;
import global.GlobalClass;
import model.MultiDrawable;
import model.MyItem;
import model.MyToast;
import model.ObjectItem;


public class MapsFragment extends Fragment {

    private ClusterManager<MyItem> mClusterManager;
    private GoogleMap map;
    private View rootView;
    private LayoutInflater inflater;
    private LatLng myLocation, desLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker marker;
    private Polyline polyline;
    private ListView list_position;
    private EditText textSearch;
    private boolean startLocation = false;
    private String mySpeed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.maps_fragment, container, false);
//        list_position = (ListView) rootView.findViewById(R.id.list_location);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NONE);
        try {
            overlayMap();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);

        mClusterManager = new ClusterManager<MyItem>(rootView.getContext(), map);
        mClusterManager.setRenderer(new MyItemRenderer());

        mapListener();

        listenerEvent();
        onGetSpeed();

        return rootView;
    }

    public LatLng getMyLocation() {
        return myLocation;
    }

    public String getMySpeed() {
        return mySpeed;
    }

    private void mapListener() {
//        map.setInfoWindowAdapter(new PopupMarkerAdapter(getActivity().getLayoutInflater()));
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mClusterManager.onMarkerClick(marker);
                return false;
            }
        });
        map.setOnInfoWindowClickListener(mClusterManager);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
//                textSearch.clearFocus();
//                list_position.setAdapter(null);
            }
        });

        mClusterManager = new ClusterManager<MyItem>(rootView.getContext(), map);
        mClusterManager.setRenderer(new MyItemRenderer());
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager.onCameraChange(cameraPosition);
                fixMapView(cameraPosition);
            }
        });

        map.setOnMarkerClickListener(mClusterManager);
        map.setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                showMarkerPop(item);
                return false;
            }
        });
    }

    private void fixMapView(CameraPosition cameraPosition) {
        float z = cameraPosition.zoom;
        double lat = cameraPosition.target.latitude;
        double longi = cameraPosition.target.longitude;

        if (z < 5.0) z = 5;
        if (z > 18) z = 18;

        if (lat < 5) lat = 5;
        if (lat > 25) lat = 25;
        if (longi < 100) longi = 100;
        if (longi > 115) longi = 115;

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, longi), z));
        LatLngBounds visibleBounds = map.getProjection().getVisibleRegion().latLngBounds;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) rootView.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void listenerEvent() {

        FrameLayout info = (FrameLayout) rootView.findViewById(R.id.showContentMarker);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDetailsMarket(v);
            }
        });

//        textSearch = (EditText) rootView.findViewById(R.id.editText);
//        textSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    hideKeyboard(v);
//                }
//            }
//        });
//
//        textSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                String textS = textSearch.getText().toString();
//                String url = ApiDataAdapter.getPlaceCompleteUrl(textS);
//                CompleteSearch task = new CompleteSearch();
//                task.execute(url);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        list_position.setOnItemClickListener(new ListPositionClickListener());
    }

//    private class ListPositionClickListener implements
//            ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            ArrayAdapterItem adapter = (ArrayAdapterItem) parent.getAdapter();
//            ObjectItem obj = adapter.getSelectObject(position);
//            textSearch.setText(obj.itemName);
//            CompleteDetails task = new CompleteDetails();
//            task.execute(ApiDataAdapter.getPlaceDetailUrl(obj.stringId));
//        }
//    }

//    private class CompleteDetails extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            String data = "";
//
//            try {
//                data = ApiDataAdapter.downloadUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                List<HashMap<String, String>> routes = null;
//                JSONObject jsonObject = new JSONObject(result);
//                PlaceDetailsJSONParser parser = new PlaceDetailsJSONParser();
//                routes = parser.parse(jsonObject);
//                desLocation = new LatLng(Double.valueOf(routes.get(0).get("lat")), Double.valueOf(routes.get(0).get("lng")));
//                makeDirections(myLocation, desLocation);
//                textSearch.clearFocus();
//                list_position.setAdapter(null);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void makeDirections(LatLng from, LatLng to) {
        try {
            polyline.remove();
            marker.remove();
        } catch (Exception e) {

        }
        MarkerOptions options = new MarkerOptions();
        options.position(to);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        marker = map.addMarker(options);
        String url = ApiDataAdapter.getDirectionsUrl(from, to);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);
    }

//    private class CompleteSearch extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            String data = "";
//
//            try {
//                data = ApiDataAdapter.downloadUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            try {
//                List<HashMap<String, String>> routes = null;
//                JSONObject jsonObject = new JSONObject(result);
//                PlaceJSONParser parser = new PlaceJSONParser();
//                routes = parser.parse(jsonObject);
//                ObjectItem[] ObjectItemData = new ObjectItem[routes.size()];
//                for (int i = 0; i < routes.size(); i++) {
//                    ObjectItemData[i] = new ObjectItem(routes.get(i).get("place"), routes.get(i).get("description"));
//                }
//
//                ArrayAdapterItem adapter = new ArrayAdapterItem(rootView.getContext(), R.layout.list_view_item, ObjectItemData);
//                list_position.setAdapter(adapter);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager.removeUpdates(locationListener);
        MapFragment f = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        if (f != null)
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {

            }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = ApiDataAdapter.downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            JSONObject jObject = null;
            try {
                jObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (jObject != null) {
                JSONObject routesJSON = null;
                try {
                    routesJSON = (JSONObject) jObject.getJSONArray("routes").get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (routesJSON != null) {
                    JSONArray legsJSON = null;
                    try {
                        legsJSON = routesJSON.getJSONArray("legs");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (legsJSON != null) {
                        JSONObject des = null;
                        try {
                            des = (JSONObject) legsJSON.get(legsJSON.length() - 1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (des != null) {
                            JSONObject desDetails = null;
                            try {
                                desDetails = (JSONObject) des.get("distance");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (desDetails != null) {
                                MyToast toast = new MyToast(rootView.getContext());
                                toast.setTitle("Ước tính quãng đường");
                                try {
                                    String tmp = "";
                                    Double km = Double.valueOf(desDetails.getString("value")) / 1000.00;
                                    tmp += "Độ dài: " + String.valueOf(km) + " km";
                                    tmp += " - ";
                                    if (Double.valueOf(mySpeed) != 0.00) {
                                        Double hour = km / Double.valueOf(mySpeed);
                                        tmp += "Thời gian: " + String.valueOf(hour) + " giờ";
                                    } else {
                                        tmp += "Thời gian: không xác định";
                                    }
                                    toast.setText(tmp);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                toast.setGravity(Gravity.TOP, 0, 100);
                                toast.setDuration(MyToast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }
            }

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = new PolylineOptions();
            try {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.zIndex(1);
                    lineOptions.geodesic(true);
                    lineOptions.color(Color.rgb(0, 100, 255));
                }

                polyline = map.addPolyline(lineOptions);
            } catch (Exception e) {
                Context context = rootView.getContext();
                map.clear();
                MyToast toast = new MyToast(context);
                toast.setTitle("Fail");
                toast.setText("Can't get data");
                toast.setGravity(Gravity.TOP, 0, 100);
                toast.setDuration(MyToast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void onGetSpeed() {
        final GlobalClass globalClass = (GlobalClass) getActivity().getApplicationContext();
        locationManager = (LocationManager) rootView.getContext().getSystemService(Context.LOCATION_SERVICE);
//        final TextView textSpeed = (TextView) rootView.findViewById(R.id.speec_value);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mySpeed = String.valueOf(location.getSpeed());
//                textSpeed.setText(mySpeed);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                myLocation = latLng;

                if(globalClass.isDrivingBoard()){
                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    DrivingFragment driving = (DrivingFragment) getFragmentManager().findFragmentById(R.id.frame_fragment);
                    driving.setSpeed(location.getSpeed());
                }
                if (desLocation != null) {
                    makeDirections(myLocation, desLocation);
                }
                if (!startLocation) {
                    startLocation = true;
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private class MyItemRenderer extends DefaultClusterRenderer<MyItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(rootView.getContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(rootView.getContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public MyItemRenderer() {
            super(rootView.getContext(), map, mClusterManager);

            View multiProfile = inflater.inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(rootView.getContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem myItem, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageResource(myItem.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(myItem.name);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (MyItem p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            return cluster.getSize() > 1;
        }
    }

    private void overlayMap() throws Exception {
        TileOverlayOptions opts = new TileOverlayOptions();
        opts.tileProvider(new MapBoxMixedTileProvider(rootView.getContext()));
        opts.zIndex(0);
        TileOverlay overlay = map.addTileOverlay(opts);
    }

    public void addItemCluster(MyItem item) {
        mClusterManager.addItem(item);
    }

    public void cluster() {
        mClusterManager.cluster();
    }

    public void clearCluster() {
        FrameLayout info = (FrameLayout) rootView.findViewById(R.id.showContentMarker);
        Animation inAnim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.abc_fade_out);
        info.setVisibility(View.GONE);
        info.startAnimation(inAnim);
        mClusterManager.clearItems();
    }

    public void showMarkerPop(MyItem item){
        FrameLayout info = (FrameLayout) rootView.findViewById(R.id.showContentMarker);

        ((TextView) info.findViewById(R.id.textName)).setText(item.name);
        ((TextView) info.findViewById(R.id.textLocation)).setText(item.getPosition().toString());

        Animation inAnim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.abc_fade_in);
        info.setVisibility(View.VISIBLE);
        info.startAnimation(inAnim);
    }

    public void viewDetailsMarket(View v) {
        GlobalClass globalVariable = (GlobalClass) getActivity().getApplicationContext();
        globalVariable.setOldFragmentS("details_market");
        Fragment fragmentOld = getFragmentManager().findFragmentById(R.id.frame_fragment);
        if (fragmentOld != null)
            try {
                getFragmentManager().beginTransaction().remove(fragmentOld).commit();
            } catch (Exception e) {

            }

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new DetailsMarketFragment();
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_fragment, fragment).commit();
        }

        FrameLayout frame = (FrameLayout) getActivity().findViewById(R.id.frame_fragment);
        Animation inAnim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.abc_slide_in_bottom);
        Animation inAnimBG = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.abc_fade_in);
        FrameLayout frameBG = (FrameLayout) getActivity().findViewById(R.id.frame_fragment_bg);
        frame.setVisibility(View.VISIBLE);
        frame.startAnimation(inAnim);
        frameBG.setVisibility(View.VISIBLE);
        frameBG.startAnimation(inAnimBG);
    }
}
