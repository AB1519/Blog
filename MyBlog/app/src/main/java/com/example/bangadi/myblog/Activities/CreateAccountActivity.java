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

import com.example.bangadi.myblog.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText firstName,lastName,email,password;
    private Button createAccountBtn;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private StorageReference mFiresbaseStorage;
    private ProgressDialog mProgressDialog;
    private ImageButton profilePic;
    private final static int GALLERY_CODE=1;
    private Uri resultUri=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers") ; // create a new database

        mAuth = FirebaseAuth.getInstance();


        mProgressDialog = new ProgressDialog(this);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.emailAcc);
        password = (EditText) findViewById(R.id.passwordAcc);
        profilePic = (ImageButton) findViewById(R.id.profilePic);
        mFiresbaseStorage = FirebaseStorage.getInstance().getReference().child("MBlog_profilepics"); //creating the folder in the database

        createAccountBtn = (Button) findViewById(R.id.createAccBtn);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount(); // calling the method
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallerIntent = new Intent();
                gallerIntent.setAction(Intent.ACTION_GET_CONTENT); //get content from the gallery/photos
                gallerIntent.setType("image/*"); // acces all type of images
                startActivityForResult(gallerIntent,GALLERY_CODE); //starts results activity
            }
        });
        }



    private void createNewAccount() {
        final String fname = firstName.getText().toString().trim();
        final String lname = lastName.getText().toString().trim();
        String newemail= email.getText().toString().trim();
        String newpwd =password.getText().toString().trim();
        if(!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname)
                && !TextUtils.isEmpty(newemail) && !TextUtils.isEmpty(newpwd)){
            mProgressDialog.setMessage("Creating Account");
            mProgressDialog.show();


           // creating new user
            mAuth.createUserWithEmailAndPassword(newemail,newpwd)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            if(authResult != null){

                                StorageReference imagepath = mFiresbaseStorage.child("MBlog_profilepics").child(resultUri.getLastPathSegment());
                                imagepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        String userid = mAuth.getCurrentUser().getUid(); // geting new user id
                                    DatabaseReference currentUserDb = mDatabaseReference.child(userid);
                                    currentUserDb.child("firstname").setValue(fname);
                                    currentUserDb.child("lastname").setValue(lname);
                                    currentUserDb.child("image").setValue(resultUri.toString());

                                    mProgressDialog.dismiss();


                                    Intent intent=new Intent(CreateAccountActivity.this,PostListActivity.class);//sending users to postlist
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// allows this activity to be at top
                                    startActivity(intent);
                                    }
                                });
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            Uri mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);  // setting cropping option to the profile pic
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                 resultUri = result.getUri();
                profilePic.setImageURI(resultUri);  // setting the cropped image to profilepic
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
