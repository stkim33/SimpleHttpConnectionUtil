package com.simplehttpconnectionutil;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.simplehttpconnectionutil.http.MsgImageHandler;
import com.simplehttpconnectionutil.http.MsgNetworkHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Object
    private NetworkErrorHandler                     errorHandler  = new NetworkErrorHandler();          //Http Error Handler
    private MsgNetworkHandler.JsonResponseHandler   jsonHandler   = new JsonResponseHandler();          //Json Handler
    private ImageResponseHandler                    ImageHandler  = new ImageResponseHandler();;        //Image Handler

    private String strImageUrlTest         =  "https://cloud.google.com/bigquery/images/create-schema-array.png?hl=ko";
    private String strJsonUrlTest          =  "https://api.androidhive.info/contacts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Http Image Get Test
        ArrayList<String> strArray = new ArrayList<String>();
        strArray.add(strImageUrlTest);
        MsgImageHandler.getInstance(this).OnSend(ImageHandler, strArray);

        //Http Json Get Test
        MsgNetworkHandler.getInstance(this).setErrorHandler(errorHandler);
        MsgNetworkHandler.getInstance(this).requestGet(jsonHandler, strJsonUrlTest);

    }

    /** Image Response Data class **/
    private class ImageResponseHandler implements MsgImageHandler.ImageHandler {

        @Override
        public void ImageSuccessResponse(MsgImageHandler.MyBitmap bitmap) {

             if(bitmap.getUrl() == strImageUrlTest) ((ImageView)(findViewById(R.id.imageView))).setImageBitmap(bitmap.getBitMap());
        }
    }

    /** Json Response Data class **/
    private class JsonResponseHandler extends MsgNetworkHandler.JsonResponseHandler {

        @Override
        protected void receivedJsonResult(String httpTaskId, boolean success, JSONObject json, String extraMessage) {
            if(success && json != null) {
                try {
                    JSONArray parkList = json.getJSONArray("returnList");
                    JSONObject JSonTemp;
                    if (parkList == null)   return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void receivedJsonResultAsArray(String httpTaskId, boolean success, JSONArray json, String extraMessage) {
            if (success && json != null) {
                Toast toast = Toast.makeText(MainActivity.this, extraMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    /** Network Error Data class **/
    private class NetworkErrorHandler extends MsgNetworkHandler.ErrorHandler {
        @Override
        public void httpFailOccurred(String errorMessage) { Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT);   toast.show();}
        @Override
        public void errorOccurred(String errorMessage) { Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT);   toast.show();}
        @Override
        public void authErrorOccurred(String errorMessage) { Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT); toast.show();}
        @Override
        public void loginErrorOccurred(String errorMessage) {Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT);toast.show();}
        @Override
        public void joinErrorOccurred(String errorMessage) {Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT);toast.show();}
    }
}
