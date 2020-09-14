package com.simplehttpconnectionutil.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


import java.util.ArrayList;

public class MsgImageHandler extends AsyncTask<ArrayList<String>, Void, Bitmap[]> {
    private static final String TAG = "SimpleHttpImage";

    public class MyBitmap
    {
        String                  strTaskId;
        String                  Url;
        Bitmap                  bitMap;

        public Bitmap   getBitMap()         { return bitMap;}
        public String   getTaskId()         { return strTaskId;}
        public String   getUrl()            { return Url;}
    }

    private ImageHandler                imageCallBack; //Json
    private ResponseHandler             responseHandler = null;

    private ArrayList<MyBitmap>         arrListBitmap;
    private Bitmap[]                    bitmap;

    private static MsgImageHandler _handler = null;
    private static Context              _context;

    public MsgImageHandler()
    {
        responseHandler = new ResponseHandler();
        arrListBitmap = new ArrayList<MyBitmap>();
    }

    //singleton
    public synchronized static MsgImageHandler getInstance(Context context)
    {
        if(_handler == null)
        {
            _context = context;
            _handler = new MsgImageHandler();
        }
        return _handler;
    }

    public interface ImageHandler {    public void ImageSuccessResponse(MyBitmap bitmap);  }

    public void setOnJsonListener(ImageHandler callBack) {
        imageCallBack = callBack;
    }


    private class ResponseHandler extends MsgNetworkHandler.ImageHandler {

        @Override
        protected void ImageHandler(String httpTaskId, boolean success, byte[] data, String extraMessage) {
            MyBitmap Bitmap = FindHttpImageTask(httpTaskId);
            if(Bitmap != null) {
                Bitmap.bitMap = byteArrayToBitmap(data);
                imageCallBack.ImageSuccessResponse(Bitmap);
            }
        }
    }

    @Override
    protected Bitmap[] doInBackground(ArrayList<String>... strImageUrl) {

        ArrayList<String> BitmapList = strImageUrl[0];

        for (int i = 0; i < BitmapList.size(); i++) {
            MyBitmap Bitmap= new MyBitmap();
            Bitmap.Url = BitmapList.get(i);
            Bitmap.strTaskId = MsgNetworkHandler.getInstance(_context).requestImage(responseHandler, BitmapList.get(i));
            arrListBitmap.add(Bitmap);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap[] bitmaps) {
        super.onPostExecute(bitmaps);

    }

    public Bitmap byteArrayToBitmap( byte[] byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length ) ;
        return bitmap ;
    }

    public MyBitmap FindHttpImageTask(String strHttpTaskId)
    {
        MyBitmap myBitmap = null;
        for (int i = 0; i <= arrListBitmap.size(); i++) {
            myBitmap = arrListBitmap.get(i);
            if(myBitmap.getTaskId() == strHttpTaskId)
                return myBitmap;
        }
        return null;
    }

    public void OnSend(ImageHandler handler, ArrayList<String> strArray)
    {
        execute(strArray);
        setOnJsonListener(handler);
    }
}
