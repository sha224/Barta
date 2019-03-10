package app.barta.barta;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private LocationManager locationManager;
    private Location deviceLocation;
    private List<Post> posts;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocation();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, null);
        recyclerView = fragmentView.findViewById(R.id.postListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchPosts();
        return fragmentView;
    }

    public void updateLocation() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No location permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location changed to " + location);
                deviceLocation = location;
                if (posts != null)
                    recyclerView.setAdapter(new PostListAdapter(posts, deviceLocation));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        }, null);
    }

    public void fetchPosts() {
        String url = "http://10.0.2.2:8080/posts?projection=details";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    posts = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray postsJson = jsonObject.getJSONObject("_embedded").getJSONArray("posts");
                    for (int i = 0; i < postsJson.length(); i++) {
                        JSONObject postJson = postsJson.getJSONObject(i);
                        JSONObject locationJson = postJson.getJSONObject("location");
                        Post post = new Post(postJson.getString("text"), postJson.getInt("upvotes"), postJson.getInt("downvotes"), postJson.getInt("commentCount"), new Post.Point(locationJson.getDouble("x"), locationJson.getDouble("y")), postJson.getString("creationTime"));
                        posts.add(post);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Finished fetching posts");
                if (deviceLocation != null)
                    recyclerView.setAdapter(new PostListAdapter(posts, deviceLocation));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error while fetching posts", error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
            updateLocation();
    }
}
