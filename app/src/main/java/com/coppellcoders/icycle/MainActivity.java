package com.coppellcoders.icycle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * @author Darshan Bhatta
 */

public class MainActivity extends Fragment implements View.OnClickListener{

    private static final int CAMERA_REQUEST_CODE = 420;

    private static final String VISION_API_KEY = "AIzaSyByPX6YJOmWdn86wp4_u1FgiRDBPvV2VB8";
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ImageButton takePicture;
    ProgressDialog dialog;
    Dialog myDia;
    ImageView imageView;
    ImageButton logout;
    long pointss;
long diffMeter;
    private Feature feature;
    private Bitmap bitmap;

    // list of all the possible APIs, just change the  api string to one of the following
    // "LANDMARK_DETECTION" - detects the landmark and prints out the name
    // "SAFE_SEARCH_DETECTION" - detects any "adult or violent and ranks the %
    // "LOGO_DETECTION" - detects a company logo, E.g. take a picture of Google logo and return google
    // "IMAGE_PROPERTIES" - returns the RGB values at specific coordinates

    private String api = "LABEL_DETECTION";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Created View");
        View root  =inflater.inflate(R.layout.activity_main, container, false);
        takePicture = root.findViewById(R.id.takePicture);
        takePicture.setOnClickListener(this);
        imageView = root.findViewById(R.id.imageView);
        logout = root.findViewById(R.id.logout);
        DatabaseReference ref = database.getReference("Users");
if(mAuth.getCurrentUser()!=null) {
    logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            Intent myIntent = new Intent(getContext(), LogInActivity.class);
            startActivity(myIntent);
            getActivity().finish();
        }
    });
    ref.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //Get map of users in datasnapshot
                    pointss = (long) dataSnapshot.child("Points").getValue();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //handle databaseError
                }
            });

    imageView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (myDia != null) {
                myDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDia.show();
            }
        }
    });
}
        feature = new Feature();
        feature.setType(api); //selects api
        feature.setMaxResults(8); //max number of outputs returned



        return root;
    }


    //opens camera and takes a picture
    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }

    //after taking a picture, it runs this method
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            callCloudVision(bitmap, feature);
        }
    }


    //calls google vision API
    @SuppressLint("StaticFieldLeak")
    private void callCloudVision(final Bitmap bitmap, final Feature feature) {
        final ArrayList<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final ArrayList<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(bitmap2JPEG(bitmap));
        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("Processing Data...");
                        dialog.show();
                    }
                });

                try {


                    HttpTransport http = AndroidHttp.newCompatibleTransport();
                    JsonFactory json = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer request = new VisionRequestInitializer(VISION_API_KEY);

                    Vision.Builder build = new Vision.Builder(http, json, null);
                    build.setVisionRequestInitializer(request);

                    Vision vision = build.build();

                    BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
                    batchRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Toast.makeText(getActivity(), "failed to reach API: " + e.getContent(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "failed to reach API: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return "Google Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {

                dialog.dismiss();
                myDia = new Dialog(getActivity());


                myDia.setContentView(R.layout.returnpopup);
                Button close = myDia.findViewById(R.id.close);

                Button goONo = myDia.findViewById(R.id.closeOopen);

                TextView item = myDia.findViewById(R.id.item_name);
                TextView recy = myDia.findViewById(R.id.recychecker);
                final TextView points = myDia.findViewById(R.id.points);

              //  text.setText(result);
                Log.i("msg", result);
                String[] rec = {"Glass", "Soft Drink", "Metal", "Paper", "Cardboard", "Plastic", "Technology", "Electronic Device", "Pattern", "Plant", "Aluminum", "Plastic Bottle", "Bottle", "Paper", "Product", "Writing", "Handwriting", "Tree", "Text", "Font", "Material", "Document", "Wood", "Textile", "Plastic", "Product", "Bottle", "Glitter", "Windshield", "Drink", "Soft Drink", "Textile", "Water", "Plastic Bottle", "Metal", "Product", "Aluminum", "Tin", "Can", "Aluminum Can", "Silver", "Tableware", "Silverware", "Vehicle", "Windshield", "Glitter", "Rim", "Material", "Glass", "Product", "Glass Bottle", "Water", "Mirror", "Material", "Drink", "Soft Drink", "Screen", "Technology", "Electronic", "Electronic", "Product", "Laptop", "Technology", "Multimedia", "Electronic Device", "NetBook", "Mobile Phone", "Gadget", "Portable communications device", "Communication device", "Food", "Product", "Drink", "Beverage", "Soft Drink", "Carbonated soft drinks", "Flavor", "Cuisine"};
                String[] splitResult = result.split("\n");
                for (int i = 0; i < splitResult.length; i++) {
                    String parsed = splitResult[i].substring(splitResult[i].indexOf("- ") + 2);
                    splitResult[i] = parsed;
                }

//                boolean isRecycable = false;
//
//                for(int i = 0; i < splitResult.length; i++){
//                    for(int k = 0; k < splitResult.length; k++) {
//                        if(rec[i].equals(splitResult[k])){
//                            isRecycable = true;
////                            break;
//                        }
//
//                    }
//                }
                boolean isRecyclable = false;
//                for (int i = 0; i < rec.length; i++) {
//                    if (Arrays.asList(rec).contains(splitResult[i])) {
//                        isRecyclable = true;
//                        break;
//                    }
//                }
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < rec.length; j++) {
                        if (splitResult[i].equals(rec[j])) {
                            isRecyclable = true;
                            item.setText("This is a " + rec[j]);
                            recy.setText("This is recyclable");
                            item.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            recy.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            break;
                        }
                    }
                }

             if (isRecyclable) {
                 goONo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ok_icon));
              }else{

                 goONo.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.close_icon));
                 item.setText("This is a " + splitResult[1]);
                 recy.setText("This is not recyclable");
                 item.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                 recy.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
             }


                System.out.println(Arrays.toString(splitResult));
                System.out.println(isRecyclable);

                //Category Classifier
                //Paper, Plastic, Metal, Glass, Electronics, Food

                String[] Paper = {"Paper", "Product", "Writing", "Handwriting", "Tree", "Text", "Font", "Material", "Document", "Wood", "Textile"};
                String[] Plastic = {"Plastic", "Product", "Bottle", "Glitter", "Windshield", "Drink", "Soft Drink", "Textile", "Water", "Plastic Bottle"};
                String[] Metal = {"Metal", "Product", "Aluminum", "Tin", "Can", "Aluminum Can", "Silver", "Tableware", "Silverware", "Vehicle", "Windshield", "Glitter", "Rim", "Material"};
                String[] Glass = {"Glass", "Product", "Glass Bottle", "Water", "Mirror", "Material", "Drink", "Soft Drink", "Screen", "Technology"};
                String[] Electronics = {"Electronic", "Electronic", "Product", "Laptop", "Technology", "Multimedia", "Electronic Device", "NetBook", "Mobile Phone", "Gadget", "Portable communications device", "Communication device"};
                String[] Food = {"Food", "Product", "Drink", "Beverage", "Soft Drink", "Carbonated soft drinks", "Flavor", "Cuisine"};

                int paperCount = 0;
                int plasticCount = 0;
                int metalCount = 0;
                int glassCount = 0;
                int electronicsCount = 0;
                int foodCount = 0;
                int otherCount = 0;

                //Paper
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < Paper.length; j++) {
                        if (splitResult[i].equals(Paper[j])) {
                            isRecyclable = true;
                            paperCount++;
                        }
                    }
                }

                //Plastic
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < Plastic.length; j++) {
                        if (splitResult[i].equals(Plastic[j])) {
                            isRecyclable = true;
                            plasticCount++;
                        }
                    }
                }
                //Metal
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < Metal.length; j++) {
                        if (splitResult[i].equals(Metal[j])) {
                            isRecyclable = true;
                            metalCount++;
                        }
                    }
                }
                //Glass
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < Glass.length; j++) {
                        if (splitResult[i].equals(Glass[j])) {
                            isRecyclable = true;
                            glassCount++;
                        }
                    }
                }
                //Electronics
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < Electronics.length; j++) {
                        if (splitResult[i].equals(Electronics[j])) {
                            isRecyclable = true;
                            electronicsCount++;
                        }
                    }
                }
                //Food
                for (int i = 0; i < splitResult.length; i++) {
                    for (int j = 0; j < Food.length; j++) {
                        if (splitResult[i].equals(Food[j])) {
                            isRecyclable = true;
                            foodCount++;
                        }
                    }
                }
                //Other
                if (paperCount == 0 && plasticCount == 0 && metalCount == 0 && glassCount == 0 && electronicsCount == 0 && foodCount == 0) {
                    otherCount = splitResult.length;
                }
                ArrayList<SortedCounts> sortedCount = new ArrayList<SortedCounts>();
                sortedCount.add(new SortedCounts("paperCount", paperCount));
                sortedCount.add(new SortedCounts("plasticCount", plasticCount));
                sortedCount.add(new SortedCounts("metalCount", metalCount));
                sortedCount.add(new SortedCounts("glassCount", glassCount));
                sortedCount.add(new SortedCounts("electronicsCount", electronicsCount));
                sortedCount.add(new SortedCounts("foodCount", foodCount));
                sortedCount.add(new SortedCounts("otherCount", otherCount));

                SortedCounts[] convertedSortedCount = new SortedCounts[sortedCount.size()];
                for (int i = 0; i < sortedCount.size(); i++) {
                    convertedSortedCount[i] = sortedCount.get(i);
                }

                int n = convertedSortedCount.length;
                SortedCounts temp = null;
                for (int i = 0; i < n; i++) {
                    for (int j = 1; j < (n - i); j++) {
                        if (convertedSortedCount[j - 1].getCount() > convertedSortedCount[j].getCount()) {
                            //swap elements
                            temp = convertedSortedCount[j - 1];
                            convertedSortedCount[j - 1] = convertedSortedCount[j];
                            convertedSortedCount[j] = temp;
                        }
                    }
                }
                System.out.println(Arrays.toString(convertedSortedCount));
//                Arrays.sort(sortedCount);

//                System.out.println("Paper: " + paperCount + " Plastic: " + plasticCount + " Metal: " + metalCount + " Glass: " + glassCount + " Electronics: " + electronicsCount + " Food: " + foodCount);
                final String[] highestCountName = {convertedSortedCount[convertedSortedCount.length - 1].getName()};
                System.out.println(highestCountName[0]);
                int highestCount = convertedSortedCount[convertedSortedCount.length - 1].getCount();
                final boolean finalIsRecyclable = isRecyclable;
                goONo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(finalIsRecyclable){


                            DatabaseReference mRef =  database.getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                            mRef.child("Points").setValue((pointss+diffMeter));
                            highestCountName[0] = highestCountName[0].replace("Count", "");
                            Intent myIntent = new Intent(getContext(), HomeActivity.class);
                            myIntent.putExtra("name", highestCountName[0]);
                            startActivity(myIntent);




                        }else{
                            Toast.makeText(getActivity(), "This is not recyclable",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                //Difficulty Meter
                //Paper - 1 (Very Easy)
                //Plastic - 3 (Medium)
                //Metal - 4 (Hard)
                //Glass - 2 (Easy)
                //Electronics - 5 (Very Hard)
                //Food - 3 (Medium)

                int[] difficulty = {1, 2, 3, 4, 5};
                diffMeter = 0;
                if (highestCountName[0].equals("paperCount")) {
                    diffMeter = difficulty[0];
                } else if (highestCountName[0].equals("plasticCount")) {
                    diffMeter = difficulty[2];
                } else if (highestCountName[0].equals("metalCount")) {
                    diffMeter = difficulty[3];
                } else if (highestCountName[0].equals("glassCount")) {
                    diffMeter = difficulty[1];
                } else if (highestCountName[0].equals("electronicsCount")) {
                    diffMeter = difficulty[4];
                } else if (highestCountName[0].equals("foodCount")) {
                    diffMeter = difficulty[2];
                } else if (highestCountName[0].equals("otherCount")){
                    diffMeter = 0;
                }
                System.out.println(diffMeter);
points.setText(diffMeter+" points");
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDia.dismiss();

                    }
                });



                myDia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDia.show();
            }


        }.execute();
    }

    private class SortedCounts {
        String name;
        int count;

        public SortedCounts(String name, int count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return name + ": " + count;
        }
    }

    // Convert the bitmap to a JPEG
    private Image bitmap2JPEG(Bitmap bit) {

        Image baseImage = new Image();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] imageBytes = stream.toByteArray();
        baseImage.encodeContent(imageBytes);

        return baseImage;
    }

    //gets the response from Google Vision and converts it into a readable format
    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entity;

        String message = "";
        switch (api) {
            case "LANDMARK_DETECTION":
                entity = imageResponses.getLandmarkAnnotations();
                message = formatText(entity);
                break;

            case "LOGO_DETECTION":
                entity = imageResponses.getLogoAnnotations();
                message = formatText(entity);
                break;

            case "SAFE_SEARCH_DETECTION":
                SafeSearchAnnotation annotation = imageResponses.getSafeSearchAnnotation();
                message = formatImageText(annotation);
                break;

            case "IMAGE_PROPERTIES":
                ImageProperties imageProperties = imageResponses.getImagePropertiesAnnotation();
                message = formatImageProp(imageProperties);
                break;

            case "LABEL_DETECTION":
                entity = imageResponses.getLabelAnnotations();
                message = formatText(entity);
                break;

        }

        return message;
    }

    // prints out safe search in a formatted way
    private String formatImageText(SafeSearchAnnotation annotation) {
        return String.format("adult: %s\nmedical: %s\nspoofed: %s\nviolence: %s\n",
                annotation.getAdult(),
                annotation.getMedical(),
                annotation.getSpoof(),
                annotation.getViolence());
    }


    //prints out the image properties in a formatted way
    private String formatImageProp(ImageProperties imageProperties) {
        String message = "";

        DominantColorsAnnotation colors = imageProperties.getDominantColors();

        for (ColorInfo color : colors.getColors()) {
            message = message + "" + color.getPixelFraction() + " - " + color.getColor().getRed() + " - " + color.getColor().getGreen() + " - " + color.getColor().getBlue();
            message = message + "\n";
        }

        return message;
    }

    //prints the message in a formatted way
    private String formatText(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            int count = 0;
            for (EntityAnnotation entity : entityAnnotation) {
                count++;
                if (count <= 8) {
                    int num = (int) (entity.getScore() * 100);
                    message = message + "    " + num + "% - " + entity.getDescription().substring(0, 1).toUpperCase() + entity.getDescription().substring(1);
                    message += "\n";
                }
            }
        } else {
            message = "Nothing Found";
        }

        return message;
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.imageView:
                break;
            case R.id.takePicture:
                takePictureFromCamera();
                break;
        }
    }
}