package io.github.allanshih.pipelinedemo.pipeline.callback;

import android.util.Log;

import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.internal.PipeStatus;

import io.github.allanshih.pipelinedemo.pipeline.object.HttpBinGetIpItem;

/**
 * Created by allan.shih on 2018/3/30.
 */

public class HttpBinGetIpCallback implements IPipeCallback {
    private static final String TAG = HttpBinGetIpCallback.class.getSimpleName();
    /**
     * When pipeline is finished.
     * @param responseObject output data
     */
    public void onResult(final Object responseObject) {
        Log.d(TAG, "onResult: " + responseObject.getClass().getSimpleName());
        if (responseObject == null) {
            onError(PipeStatus.HTTP_RESPONSE_NULL, null);
            return;
        }

        if (responseObject instanceof HttpBinGetIpItem) {
            HttpBinGetIpItem ipItem = (HttpBinGetIpItem) responseObject;
            String originIp = ipItem.getOrigin();
            Log.d(TAG, "origin ip: " + (originIp != null? originIp: "null"));
        }
    }

    /**
     * This callback is invoked when there is a pipeline execution error.
     * @param status error
     * @param pipeItem errorItem
     */
    public void onError(final int status, final IPipeItem pipeItem) {
        if (pipeItem != null) {
            Log.d(TAG, "Error: " + pipeItem.getErrorMessage());
        }
    }
}