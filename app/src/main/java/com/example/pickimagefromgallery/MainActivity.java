package com.example.pickimagefromgallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button btnPickImageFromGallery, btnMultipleImagesFromGallery;
    ImageView imageView;
    private PermissionManager permissionManager;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION = 100;
    private final int PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION = 200;
    private final int PICK_SINGLE_IMAGE_FROM_GALLERY_REQUEST_CODE = 300;
    private final int PICK_MULTIPLE_IMAGES_FROM_GALLERY_REQUEST_CODE = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPickImageFromGallery = findViewById(R.id.btnPickImageFromGallery);
        btnMultipleImagesFromGallery = findViewById(R.id.btnMultipleImagesFromGallery);
        imageView = findViewById(R.id.imageView);

        permissionManager = PermissionManager.getInstance(this);

        btnPickImageFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!permissionManager.checkPermissions(permissions)) {
                    permissionManager.askPermissions(MainActivity.this, permissions,
                            PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION);
                } else {
                    pickSingleImageFromGallery();
                }
            }
        });

        btnMultipleImagesFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (!permissionManager.checkPermissions(permissions)) {
                    permissionManager.askPermissions(MainActivity.this, permissions,
                            PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION);
                } else {
                    pickMultipleImagesFromGallery();
                }
            }
        });
    }

    private void pickMultipleImagesFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_MULTIPLE_IMAGES_FROM_GALLERY_REQUEST_CODE);
    }

    private void pickSingleImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_SINGLE_IMAGE_FROM_GALLERY_REQUEST_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode,
                                              @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == PICK_SINGLE_IMAGE_FROM_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);
        } else if (requestCode == PICK_MULTIPLE_IMAGES_FROM_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    File file = new File(uri.getPath());
                    Log.d("TAG_URI: ", file.getName());
                    imageView.setImageURI(data.getClipData().getItemAt(0).getUri());
                }
            } else {
                Uri uri = data.getData();
                File file = new File(uri.getPath());
                Log.d("TAG_PATH: ", file.getName());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onRequestPermissionsResult(int requestCode,
                                                     @NonNull @NotNull String[] permissions,
                                                     @NonNull @NotNull int[] grantResults) {
        if (requestCode == PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION && permissionManager.handlePermissionResult(MainActivity.this,
                PICK_SINGLE_IMAGE_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION, permissions,
                grantResults)) {
            pickSingleImageFromGallery();
        } else if (requestCode == PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION && permissionManager.handlePermissionResult(MainActivity.this,
                PICK_MULTIPLE_IMAGES_FROM_GALLERY_READ_EXTERNAL_STORAGE_PERMISSION, permissions,
                grantResults)) {
            pickMultipleImagesFromGallery();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}