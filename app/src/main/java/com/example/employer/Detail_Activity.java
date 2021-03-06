package com.example.employer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class Detail_Activity extends AppCompatActivity {
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    lat= bundle.getString("latitude");
                    lon= bundle.getString("longitude");
                    break;
                default:
                    locationAddress = null;
            }
            //   latLongTV.setText(locationAddress);
        }
    }
    EditText et_f_name, et_l_name, et_email, et_contact, et_altcontact,etaadhar,etnameofcompany,
            et_company_email,et_city,et_location,et_gstin,et_company_number;
    Spinner sp_role,sp_type_of_buiseness;
    private StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID,lat,lon;
   ImageView profile;
   TextView aadhar_attach,gstin_attach;
   private Uri filepath1,filepath2,filepath3;
   Uri profileUrl,adharUrl,gstURL;
    ArrayAdapter<String> adapter,adapters;
    String buiseness[]={"Hotel","Resturant"};
    String profesion[] = {"Hotel Manager", "Hotel Owner", "Other"};
    String sstatus,type;
    Button btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_detail);
        sp_role=findViewById(R.id.job_role);
        adapter = new ArrayAdapter<String>(Detail_Activity.this, android.R.layout.simple_list_item_1, profesion);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp_role.setAdapter(adapter);
        sp_type_of_buiseness=findViewById(R.id.type_of_buisness);
        adapters=new ArrayAdapter<String>(Detail_Activity.this, android.R.layout.simple_list_item_1, buiseness);
        adapters.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sp_type_of_buiseness.setAdapter(adapters);
        sp_type_of_buiseness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                    {
                        type="Hotel";
                        break;
                    }
                    case 1:
                    {
                        type="Resturant";
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position)
                {
                    case 0:
                    {
                        sstatus="Hotel Manager";
                        break;
                    }
                    case 1:
                    {
                        sstatus="Hotel Owner";
                        break;
                    }
                    case 2:
                    {
                        sstatus="Other";
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_type_of_buiseness=findViewById(R.id.type_of_buisness);

        et_f_name=findViewById(R.id.et_f_name);
        et_l_name = findViewById(R.id.et_l_name);
        et_email=findViewById(R.id.et_email);
       // et_role=findViewById(R.id.et_your_role);
        et_contact= findViewById(R.id.et_contact_detail);
        et_altcontact=findViewById(R.id.et_alter_contact_detail);
        et_company_number=findViewById(R.id.et_company_contact_detail);
        etaadhar=findViewById(R.id.et_aadhar);
        etnameofcompany=findViewById(R.id.et_company_name);
       // et_typeofbusiness=findViewById(R.id.et_business_type);
        et_company_email=findViewById(R.id.company_email);
        et_city=findViewById(R.id.et_city);
        et_location=findViewById(R.id.et_location);
        et_gstin=findViewById(R.id.et_gst);

        profile=findViewById(R.id.ProfileImage);
        aadhar_attach=findViewById(R.id.adharCard);
        gstin_attach=findViewById(R.id.gstcard);
        btnRegister=findViewById(R.id.btnRegister);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        DocumentReference documentReference=fStore.collection("Employers").document(userID);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Detail_Activity.this, "User Profile", Toast.LENGTH_SHORT).show();
               Intent in = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1).getIntent(Detail_Activity.this);
                startActivityForResult(in, 1);
            }
        });

        aadhar_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1).getIntent(Detail_Activity.this);
                startActivityForResult(in, 2);
            }
        });
        gstin_attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1).getIntent(Detail_Activity.this);
                startActivityForResult(in, 3);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(et_location.getText().toString(),
                        getApplicationContext(), new GeocoderHandler());

                if(et_f_name.getText().toString().isEmpty()|| et_l_name.getText().toString().isEmpty() || et_company_email.getText().toString().isEmpty()||  et_contact.getText().toString().isEmpty()
                        || et_altcontact.getText().toString().isEmpty()|| et_email.getText().toString().isEmpty() || etaadhar.getText().toString().isEmpty()
                        ||etnameofcompany.getText().toString().isEmpty()
                        || et_city.getText().toString().isEmpty()|| et_location.getText().toString().isEmpty()|| et_gstin.getText().toString().isEmpty()|| et_company_number.getText().toString().isEmpty()
                ){
                    Toast.makeText(Detail_Activity.this, "Fill the required Details", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                    if (filepath1 != null) {
                        StorageReference reference = storageReference.child("Employer/" + UUID.randomUUID().toString());
                        reference.putFile(filepath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Toast.makeText(Detail_Activity.this, "Profile done", Toast.LENGTH_SHORT).show();
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        profileUrl = uri;
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(Detail_Activity.this, "in error " + exception, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    if (filepath2 != null) {
                        StorageReference reference = storageReference.child("Employer/" + UUID.randomUUID().toString());
                        reference.putFile(filepath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Detail_Activity.this, "Adhar Done", Toast.LENGTH_LONG).show();
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        adharUrl = uri;
                                    }
                                });
                            }
                        });
                    }
                    if (filepath3!=null)
                    {
                        StorageReference reference = storageReference.child("Employer/" + UUID.randomUUID().toString());
                        reference.putFile(filepath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(Detail_Activity.this, "GST Done", Toast.LENGTH_SHORT).show();
                               taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                   @Override
                                   public void onSuccess(Uri uri) {
                                       gstURL=uri;
                                       DocumentReference docRef = fStore.collection("Employers").document(userID);

                                       DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference().child("Employer_Images").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                       HashMap<String, String> hashMap = new HashMap<>();
                                       hashMap.put("image", profileUrl.toString());
                                       hashMap.put("AadharURL", adharUrl.toString());
                                       hashMap.put("GSTURL", gstURL.toString());

                                       imagestore.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               Toast.makeText(Detail_Activity.this, "Store in databse ", Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                   }
                               });
                            }
                        });



                    }



                    DocumentReference docRef = fStore.collection("Employers").document(userID);
                    Map<String,Object> user = new HashMap<>();
                    user.put("first",et_f_name.getText().toString());
                    user.put("last",et_l_name.getText().toString());
                    user.put("email",et_email.getText().toString());
                    user.put("role",sstatus);
                    user.put("contact",et_contact.getText().toString());
                    user.put("Alternate_contact",et_altcontact.getText().toString());
                    user.put("Aadhar_NO",etaadhar.getText().toString());
                    user.put("Name_Of_Company",etnameofcompany.getText().toString());
                    user.put("Type",type);
                    user.put("Comapany_Mail",et_company_email.getText().toString());
                    user.put("City",et_city.getText().toString());
                    user.put("Location",et_location.getText().toString());
                    user.put("GSTIN",et_gstin.getText().toString());
                    user.put("Company_contact_number",et_company_number.getText().toString());
                  //  user.put("SignIN","");
                    user.put("Valid","false");
                    user.put("Block","false");
                    user.put("Latitude",lat);
                    user.put("Longitude",lon);
                 //   user.put("image", profileUrl.toString());
                   // user.put("AadharURL", adharUrl.toString());
                    //user.put("GSTURL", gstURL.toString());
                    docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Detail_Activity.this, "Sucessfully Registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),Verification_Activity.class));
                                finish();
                            }
                        }
                    });

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap;
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filepath1 = result.getUri();
            if (filepath1 != null)
            {
                final ProgressDialog pd=new ProgressDialog(this);
                pd.setTitle("Uploading....");
                pd.show();
                StorageReference reference = storageReference.child("Employer/" + UUID.randomUUID().toString());
                reference.putFile(filepath1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        pd.dismiss();
                        Toast.makeText(Detail_Activity.this, "Profile Done", Toast.LENGTH_SHORT).show();
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                profileUrl = uri;
                            }
                        });
                    }

                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        pd.setMessage("Uploaded in profile "+(int)progress+" %");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(Detail_Activity.this, "in error " + exception, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath1);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filepath2 = result.getUri();
            if (filepath2 != null)
            {
                final ProgressDialog pd=new ProgressDialog(this);
                pd.setTitle("Uploading....");
                pd.show();
                StorageReference reference = storageReference.child("Employer/" + UUID.randomUUID().toString());
                reference.putFile(filepath2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(Detail_Activity.this, "Aadhar Done", Toast.LENGTH_SHORT).show();

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                adharUrl = uri;
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        pd.setMessage("Uploaded in Aadhar image "+(int)progress+" %");
                    }
                });
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            filepath3 = result.getUri();
            if (filepath3 != null)
            {
                final ProgressDialog pd=new ProgressDialog(this);
                pd.setTitle("Uploading....");
                pd.show();
                StorageReference reference = storageReference.child("Employer/" + UUID.randomUUID().toString());
                reference.putFile(filepath3).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Toast.makeText(Detail_Activity.this, "passbook Done", Toast.LENGTH_SHORT).show();

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                gstURL = uri;
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        pd.setMessage("Uploaded in GST image "+(int)progress+" %");
                    }
                });
            }

        }
    }

}
