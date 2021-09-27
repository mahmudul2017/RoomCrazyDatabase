package com.room.crazydatabase

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.room.crazydatabase.roomDB.FileUtil
import com.room.crazydatabase.roomDB.vm.ImageViewModel
import id.zelory.compressor.Compressor
import id.zelory.compressor.loadBitmap
import kotlinx.android.synthetic.main.activity_compress.*
import kotlinx.android.synthetic.main.activity_image.*
import java.io.ByteArrayOutputStream
import java.io.File
import kotlinx.coroutines.*
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.math.pow

class ImageActivity : AppCompatActivity() {
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var alertProfileDialog: AlertDialog
    private lateinit var compressedImageFile: File
    private lateinit var galleryImageFile: File
    private lateinit var imageFile: File
    //private var imageBitmap: Bitmap? = null

    private lateinit var username: String
    var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        imgPicker.setOnClickListener {
            chooseProfilePicture()
        }

        btnCoroutines.setOnClickListener {
            val id = 10

            val callId = coroutineFunc()
            Log.d("callId", "$callId")
        }

        imgDelete.setOnClickListener {
            imageViewModel.deleteUserListsVM(this)
        }

        btnUserList.setOnClickListener {
            var userLists = imageViewModel.getUserListsVM(this)
            Log.d("userList", userLists?.get(0)?.userName.toString())
        }

        btnLogin.setOnClickListener {
            lifecycleScope.launch {
                galleryImageFile = compressImageSize(compressedImageFile)
                saveImageDb(galleryImageFile)
                Log.d(
                    "compressValue",
                    "coroutines called ${getReadableFileSize(galleryImageFile.length())} " +
                            ":: ${getReadableFileSize(compressedImageFile.length())}"
                )
            }
        }
    }

    private fun saveImageDb(compressFile: File?) {
        username = edtUserName.text.toString().trim()

        when {
            username.isNullOrEmpty() -> {
                edtUserName.error = "Please enter the username"
            }
            else -> {
                //val image = (imgProfile.drawable as BitmapDrawable).bitmap
                val image = BitmapFactory.decodeFile(compressFile.toString())

                val stream = ByteArrayOutputStream()
                //image!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
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

    private fun coroutineFunc(): Int {
        GlobalScope.launch(Dispatchers.Main) {
            btnCoroutines.text = "Called"
        }
        return 25
    }

    private suspend fun compressImageSize(file: File): File {
        return withContext(Dispatchers.Main) {
            Compressor.compress(this@ImageActivity, file)
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
                compressedImageFile = FileUtil.from(this, intent!!.data!!)?.also {
                    // actualSizeTextView.text = String.format("Size : %s", getReadableFileSize(it.length()))
                    // clearImage()
                }
                imgProfile.setImageBitmap(loadBitmap(compressedImageFile))

                /* val selectedImageUri: Uri = intent!!.data!!
                // val path: String? = selectedImageUri.path
                imageFile = File(getRealPathFromURI(selectedImageUri))
                // imagePath = imageFile.path
                var imageBitmap = convertUriToBitmap(selectedImageUri)
                imgProfile.setImageBitmap(imageBitmap)
                // imgProfile.setImageURI(selectedImageUri)
                Log.d("img", "imgGallery called :: uri $selectedImageUri :: imageBitmap $imageBitmap :: imageFile $imageFile") */
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
                Log.d(
                    "img",
                    "imgCamera called :: uri $tempUri :: imagePath $imagePath :: file $imageFile"
                )
            }
        }
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }

    private fun getRealPathFromURI(uri: Uri?): String? {
        val cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    private fun convertUriToBitmap(uri: Uri?): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            if (uri != null) {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            }
        } catch (e: Exception) {
            //handle exception
        }
        return bitmap
    }

    private fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (kotlin.math.log10(size.toDouble()) / kotlin.math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
    }
}