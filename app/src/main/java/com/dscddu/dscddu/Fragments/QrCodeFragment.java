package com.dscddu.dscddu.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dscddu.dscddu.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * A simple {@link Fragment} subclass.
 */
public class QrCodeFragment extends Fragment {
    public final static int QRcodeWidth = 300 ;

    View rootView;
    private ImageView imageView;
    private Bitmap bitmap ;
    private TextView textView;
    private ProgressBar progressBar;

    public QrCodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_qr_code, container, false);
        imageView = rootView.findViewById(R.id.qrCodeImage);
        progressBar = rootView.findViewById(R.id.qrCodeProgress);
        textView = rootView.findViewById(R.id.textMesgQR);
        progressBar.setVisibility(View.VISIBLE);
        qrCodeGenerationTask task = new qrCodeGenerationTask();
        task.execute("hello");
        return rootView;
    }



    public class  qrCodeGenerationTask extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... Value) {
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = new MultiFormatWriter().encode(
                        Value[0],
                        BarcodeFormat.DATA_MATRIX.QR_CODE,
                        QRcodeWidth, QRcodeWidth, null
                );

            } catch (IllegalArgumentException Illegalargumentexception) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),"Something Went " +
                        "Wrong", Snackbar.LENGTH_INDEFINITE).show();
                return null;
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if(bitMatrix != null){
                int bitMatrixWidth = bitMatrix.getWidth();

                int bitMatrixHeight = bitMatrix.getHeight();

                int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

                for (int y = 0; y < bitMatrixHeight; y++) {
                    int offset = y * bitMatrixWidth;

                    for (int x = 0; x < bitMatrixWidth; x++) {

                        pixels[offset + x] = bitMatrix.get(x, y) ?
                                getResources().getColor(R.color.backgroundBlack):
                                getResources().getColor(R.color.backgroundWhite);
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

                bitmap.setPixels(pixels, 0, 300, 0, 0, bitMatrixWidth, bitMatrixHeight);
                return bitmap;
            }
            else{
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
            else {
                textView.setText("Something Went Wrong. Try again after some times");
            }
            progressBar.setVisibility(View.GONE);
        }
    }

}
