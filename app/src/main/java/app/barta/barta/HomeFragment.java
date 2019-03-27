package app.barta.barta;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private List<Post> posts;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public void fetchPosts() {
        String url = BuildConfig.API_URL + "/posts";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
                try {
                    posts = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray postsJson = jsonObject.getJSONObject("_embedded").getJSONArray("posts");
                    for (int i = 0; i < postsJson.length(); i++) {
                        JSONObject postJson = postsJson.getJSONObject(i);
                        String postUrl = postJson.getJSONObject("_links").getJSONObject("self").getString("href");
                        JSONObject locationJson = postJson.getJSONObject("location");
                        Post post = new Post(postUrl, postJson.getString("text"), postJson.getInt("upvotes"), postJson.getInt("downvotes"), postJson.getInt("commentCount"), new Post.Point(locationJson.getDouble("x"), locationJson.getDouble("y")), postJson.getString("creationTime"));
                        posts.add(post);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error while parsing JSON", e);
                }

                Log.d(TAG, "Finished fetching posts");
                Location deviceLocation = ((MainActivity) getContext()).getDeviceLocation();
                if (deviceLocation != null)
                    recyclerView.setAdapter(new PostListAdapter(getContext(), posts, deviceLocation));
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
}
