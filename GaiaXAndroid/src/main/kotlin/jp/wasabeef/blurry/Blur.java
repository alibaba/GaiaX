package jp.wasabeef.blurry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.alibaba.gaiax.render.view.basic.GXView;

/**
 * Copyright (C) 2020 Wasabeef
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Blur {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap of(View capture, BlurFactor factor) {
        capture.setDrawingCacheEnabled(true);
        capture.destroyDrawingCache();
        Bitmap captureCache = capture.getDrawingCache();
        Bitmap bitmap = of(capture.getContext(), captureCache.copy(Bitmap.Config.ARGB_8888, true), factor);
        captureCache.recycle();
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap of(Context context, Bitmap source, BlurFactor factor) {
        int width = factor.width / factor.sampling;
        int height = factor.height / factor.sampling;

        if (Helper.hasZero(width, height)) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) factor.sampling, 1 / (float) factor.sampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        PorterDuffColorFilter filter = new PorterDuffColorFilter(factor.color, PorterDuff.Mode.SRC_ATOP);
        paint.setColorFilter(filter);
        if (factor.captureTargetRect != null) {
            Rect rect = factor.captureTargetRect;
            Rect srcRect = new Rect(rect.left, rect.top, rect.right, rect.bottom);
            Rect dstRect = new Rect(0, 0, factor.width, factor.height);
            canvas.drawBitmap(source, srcRect, dstRect, paint);
        } else {
            canvas.drawBitmap(source, 0, 0, paint);
        }

        try {
            bitmap = Blur.rs(context, bitmap, factor.radius);
        } catch (RSRuntimeException e) {
        }

        if (factor.sampling == BlurFactor.DEFAULT_SAMPLING) {
            return bitmap;
        } else {
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, factor.width, factor.height, true);
            bitmap.recycle();
            return scaled;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Bitmap rs(Context context, Bitmap bitmap, int radius) throws RSRuntimeException {
        RenderScript rs = null;
        Allocation input = null;
        Allocation output = null;
        ScriptIntrinsicBlur blur = null;
        try {
            rs = RenderScript.create(context);
            rs.setMessageHandler(new RenderScript.RSMessageHandler());
            input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            output = Allocation.createTyped(rs, input.getType());
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            blur.setInput(input);
            blur.setRadius(radius);
            blur.forEach(output);
            output.copyTo(bitmap);
        } finally {
            if (rs != null) {
                rs.destroy();
            }
            if (input != null) {
                input.destroy();
            }
            if (output != null) {
                output.destroy();
            }
            if (blur != null) {
                blur.destroy();
            }
        }

        return bitmap;
    }
}
