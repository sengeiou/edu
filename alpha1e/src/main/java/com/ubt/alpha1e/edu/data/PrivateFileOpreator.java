package com.ubt.alpha1e.edu.data;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

public class PrivateFileOpreator {

	public final static String NO_VALUE = "NO_VALUE";
	// --------------------------------------------------
	private static ExecutorService pool = Executors.newSingleThreadExecutor();
	// --------------------------------------------------
	private Context mContext;

	private static PrivateFileOpreator mOpreater;

	private PrivateFileOpreator() {
	}

	public static PrivateFileOpreator getInstance(Context _context) {
		if (mOpreater == null) {
			mOpreater = new PrivateFileOpreator();
		}
		mOpreater.mContext = _context;
		return mOpreater;
	}

	public long doWriteBitMap(final String fileName, final Bitmap bitmap,
			IPrivateFileListener listener) {
		final long request_code = new Date().getTime();
		pool.submit(new Runnable() {

			@Override
			public void run() {
				try {
					FileOutputStream fout = mContext.openFileOutput(fileName,
							mContext.MODE_PRIVATE);
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.JPEG, 100, output);
					bitmap.recycle();
					byte[] bytes = output.toByteArray();
					fout.write(bytes);
					fout.close();
					output.close();
				} catch (IOException e) {

				}

			}
		});

		return request_code;
	}

	public long doReadBitMap(final String fileName,
			IPrivateFileListener listener) {
		final long request_code = new Date().getTime();

		FileInputStream fin;
		try {
			fin = mContext.openFileInput(fileName);
			Bitmap bitmap = BitmapFactory.decodeStream(fin);
			listener.onPrivateBitMapOpreaterFinish(true, request_code, bitmap);
		} catch (FileNotFoundException e) {
			listener.onPrivateBitMapOpreaterFinish(false, request_code, null);
		}

		return request_code;
	}
}
