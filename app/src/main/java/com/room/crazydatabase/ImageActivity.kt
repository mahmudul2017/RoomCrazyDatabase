package com.room.crazydatabase

import android.Manifest
import android.R.attr
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.room.crazydatabase.roomDB.vm.ImageViewModel
import kotlinx.android.synthetic.main.activity_image.*
import java.io.ByteArrayOutputStream
import java.io.File
import android.R.attr.path
import java.net.URI
import android.graphics.drawable.BitmapDrawable
import id.zelory.compressor.Compressor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ImageActivity : AppCompatActivity() {
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var alertProfileDialog: AlertDialog
    private lateinit var compressedImageFile: File
    private lateinit var imageFile: File

    private lateinit var username: String
    var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        imgPicker.setOnClickListener {
            chooseProfilePicture()
        }

        imgDelete.setOnClickListener {
            imageViewModel.deleteUserListsVM(this)
        }

        btnUserList.setOnClickListener {
            var userLists = imageViewModel.getUserListsVM(this)
            Log.d("userList", userLists?.get(0)?.userName.toString())
        }

        btnLogin.setOnClickListener {
            username = edtUserName.text.toString().trim()

            when {
                username.isNullOrEmpty() -> {
                    edtUserName.error = "Please enter the username"
                }
                else -> {
                    val image = (imgProfile.drawable as BitmapDrawable).bitmap

                    // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                    val tempUri: Uri = getImageUri(applicationContext, image!!)!!
                    // CALL THIS METHOD TO GET THE ACTUAL PATH
                    val imageFile = File(getRealPathFromURI(tempUri))

                    /* GlobalScope.launch(Dispatchers.Main) {
                        val compressedImageFile = Compressor.compress(this@ImageActivity, imageFile)
                        Log.d("image", "$compressedImageFile")
                    } */

                    //val image = BitmapFactory.decodeFile(imagePath)
                    val stream = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val imageInByte: ByteArray = stream.toByteArray()

                    Log.d("image", "$image")

                    if (imageInByte != null) {
                        imageViewModel.insertDataVM(
                            this@ImageActivity,
                            username,
                            imageInByte
                        )
                        Log.d("userData", "$username ${imageInByte.size}")
                        Toast.makeText(
                            this@ImageActivity,
                            "Inserted Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@ImageActivity,
                            "Please select profile image",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun chooseProfilePicture() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_profile_pic, null)
        builder.setCancelable(false)
        builder.setView(dialogView)

        val imgCamera: ImageView = dialogView.findViewById(R.id.imgCamera)
        val imgGallery: ImageView = dialogView.findViewById(R.id.imgGallery)
        val imgClose: ImageView = dialogView.findViewById(R.id.imgClose)

        imgClose.setOnClickListener {
            alertProfileDialog.cancel()
        }

        imgCamera.setOnClickListener {
            if (checkAndRequestPermissions()) {
                takePictureFromCamera()
                Log.d("imgCamera", "imgCamera called")
            }
        }

        imgGallery.setOnClickListener {
            takePictureFromGallery()
        }

        alertProfileDialog = builder.create()
        alertProfileDialog.show()
    }

    private fun checkAndRequestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            val cameraPermission =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                // this method is called only activity
                /* ActivityCompat.requestPermissions(
                    requireContext() as Activity,
                    arrayOf(Manifest.permission.CAMERA),
                    20
                ) */

                // this method is called only fragment
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    20
                )
                return false
            }
        }
        return true
    }

    private fun takePictureFromGallery() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, 1)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureFromCamera()
            alertProfileDialog.cancel()
        } else {
            Toast.makeText(this, "Permission not Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private fun takePictureFromCamera() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePicture.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePicture, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                alertProfileDialog.cancel()
                val selectedImageUri: Uri = intent!!.data!!
                // val path: String? = selectedImageUri.path
                imageFile = File(getRealPathFromURI(selectedImageUri))
                imagePath = imageFile.path
                imgProfile.setImageURI(selectedImageUri)
                Log.d("img", "imgGallery called :: uri $selectedImageUri :: imagePath $imagePath :: file $imageFile")
            }
            2 -> if (resultCode == RESULT_OK) {
                alertProfileDialog.cancel()
                val bundle: Bundle? = intent!!.extras
                val bitmapImage = bundle!!["data"] as Bitmap?
                imgProfile.setImageBitmap(bitmapImage)
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                val tempUri: Uri = getImageUri(applicationContext, bitmapImage!!)!!
                // CALL THIS METHOD TO GET THE ACTUAL PATH
                imageFile = File(getRealPathFromURI(tempUri))
                imagePath = imageFile.path
                Log.d("img", "imgCamera called :: uri $tempUri :: imagePath $imagePath :: file $imageFile")
            }
        }
    }

    fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    fun getRealPathFromURI(uri: Uri?): String? {
        val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }
}