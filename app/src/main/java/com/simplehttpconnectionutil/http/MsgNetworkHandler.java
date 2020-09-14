package com.simplehttpconnectionutil.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


import com.simplehttpconnectionutil.R;
import com.simplehttpconnectionutil.http.helpers.HttpUtility;
import com.simplehttpconnectionutil.http.helpers.Logger;
import com.simplehttpconnectionutil.http.helpers.SimpleHttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MsgNetworkHandler
{
    //Info
    private static final String AES_SECRET_KEY = "";
    public static String IF_BASE_URL /*"http://www.webServerTest.com";*/;
    public static final int TIMEOUT_INTERVAL = 10 * 1000;

    //Code
    public static final int RESULT_CODE_SUCCESS = 200;
    public static final int RESULT_CODE_FAIL = -1;
    public static final int REASON_CODE_NOPROBLEM = 0;
    public static final int REASON_CODE_AUTHERROR = 1;
    public static final int REASON_CODE_JOINERROR = 2;
    public static final int REASON_CODE_LOGINERROR = 3;
    public static final int REASON_CODE_REQUESTERROR = 4;
    public static final int REASON_CODE_MAINTENANCE = 98;
    public static final int REASON_CODE_ERROR = 99;

    //Param
    public static final String PARAM_KEY_REASON_CODE = "reason_code";
    public static final String PARAM_KEY_RESULT 	 = "contacts";
    public static final String PARAM_KEY_RESULT_LIST = "ArrayList";

    //Url
    public static final String IF_JOIN_URL = IF_BASE_URL + "/join";
    public static final String IF_LOGIN_URL = IF_BASE_URL + "/login";


    //Object
    private static MsgNetworkHandler _handler = null;
    private static Context _context = null;
    private static HttpUtility _http = null;
    private static ErrorHandler _errorHandler = null;

    //default constructor
    private MsgNetworkHandler()
    {
        HttpUtility.setConnectionTimeout(TIMEOUT_INTERVAL);
        HttpUtility.setReadTimeout(TIMEOUT_INTERVAL);

        _http = HttpUtility.getInstance();
    }

    //singleton
    public synchronized static MsgNetworkHandler getInstance(Context context)
    {
        if(_handler == null)
        {
            _context = context;
            _handler = new MsgNetworkHandler();
        }
        return _handler;
    }

    //set error handler
    public void setErrorHandler(ErrorHandler handler)
    {
        _errorHandler = handler;
    }

    //helper functions

    /**
     *
     * @return null if error
     */
    private static String getAppVersion()
    {
        try
        {
            String version = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0).versionName;
            if ( version == null ) version = "";
            return version;
        }
        catch(Exception e)
        {
            Logger.e(e.toString());
            return "";
        }
    }


    public String requestImage(ImageHandler handler, String Url) {
        return _http.getAsync(handler, Url, null, null);
    }

    public String requestPost(JsonResponseHandler handler, String Url) {
        final String IF_POST_URL = Url;
        HashMap<String, Object> params = new HashMap<String, Object>();
        return _http.postAsync(handler, IF_POST_URL, null, params);
    }


    public String requestPost(JsonResponseHandler handler) {
        final String IF_POST_URL = IF_BASE_URL + "/api/test.json";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("header1", "value1");
        params.put("header2", "value2");
        return _http.postAsync(handler, IF_POST_URL, null, params);
    }

    public String requestGet(JsonResponseHandler handler, String Url) {
        final String IF_GET_URL = Url;
        HashMap<String, Object> params = new HashMap<String, Object>();
        return _http.getAsync(handler, IF_GET_URL, null, null);
    }


    public String requestGet(JsonResponseHandler handler) {
        final String IF_GET_URL = IF_BASE_URL + "/api/test.json";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("header1", "value1");
        params.put("header2", "value2");
        return _http.getAsync(handler, IF_GET_URL, null, params);
    }



    public static abstract class ErrorHandler extends Handler
    {
        public abstract void httpFailOccurred(String errorMessage);
        public abstract void errorOccurred(String errorMessage);
        public abstract void authErrorOccurred(String errorMessage);
        public abstract void loginErrorOccurred(String errorMessage);
        public abstract void joinErrorOccurred(String errorMessage);
    }

    public static abstract class ImageHandler extends Handler
    {

        abstract protected void ImageHandler(String httpTaskId, boolean success, byte[] data, String extraMessage);

        @Override
        public void handleMessage(Message msg) {
            String taskId = msg.getData().getString(HttpUtility.ASYNC_HTTP_TASK_ID);

            String extraMessage     = null;
            byte[] arrResponByte    = null;
            int httpStatusCode      = msg.arg1;
            int resultCode          = RESULT_CODE_FAIL;

            if ( httpStatusCode != RESULT_CODE_FAIL ) {
                arrResponByte  = ((SimpleHttpResponse)msg.getData().getParcelable(HttpUtility.ASYNC_HTTP_RESULT)).getHttpResponseBody();
                resultCode = RESULT_CODE_SUCCESS;
            }

            if(httpStatusCode != RESULT_CODE_SUCCESS || resultCode == RESULT_CODE_FAIL) {
                _errorHandler.errorOccurred(_context.getString(R.string.msg_error));
            }

            if(arrResponByte instanceof byte[]) {
                ImageHandler(taskId, true, arrResponByte, extraMessage);
            }
        }
    }


    public static abstract class JsonResponseHandler extends Handler
    {
        abstract protected void receivedJsonResult(String httpTaskId, boolean success, JSONObject json, String extraMessage);
        abstract protected void receivedJsonResultAsArray(String httpTaskId, boolean success, JSONArray json, String extraMessage);

        @Override
        public void handleMessage(Message msg)
        {
            String taskId = msg.getData().getString(HttpUtility.ASYNC_HTTP_TASK_ID);
            try
            {
                JSONObject json     = null;
                int httpStatusCode  = msg.arg1;
                String extraMessage = null;

                if(httpStatusCode == RESULT_CODE_SUCCESS)
                {
                    json = new JSONObject(((SimpleHttpResponse)msg.getData().getParcelable(HttpUtility.ASYNC_HTTP_RESULT)).getHttpResponseBodyAsString());

                    Object result = null;

                    if(!json.isNull(PARAM_KEY_RESULT)) 		            result = json.get(PARAM_KEY_RESULT);
                    else if(!json.isNull(PARAM_KEY_RESULT_LIST)) 		result = json.get(PARAM_KEY_RESULT_LIST);

                    if(result instanceof JSONObject)
                    {
                        receivedJsonResult(taskId, true, (JSONObject)result, "JsonObject");
                        return;
                    }
                    else if(result instanceof JSONArray)
                    {
                        receivedJsonResultAsArray(taskId, true, (JSONArray)result, "JsonArray");
                        return;
                    }
                    else
                    {
                        //내부 Error 처리
                        int reasonCode = json.getInt(PARAM_KEY_REASON_CODE);
                        if(_errorHandler != null)
                        {
                            if(reasonCode == REASON_CODE_AUTHERROR) 		{_errorHandler.authErrorOccurred(_context.getString(R.string.msg_autherror));}
                            else if(reasonCode == REASON_CODE_JOINERROR) 	{_errorHandler.joinErrorOccurred(_context.getString(R.string.msg_joinerror));}
                            else if(reasonCode == REASON_CODE_LOGINERROR) 	{_errorHandler.loginErrorOccurred(_context.getString(R.string.msg_loginerror));}
                            else
                            {
                                String errorMessage = null;
                                switch(reasonCode)
                                {
                                    case REASON_CODE_REQUESTERROR:	    errorMessage = _context.getString(R.string.msg_requesterror);	break;
                                    case REASON_CODE_MAINTENANCE:	    errorMessage = _context.getString(R.string.msg_maintenance);	    break;
                                    case REASON_CODE_ERROR:	 default:	errorMessage = _context.getString(R.string.msg_error);	break;
                                }
                                _errorHandler.errorOccurred(errorMessage);
                            }
                        }
                        _errorHandler.errorOccurred(_context.getString(R.string.msg_data));
                        return;
                    }
                }
                else
                {
                    //통신 Error 처리
                    if(_errorHandler != null)
                        _errorHandler.httpFailOccurred(_context.getString(R.string.msg_fail));
                }
            }
            catch(JSONException e)
            {
                e.printStackTrace();
                Logger.e(e.toString());
                receivedJsonResult(taskId, false, null, null);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Logger.e(e.toString());
                receivedJsonResult(taskId, false, null, null);
            }
        }
    }
}