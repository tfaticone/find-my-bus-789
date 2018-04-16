//
//        import android.os.AsyncTask;
//        import android.util.JsonReader;
//
//        import java.io.IOException;
//        import java.io.InputStream;
//        import java.io.InputStreamReader;
//        import java.io.UnsupportedEncodingException;
//        import java.net.MalformedURLException;
//        import java.net.URL;
//        import javax.net.ssl.HttpsURLConnection;
//
//public class RouteConnection {
//
//    AsyncTask.execute(new public RouteConnection() {
//        public void run(){
//            // Create URL
//            URL RTSEndpoint;
//            try {
//                RTSEndpoint = new URL("http://api.rgrta.com/");
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//
//            // Create connection
//            HttpsURLConnection myConnection = null;
//            try {
//                myConnection = (HttpsURLConnection) RTSEndpoint.openConnection();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
//            myConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");
//            myConnection.setRequestProperty("Contact-Me", "hathibelagal@example.com");
//
//            if (myConnection.getResponseCode() == 200) {
//                // Success
//                // Further processing here
//                InputStream responseBody = myConnection.getInputStream();
//                InputStreamReader responseBodyReader =
//                        null;
//                try {
//                    responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//
//                }
//                JsonReader jsonReadere.printStackTrace(); = new JsonReader(responseBodyReader);
//            } else {
//                // Error handling code goes here
//            }
//        }
//    }
//}
package edu.rit.se.www.findmybus.API;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import edu.rit.se.www.findmybus.R;

public class RouteConnection extends AppCompatActivity{
    TextView routeId; // This will be a reference to our GitHub username input.
    TextView routeList;  // This will reference our repo list text box.
    RequestQueue requestQueue;  // This is our requests queue to process our HTTP requests.

    String baseUrl = "http://api.rgrta.com/rtroutes?key=d0eac034-06b4-4c51-877a-3f21119b87e7";  // This is the API base URL (GitHub API)
    String url;  // This will hold the full URL which will include the username entered in the etGitHubUser.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_current_bus_info);

        this.routeId = (TextView) findViewById(R.id.routeID);  // Link our route text box.
        this.routeList = (TextView) findViewById(R.id.routeList);  // Link our repository list text output box.
        this.routeList.setMovementMethod(new ScrollingMovementMethod());  // This makes our text box scrollable, for those big GitHub contributors with lots of repos :)

        requestQueue = Volley.newRequestQueue(this);  // This setups up a new request queue which we will need to make HTTP requests.
    }

    private void clearRouteList() {
        // This will clear the repo list (set it as a blank string).
        this.routeList.setText("");
    }

    private void addToRouteList(String routeNumber) {
        // This will add a new repo to our list.
        // It combines the repoName and lastUpdated strings together.
        // And then adds them followed by a new line (\n\n make two new lines).
        String currentText = routeList.getText().toString();
        this.routeList.setText(currentText + "\n\n" + routeNumber);
    }

    private void setRouteListText(String str) {
        // This is used for setting the text of our repo list box to a specific string.
        // We will use this to write a "No repos found" message if the user doens't have any.
        this.routeList.setText(str);
    }

    private void getRouteList(String routeNumber) {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).
        this.url = this.baseUrl + "&routeid=" + routeNumber;

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {
                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // For each repo, add a new line to our repo list.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String routeName = jsonObj.get("RouteName").toString();
                                    addToRouteList(routeName);
                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            // The user didn't have any repos.
                            setRouteListText("No repos found.");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        setRouteListText("Error while calling REST API");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

    public void getRoutesClicked(View v) {
        // Clear the repo list (so we have a fresh screen to add to)
        Log.e("basic","get Routes clicked");
        clearRouteList();
        // Call our getRepoList() function that is defined above and pass in the
        // text which has been entered into the etGitHubUser text input field.
        getRouteList(routeId.getText().toString());
    }
}