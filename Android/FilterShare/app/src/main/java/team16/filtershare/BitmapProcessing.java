package team16.filtershare;

import java.io.IOException;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.media.ExifInterface;
import android.util.Log;

public class BitmapProcessing {

    public static Bitmap applyEffects(Bitmap origBitmap, int brightness, int contrast, int saturation, int sharpen, int temperature, int tint, int vignette, int grain) {
        Bitmap bitmap = origBitmap;
        bitmap = BitmapProcessing.brightness(bitmap, brightness);
        bitmap = BitmapProcessing.contrast(bitmap, contrast);
        bitmap = BitmapProcessing.saturation(bitmap, saturation);
        bitmap = BitmapProcessing.sharpen(bitmap,sharpen);
        bitmap = BitmapProcessing.temperature(bitmap, temperature);
        bitmap = BitmapProcessing.tint(bitmap, tint);
        bitmap = BitmapProcessing.vignette(bitmap, vignette);
        bitmap = BitmapProcessing.grain(bitmap, grain);
        return bitmap;
    }

    /**
     * Returns a value that is one of val (if it's between min or max) or min or max (if it's outside that range).
     * @param val
     * @param min
     * @param max
     * @return constrained value
     */
    private static double constrain(double val, double min, double max) {
        if (val < min) {
            return min;
        }
        else if (val > max) {
            return max;
        }
        return val;
    }

	// [-360, +360] -> Default = 0
	public static Bitmap rotate(Bitmap bitmap, float degrees) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
		Matrix matrix = new Matrix();
		matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap emboss(Bitmap src) {
		double[][] EmbossConfig = new double[][] {
				{ -1 ,  0, -1 },
				{  0 ,  4,  0 },
				{ -1 ,  0, -1 }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(EmbossConfig);
		convMatrix.Factor = 1;
		convMatrix.Offset = 127;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}

	// [0, 150], default => 100
	public static Bitmap cfilter(Bitmap src, double red, double green, double blue) {
		red = (double) red / 100;
		green = (double) green / 100;
		blue = (double) blue / 100;

		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				// apply filtering on each channel R, G, B
				A = Color.alpha(pixel);
				R = (int)(Color.red(pixel) * red);
				G = (int)(Color.green(pixel) * green);
				B = (int)(Color.blue(pixel) * blue);
				// set new color pixel to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		// return final image
		return bmOut;
	}

	public static Bitmap gaussian(Bitmap src) {
		double[][] GaussianBlurConfig = new double[][] {
				{ 1, 2, 1 },
				{ 2, 4, 2 },
				{ 1, 2, 1 }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(GaussianBlurConfig);
		convMatrix.Factor = 16;
		convMatrix.Offset = 0;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}

	// bitOffset = (16, 32, 64, 128)
	public static Bitmap cdepth(Bitmap src, int bitOffset) {
		// get image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// round-off color offset
				R = ((R + (bitOffset / 2)) - ((R + (bitOffset / 2)) % bitOffset) - 1);
				if(R < 0) { R = 0; }
				G = ((G + (bitOffset / 2)) - ((G + (bitOffset / 2)) % bitOffset) - 1);
				if(G < 0) { G = 0; }
				B = ((B + (bitOffset / 2)) - ((B + (bitOffset / 2)) % bitOffset) - 1);
				if(B < 0) { B = 0; }

				// set pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		return bmOut;
	}

	public static Bitmap sharpen(Bitmap src, int value) {
        double weight = value / 10.0d;
		double[][] SharpConfig = new double[][] {
				{ 0 , -2    , 0  },
				{ -2, weight, -2 },
				{ 0 , -2    , 0  }
		};
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(SharpConfig);
		convMatrix.Factor = weight - 8;
		return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
	}

	public static Bitmap grain(Bitmap source, int scale) {
		final int COLOR_MAX = 0xFF;

		// get image size
		int width = source.getWidth();
		int height = source.getHeight();
		int[] pixels = new int[width * height];
		// get pixel array from source
		source.getPixels(pixels, 0, width, 0, 0, width, height);
		// a random object
		Random random = new Random();

		int index = 0;
		// iteration through pixels
		for(int y = 0; y < height; ++y) {
			for(int x = 0; x < width; ++x) {
				// get current index in 2D-matrix
				index = y * width + x;

                int color = pixels[index];
                int red = (int)(constrain(Color.red(color) + scale * (2*Math.random() - 1), 0, 255));
                int green = (int)(constrain(Color.green(color) + scale * (2*Math.random() - 1), 0, 255));
                int blue = (int)(constrain(Color.blue(color) + scale * (2*Math.random() - 1), 0, 255));
                // Put new color
                int randColor = Color.rgb(red, green, blue);

				pixels[index] |= randColor;
			}
		}

		// output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
		bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

		source.recycle();
		source = null;

		return bmOut;
	}

	// [-255, +255] -> Default = 0
	public static Bitmap brightness(Bitmap src, int value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if(R > 255) { R = 255; }
				else if(R < 0) { R = 0; }

				G += value;
				if(G > 255) { G = 255; }
				else if(G < 0) { G = 0; }

				B += value;
				if(B > 255) { B = 255; }
				else if(B < 0) { B = 0; }

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// src.recycle();
		src = null;

		return bmOut;
	}

	public static Bitmap sepia(Bitmap src) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// constant grayscale
		final double GS_RED = 0.3;
		final double GS_GREEN = 0.59;
		final double GS_BLUE = 0.11;
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				// get color on each channel
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				// apply grayscale sample
				B = G = R = (int)(GS_RED * R + GS_GREEN * G + GS_BLUE * B);

				// apply intensity level for sepid-toning on each channel
				R += 110;
				if(R > 255) { R = 255; }

				G += 65;
				if(G > 255) { G = 255; }

				B += 20;
				if(B > 255) { B = 255; }

				// set new pixel color to output image
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		return bmOut;
	}

	// red, green, blue [0, 48]
	public static Bitmap gamma(Bitmap src, double red, double green, double blue) {
		red = (double) (red + 2) / 10.0;
		green = (double) (green + 2) / 10.0;
		blue = (double) (blue + 2) / 10.0;
		// create output image
		Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		// get image size
		int width = src.getWidth();
		int height = src.getHeight();
		// color information
		int A, R, G, B;
		int pixel;
		// constant value curve
		final int    MAX_SIZE = 256;
		final double MAX_VALUE_DBL = 255.0;
		final int    MAX_VALUE_INT = 255;
		final double REVERSE = 1.0;

		// gamma arrays
		int[] gammaR = new int[MAX_SIZE];
		int[] gammaG = new int[MAX_SIZE];
		int[] gammaB = new int[MAX_SIZE];

		// setting values for every gamma channels
		for(int i = 0; i < MAX_SIZE; ++i) {
			gammaR[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
			gammaG[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
			gammaB[i] = (int)Math.min(MAX_VALUE_INT,
					(int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
		}

		// apply gamma table
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// look up gamma
				R = gammaR[Color.red(pixel)];
				G = gammaG[Color.green(pixel)];
				B = gammaB[Color.blue(pixel)];
				// set new color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		// return final image
		return bmOut;
	} 

	// [-100, +100] -> Default = 0
	public static Bitmap contrast(Bitmap src, double value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap

		// create a mutable empty bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

		// create a canvas so that we can draw the bmOut Bitmap from source bitmap
		Canvas c = new Canvas();
		c.setBitmap(bmOut);

		// draw bitmap to bmOut from src bitmap so we can modify it
		c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


		// color information
		int A, R, G, B;
		int pixel;
		// get contrast value
		double contrast = Math.pow((100 + value) / 100, 2);

		// scan through all pixels
		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				// apply filter contrast for every channel R, G, B
				R = Color.red(pixel);
				R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(R < 0) { R = 0; }
				else if(R > 255) { R = 255; }

				G = Color.green(pixel);
				G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(G < 0) { G = 0; }
				else if(G > 255) { G = 255; }

				B = Color.blue(pixel);
				B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
				if(B < 0) { B = 0; }
				else if(B > 255) { B = 255; }

				// set new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}
		// src.recycle();
		src = null;
		return bmOut;
	}

	// [0, 200] -> Default = 100
	public static Bitmap saturation(Bitmap src, int value) {
		float f_value = (float) (value / 100.0);

		int w = src.getWidth();
		int h = src.getHeight();

		Bitmap bitmapResult = 
				Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvasResult = new Canvas(bitmapResult);
		Paint paint = new Paint();
		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.setSaturation(f_value);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
		paint.setColorFilter(filter);
		canvasResult.drawBitmap(src, 0, 0, paint);

		// src.recycle();
		src = null;

		return bitmapResult;
	}

	public static Bitmap grayscale(Bitmap src) {
		//Array to generate Gray-Scale image
		float[] GrayArray = {
				0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
				0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
				0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
				0.0f, 0.0f, 0.0f, 1.0f, 0.0f,  
		};

		ColorMatrix colorMatrixGray = new ColorMatrix(GrayArray);

		int w = src.getWidth();
		int h = src.getHeight();

		Bitmap bitmapResult = Bitmap
				.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvasResult = new Canvas(bitmapResult);
		Paint paint = new Paint();

		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrixGray);
		paint.setColorFilter(filter);
		canvasResult.drawBitmap(src, 0, 0, paint);

		src.recycle();
		src = null;

		return bitmapResult;
	}

    public static Bitmap temperature(Bitmap src, int value) {
// image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R = R + (value - 50);
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G = G - ((value - 50) / 2);
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B = B - (value - 50);
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // src.recycle();
        src = null;

        return bmOut;
    }

	public static Bitmap vignette(Bitmap image, int value) {
		final int width = image.getWidth();
		final int height = image.getHeight();
		
	    float radius = width / ((value / 100.0f) + 0.01f);
	    int[] colors = new int[] { 0, 0x55000000, 0xff000000 };
        float[] positions = new float[] { 0.0f, 0.5f, 1.0f };
	    
	    //RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, colors, positions, Shader.TileMode.CLAMP);
	    
	    RadialGradient gradient = new RadialGradient(width / 2, height / 2, radius, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

	    Canvas canvas = new Canvas(image);
	    canvas.drawARGB(1, 0, 0, 0);

	    final Paint paint = new Paint();
	    paint.setAntiAlias(true);
	    paint.setColor(Color.BLACK);
	    paint.setShader(gradient);

	    final Rect rect = new Rect(0, 0, image.getWidth(), image.getHeight());
	    final RectF rectf = new RectF(rect);

	    canvas.drawRect(rectf, paint);
	    
	    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    	canvas.drawBitmap(image, rect, rect, paint);
	    
	    return image;
	}

	// hue = [0, 360] -> Default = 0
	public static Bitmap hue(Bitmap bitmap, float hue) {
		Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
		final int width = newBitmap.getWidth();
		final int height = newBitmap.getHeight();
		float [] hsv = new float[3];

		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int pixel = newBitmap.getPixel(x,y);
				Color.colorToHSV(pixel,hsv);
				hsv[0] = hue;
				newBitmap.setPixel(x,y,Color.HSVToColor(Color.alpha(pixel),hsv));
			}
		}

		bitmap.recycle();
		bitmap = null;

		return newBitmap;
	}

	public static Bitmap tint(Bitmap src, int degree) {
        double PI = 3.14159d;
        double FULL_CIRCLE_DEGREE = 360d;
        double HALF_CIRCLE_DEGREE = 180d;
        double RANGE = 256d;

        // image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap

        int[] pix = new int[width * height];
        src.getPixels(pix, 0, width, 0, 0, width, height);

        int RY, GY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;

        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));

        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                GY = (-30 * r + 41 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }

        Bitmap outBitmap = Bitmap.createBitmap(width, height, src.getConfig());
        outBitmap.setPixels(pix, 0, width, 0, 0, width, height);

        pix = null;

        return outBitmap;
	}

	public static Bitmap invert(Bitmap src) {
		Bitmap output = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
		int A, R, G, B;
		int pixelColor;
		int height = src.getHeight();
		int width = src.getWidth();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixelColor = src.getPixel(x, y);
				A = Color.alpha(pixelColor);

				R = 255 - Color.red(pixelColor);
				G = 255 - Color.green(pixelColor);
				B = 255 - Color.blue(pixelColor);

				output.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		return output;
	}

	// percent = [0, 150], type = (1, 2, 3) => (R, G, B)
	public static Bitmap boost(Bitmap src, int type, float percent) {
		percent = (float) percent / 100;

		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

		int A, R, G, B;
		int pixel;

		for(int x = 0; x < width; ++x) {
			for(int y = 0; y < height; ++y) {
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);
				if(type == 1) {
					R = (int)(R * (1 + percent));
					if(R > 255) R = 255;
				}
				else if(type == 2) {
					G = (int)(G * (1 + percent));
					if(G > 255) G = 255;
				}
				else if(type == 3) {
					B = (int)(B * (1 + percent));
					if(B > 255) B = 255;
				}
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		return bmOut;
	}

	public static final Bitmap sketch(Bitmap src) {
		int type = 6;
		int threshold = 130;
		
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, src.getConfig());

		int A, R, G, B;
		int sumR, sumG, sumB;
		int[][] pixels = new int[3][3];
		for(int y = 0; y < height - 2; ++y) {
			for(int x = 0; x < width - 2; ++x) {
				//      get pixel matrix
				for(int i = 0; i < 3; ++i) {
					for(int j = 0; j < 3; ++j) {
						pixels[i][j] = src.getPixel(x + i, y + j);
					}
				}
				// get alpha of center pixel
				A = Color.alpha(pixels[1][1]);
				// init color sum
				sumR = sumG = sumB = 0;
				sumR = (type*Color.red(pixels[1][1])) - Color.red(pixels[0][0]) - Color.red(pixels[0][2]) - Color.red(pixels[2][0]) - Color.red(pixels[2][2]);
				sumG = (type*Color.green(pixels[1][1])) - Color.green(pixels[0][0]) - Color.green(pixels[0][2]) - Color.green(pixels[2][0]) - Color.green(pixels[2][2]);
				sumB = (type*Color.blue(pixels[1][1])) - Color.blue(pixels[0][0]) - Color.blue(pixels[0][2]) - Color.blue(pixels[2][0]) - Color.blue(pixels[2][2]);
				// get final Red
				R = (int)(sumR  + threshold);
				if(R < 0) { R = 0; }
				else if(R > 255) { R = 255; }
				// get final Green
				G = (int)(sumG  + threshold);
				if(G < 0) { G = 0; }
				else if(G > 255) { G = 255; }
				// get final Blue
				B = (int)(sumB  + threshold);
				if(B < 0) { B = 0; }
				else if(B > 255) { B = 255; }

				result.setPixel(x + 1, y + 1, Color.argb(A, R, G, B));
			}
		}

		src.recycle();
		src = null;

		return result;
	}

	public static Bitmap modifyOrientation(Bitmap bitmap, String image_url) throws IOException {
		ExifInterface ei = new ExifInterface(image_url);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			return rotate(bitmap, 90);

		case ExifInterface.ORIENTATION_ROTATE_180:
			return rotate(bitmap, 180);

		case ExifInterface.ORIENTATION_ROTATE_270:
			return rotate(bitmap, 270);

		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			return flip(bitmap, true, false);

		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			return flip(bitmap, false, true);

		default:
			return bitmap;
		}
	}

}
