package adapter;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by me866chuan on 8/23/14.
 */
public class ApiDataAdapter{

    private static String place_key = "AIzaSyASy7gM6fRddAvWD7aJPGUcDO5UT8xujJk";

    public static String getPlaceCompleteUrl(String txt){
        String str = "input="+txt;

        String key = "key="+place_key;

        String type = "types=geocode";

        String parameters = str+"&"+type+"&"+key;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

        return url;
    }

    public static String getPlaceDetailUrl(String txt){
        String str = "placeid="+txt;
        String key = "key="+place_key;
        String parameters = str+"&"+key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

        return url;
    }

    public static String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";

        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            try{
                iStream.close();
                urlConnection.disconnect();
            }
            catch (Exception e){

            }
        }
        return data;
    }
}
