package app.barta.barta;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";

    private Location deviceLocation;
    private String userUrl;
    private TextView postTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        deviceLocation = getIntent().getParcelableExtra(MainActivity.EXTRA_LOCATION);
        userUrl = getIntent().getStringExtra(MainActivity.EXTRA_USER_URL);
        postTextInput = findViewById(R.id.postTextInput);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions_compose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.composeDoneAction) {
            createPost(postTextInput.getText().toString());
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void createPost(String postText) {
        String url = BuildConfig.API_URL + "/posts";

        String requestBody = null;

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("text", postText);
            JSONObject location = new JSONObject();
            location.put("x", deviceLocation.getLongitude());
            location.put("y", deviceLocation.getLatitude());
            jsonBody.put("location", location);
            jsonBody.put("author", userUrl);
            requestBody = jsonBody.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error while making JSON Body", e);
        }

        final String request = requestBody;
        Log.d(TAG, "Request: " + request);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error Response", error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return request.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "Error while setting body", e);
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Log.d(TAG, "Status Code: " + response.statusCode);
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
