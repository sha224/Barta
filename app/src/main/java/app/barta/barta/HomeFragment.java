package app.barta.barta;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_home, null);
        RecyclerView recyclerView = (RecyclerView) fragmentView.findViewById(R.id.postListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fetchPosts(recyclerView);
        return fragmentView;
    }

    public void fetchPosts(final RecyclerView recyclerView) {
        final List<Post> posts = new ArrayList<>();
        String url = "http://10.0.2.2:8080/posts?projection=details";
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("-------------------------------------------------------------------------------" + response);
                try {
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

                recyclerView.setAdapter(new PostListAdapter(posts));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("-------------------------------------------------------------------------------" + error);
                recyclerView.setAdapter(new PostListAdapter(posts));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}
