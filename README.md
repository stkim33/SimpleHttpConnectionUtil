## SimpleHttpConnectionUtil 

### Language : Java
### IDE : Android Studio

#### Simple Http Json Connection Object Init

```Java
 //Object
    private NetworkErrorHandler                     errorHandler  = new NetworkErrorHandler();          //Http Error Handler
    private MsgNetworkHandler.JsonResponseHandler   jsonHandler   = new JsonResponseHandler();          //Json Handler
    private ImageResponseHandler                    ImageHandler  = new ImageResponseHandler();;        //Image Handler

    private String strImageUrlTest         =  "https://ImageSampleUrl.co.kr";
    private String strJsonUrlTest          =  "https://JsonSampleUrl.co.kr";
```

####  Simple Http Json Connection Object Function Call
```Java
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
```


####  Http Json Response Handler class
```Java
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
```
