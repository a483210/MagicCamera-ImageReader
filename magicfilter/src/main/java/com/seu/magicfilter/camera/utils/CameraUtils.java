package com.seu.magicfilter.camera.utils;

import android.hardware.Camera;

import com.seu.magicfilter.camera.CameraEngine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by why8222 on 2016/2/25.
 */
public class CameraUtils {

    //适配fps区间
    public static int[] adaptFpsRange(int expectedFps, Camera.Parameters params) {
        List<int[]> ranges = params.getSupportedPreviewFpsRange();
        expectedFps *= 1000;
        int[] closestRange = ranges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        int count = ranges.size();
        for (int i = 1; i < count; i++) {
            int[] range = ranges.get(i);
            int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
            if (curMeasure < measure) {
                closestRange = range;
                measure = curMeasure;
            }
        }
        return closestRange;
    }

    public static Camera.Size adaptPreviewSize(Camera.Parameters params) {
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        double minDiffW = Double.MAX_VALUE;
        double minDiffH = Double.MAX_VALUE;
        Camera.Size optimalSize = null;
        for (int index = sizes.size() - 1; index >= 0; index--) {
            Camera.Size size = sizes.get(index);
            if (size.height >= CameraEngine.RECORD_WIDTH && size.width >= CameraEngine.RECORD_HEIGHT) {
                if (size.height - CameraEngine.RECORD_WIDTH < minDiffW || size.width - CameraEngine.RECORD_HEIGHT < minDiffH) {
                    optimalSize = size;
                    minDiffW = size.height - CameraEngine.RECORD_WIDTH;
                    minDiffH = size.width - CameraEngine.RECORD_HEIGHT;
                }
            }
        }
        if (optimalSize == null) {
            optimalSize = sizes.get(sizes.size() - 1);
        }
        return optimalSize;
    }

    public static Camera.Size adaptPictureSize(Camera.Parameters params) {
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Collections.sort(sizes, new ResolutionComparator());
        Camera.Size size = null;
        for (int i = 0; i < sizes.size(); i++) {
            size = sizes.get(i);
            if (size != null
                    && size.width >= CameraEngine.RECORD_WIDTH
                    && size.height >= CameraEngine.RECORD_HEIGHT)
                return size;
        }
        return size;
    }

    // 排序
    private static class ResolutionComparator implements Comparator<Camera.Size> {
        @Override
        public int compare(Camera.Size size1, Camera.Size size2) {
            if (size1.height != size2.height)
                return size1.height - size2.height;
            else
                return size1.width - size2.width;
        }
    }

}
