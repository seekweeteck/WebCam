package my.edu.tarc.webcam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final String UPLOAD_URL = "http://bait2073.esy.es/insert_student.php";
    public static final String UPLOAD_ID = "id";
    public static final String UPLOAD_NAME = "name";
    public static final String UPLOAD_PHOTO = "photo";
    public static final String TAG = "MY MESSAGE";

    private int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageViewPreview;
    private String mCurrentPhotoPath;

    private String id;
    private String name;
    private EditText editTextID, editTextName;
    private Bitmap imageBitmap;

    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextID = (EditText)findViewById(R.id.editTextID);
        editTextName = (EditText)findViewById(R.id.editTextName);
        imageViewPreview = (ImageView)findViewById(R.id.imageViewPreview);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void saveRecord(View v){
        class UploadImage extends AsyncTask<Student,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Uploading record", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                Log.d("onPostExecute", s);
            }

            @Override
            protected String doInBackground(Student... params) {
                String id = params[0].getId();
                String name = params[0].getName();
                String uploadImage = getStringImage(imageBitmap);

                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_ID, id);
                data.put(UPLOAD_NAME, name);
                data.put(UPLOAD_PHOTO, uploadImage);

                String result = rh.sendPostRequest(UPLOAD_URL, data);
                Log.d("doInBackground", result);
                return result;
            }
        }

        UploadImage ui = new UploadImage();
        String id, name;

        id = editTextID.getText().toString();
        name = editTextName.getText().toString();
        Student student = new Student(id, name, filePath);
        ui.execute(student);
    }

    public void takePhoto(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        //Returns the first activity component that can handle the intent
        /*if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d(getPackageName(), "Error creating photo file.");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        getPackageName().toString(),
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //imageBitmap = (Bitmap) extras.get("data");
            //imageViewPreview.setImageBitmap(imageBitmap);

            try {
                //imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imageViewPreview.setImageBitmap(imageBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //setPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageViewPreview.getWidth();
        int targetH = imageViewPreview.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageViewPreview.setImageBitmap(bitmap);
    }
}
