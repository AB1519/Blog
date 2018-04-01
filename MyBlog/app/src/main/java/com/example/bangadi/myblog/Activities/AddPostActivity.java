package com.example.bangadi.myblog.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bangadi.myblog.Model.Blog;
import com.example.bangadi.myblog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddPostActivity extends AppCompatActivity {
    private ImageButton mPostimage;
    private EditText mPostTitle,mPostDesc;
    private Button mSubmitButton;
    private DatabaseReference mPostDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgress;
    private Uri mImageUri; //uniques resource identifier
    private static final int GALLERY_CODE=1;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser(); // gets current user
        mStorage = FirebaseStorage.getInstance().getReference(); // gets the reference to our storage

        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");  //reference database

        mProgress =new ProgressDialog(this);
        //Instantiating teh widgets
        mPostimage = (ImageButton) findViewById(R.id.imageButton);
        mPostTitle =(EditText) findViewById(R.id.postTitle);
        mPostDesc =(EditText) findViewById(R.id.postDescription);
        mSubmitButton =(Button) findViewById(R.id.submitPost);


        mPostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creating a new intent to access the photoes/gallery from the phone
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*"); //takes any type of image from gallery
                startActivityForResult(galleryIntent,GALLERY_CODE); // starting result activity method
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Posting to database
                startPosting();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==GALLERY_CODE && resultCode == RESULT_OK){
            mImageUri = data.getData();
            mPostimage.setImageURI(mImageUri); //passing the imageUri and open the gallery/photos
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog");
        mProgress.show();

        final String titleVal= mPostTitle.getText().toString().trim();
        final String descVal= mPostDesc.getText().toString().trim();


        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal)
                && mImageUri != null){
            //start uploading
            StorageReference filepath = mStorage.child("MBlog_images").child(mImageUri.getLastPathSegment());  //gets path and attach to the MBlog-image folder
            //save the file in the database
            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadurl = taskSnapshot.getDownloadUrl();  // gets the url of our image stored
                    DatabaseReference newPost = mPostDatabase.push(); // create a new item with unique reference
                    //constructing Hash Map to store the values
                    Map<String, String> dataToSave = new HashMap<>();
                    dataToSave.put("title" ,titleVal);
                    dataToSave.put("desc" ,descVal);
                    dataToSave.put("image" ,downloadurl.toString());
                    dataToSave.put("timestamp" ,String.valueOf(java.lang.System.currentTimeMillis()));
                    dataToSave.put("userid" ,mUser.getUid());


                    newPost.setValue(dataToSave); //posting
                    mProgress.dismiss();

                    startActivity(new Intent(AddPostActivity.this,PostListActivity.class)); // takes user back to postlist activity
                    finish(); //  get out of previous activity


                }
            });


        }
    }
}
