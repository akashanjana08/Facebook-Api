package agri.com.facebookapiexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker mProfileTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile","public_profile", "user_birthday", "user_friends"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        getFacebookProfileDetails(loginResult);
                      //Toast.makeText(MainActivity.this,loginResult.getAccessToken().getUserId(),Toast.LENGTH_SHORT).show();
                        getAlbums();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


   void  getFacebookProfileDetails(LoginResult loginResult)
    {

        String accessToken =  loginResult.getAccessToken().getToken();
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                            System.out.println("ERROR");
                        } else {
                            System.out.println("Success");
                            try {

                                String jsonresult = String.valueOf(json);
                                System.out.println("JSON Result" + jsonresult);

                                //String str_email = json.getString("email");
                                String profileId = json.getString("id");
                                String str_firstname = json.getString("first_name");
                                String str_lastname = json.getString("last_name");
                                String name = json.getString("name");
                                String uri = null;


                                JSONObject data = response.getJSONObject();
                                if (data.has("picture"))
                                {
                                    uri = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                    String datas = "Data";
                                    // set profile image to imageview using Picasso or Native methods
                                }

                            } catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,first_name,last_name,gender,picture.width(100).height(100)");
        request.setParameters(parameters);
        request.executeAsync();


    }

    List<FbAlbumItem> listAlbum;
    public void getAlbums(){

        listAlbum = new ArrayList<>();
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        if(object!=null) {
                            try {

                                JSONObject obj = object.getJSONObject("albums");
                                JSONArray jArray = obj.getJSONArray("data");
                                for(int i =0;i<jArray.length();i++){
                                    FbAlbumItem album = new FbAlbumItem();
                                    JSONObject dataObj = jArray.getJSONObject(i);
                                    album.setAlbumId(dataObj.getString("id"));
                                    album.setAlbumName(dataObj.getString("name"));
                                    album.setImageUrl("https://graph.facebook.com/" + dataObj.getString("cover_photo") + "/picture?type=normal"
                                            + "&access_token=" + AccessToken.getCurrentAccessToken().getToken());
                                    listAlbum.add(album);
                                }
                                albumlist(listAlbum);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{

                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "albums{id,cover_photo,name}");
        request.setParameters(parameters);
        request.executeAsync();

    }


    void albumlist(List<FbAlbumItem> listAlbum){

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos"));
        this.listAlbum = listAlbum;
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+listAlbum.get(0).getAlbumId()+"/photos",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                      /* handle the result */
                        getPhoto();
                    }
                }
        ).executeAsync();
    }


    void getPhoto()
    {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_photos"));

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/1951746661618566/photos",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                       /* handle the result */
                        String data = "Data";
                    }
                }
        ).executeAsync();
    }
}
