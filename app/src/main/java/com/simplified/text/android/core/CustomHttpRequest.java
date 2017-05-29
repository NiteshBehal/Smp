package com.simplified.text.android.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Wrapper around Volley to send web requests along with headers if any. It provides the option to parse the data into model object using gson or return the raw response as a String.
 */

public class CustomHttpRequest<T> extends Request<T> {
    private String mRequestBody;
    private Map<String, String> mRequestHeader = new HashMap<String, String>();
    private Class<T> mModelClass;
    private final Gson gson = new Gson();
    private static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE = String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private InternalListener mInternalListener;

//    private String credentials = URLConstants.RESTHEARTCREDEN;
//    private String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

    private CustomHttpRequest() {
        super(Method.GET, null, null);
    }

    /**
     * @param method     int specifying service type. ex: Request.Method.GET
     * @param url        URL to hit.
     * @param requestID  manually set id to keep track of the url being hit, it is used to keep track of response object and response error.
     * @param header     header to send, if any.
     * @param body       parameters to send(generally JSONObject) as a String, if any.
     * @param timeOut    timeOut in milliseconds.
     * @param modelClass GSON model class object to populate data, pass null if raw String response is required
     * @param listener   Custom response listener class object, return type of response is an Object which can further be cast to desired type using urlID.
     */
    public CustomHttpRequest(int method, @NonNull String url, int requestID, @Nullable Map<String, String> header, @Nullable String body, int timeOut, @Nullable Class<T> modelClass, HttpResponseListener listener) {
        super(method, url, listener == null ? null : new InternalListener(listener, requestID));
        setShouldCache(false);

        Log.d(">>>>>", url);
        Log.d(">>>>>", ""+body);
        mInternalListener = (InternalListener) this.getErrorListener();
        mRequestBody = body;
        if (header != null) {
            mRequestHeader = header;
        }
//        mRequestHeader.put("Authorization", "Basic " + base64EncodedCredentials);
        mModelClass = modelClass;
        setRetryPolicy(new DefaultRetryPolicy(timeOut, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * @param method     int specifying service type. ex: Request.Method.GET
     * @param url        URL to hit.
     * @param requestID  manually set id to keep track of the url being hit, it is used to keep track of response object and response error.
     * @param header     header to send, if any.
     * @param body       parameters to send(generally JSONObject) as a String, if any.
     * @param modelClass GSON model class object to populate data, pass null if raw String response is required
     * @param listener   Custom response listener class object, return type of response is an Object which can futher be cast to desired type using urlID.
     */
    public CustomHttpRequest(int method, @NonNull String url, int requestID, @Nullable Map<String, String> header, @Nullable String body, @Nullable Class<T> modelClass, HttpResponseListener listener) {
        this(method, url, requestID, header, body, 60000, modelClass, listener);   /*default time is 60 secs*/

    }

    /**
     * Executes on a worker thread, parsing is done here.
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Log.d(">>>>>", responseString);
            if (mModelClass == null) {
                return Response.success((T) responseString, HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return Response.success(gson.fromJson(responseString, mModelClass), HttpHeaderParser.parseCacheHeaders(response));
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * Executes on the main thread;
     */
    @Override
    protected void deliverResponse(T response) {
        if (mInternalListener != null) {
            mInternalListener.onResponse(response);
        }
    }



    @Override
    protected void onFinish() {
        super.onFinish();
        mInternalListener = null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mRequestHeader != null ? mRequestHeader : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}
