package info.cryptoproof.scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

/**
 * Created by derrend on 08/11/15.
 */
public class DecoderActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

//    private TextView myTextView;
    private QRCodeReaderView mydecoderview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decoder);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        Intent intent = new Intent();
        intent.putExtra("scanned", text);
        setResult(RESULT_OK,intent);
        finish();
    }


    // Called when your device have no camera
    @Override
    public void cameraNotFound() {

    }

    // Called when there's no QR codes in the camera preview image
    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }
}
