package adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by me866chuan on 8/23/14.
 */
public class ApiMapsAdapter{

    private String DOMAIN_API = "http://staging.skipfile.com:3000/";

    public String loginUser(String username, String password){



        String token = "";
        return token;
    }

    public String registerUser(String username, String password){
        String url = DOMAIN_API+"register";


        String token = "";
        return token;
    }

    public String loginFB() throws IOException, JSONException {
        String strUrl = DOMAIN_API+"login/facebook";

        String token = "";
        return token;
    }
}
