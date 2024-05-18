package com.example.firebaseilkproje

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.firebaseilkproje.databinding.ActivityDescriptionsBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

@SuppressLint("ParcelCreator")
class DescriptionsActivity() : AppCompatActivity(), Parcelable {
    private lateinit var binding : ActivityDescriptionsBinding
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    val storage = Firebase.storage

    var isPermission = true

    var selectedImage : Uri? = null
    var selectedBitmap : Bitmap? = null
    val shareMap = hashMapOf<String, Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_second)
        binding = ActivityDescriptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shareButton.setOnClickListener { share(it) }
        binding.imageView.setOnClickListener {imageShare(it)}
        binding.imageAddButton.setOnClickListener { imageAdd(it) }

        binding.imageView.visibility = View.GONE

        auth = Firebase.auth
    }

    private fun imageAdd(view: View) {
        binding.imageView.visibility = View.VISIBLE
    }

    private fun imageShare(view: View) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){ // eger TIRAMUSU ve üstü versiyonsa
            if(ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                // izin verilmemiş, izin iste
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES),1)
            }else{
                // izin verilmis, galeri acılır
                isPermission=true
                openGallery()
            }
        }else{ // eger TIRAMUSU altı versiyonlarsa

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                // izin verilmemis
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                // izin verilmis, galeri acılır
                isPermission=true
                openGallery()
            }
        }
    }

    private fun openGallery() {
        // izin zaten verilmisse, galeriyi ac
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,2)
    }

    private fun share(view: View){
        if(selectedImage != null){
            val uuid = UUID.randomUUID()
            val imageUUID = "${uuid}.jpg"
            val reference = storage.reference
            val imagesReference = reference.child("images").child(imageUUID)
            imagesReference.putFile(selectedImage!!).addOnSuccessListener {
                // url alınacak
                val uploadedImageReference = FirebaseStorage.getInstance().reference.child("images").child(imageUUID)
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri->
                    val downloadUrl = uri.toString()
                    saveToDatabase(downloadUrl)
                }.addOnFailureListener {
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
        else{ saveToDatabase(null) }
    }
    private fun saveToDatabase(downloadUrl :String?){
        val sharedDescriptions = binding.descriptionsEditText.text.toString()
        val username = auth.currentUser?.displayName.toString()
        val time = Timestamp.now()
        shareMap.put("sharedDescriptions", sharedDescriptions)
        shareMap.put("username", username)
        shareMap.put("time", time)
        if (downloadUrl != null){
            shareMap.put("imageUrl", downloadUrl)
        }
        db.collection("Descriptions").add(shareMap).addOnCompleteListener {
            if (it.isSuccessful){
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // izinler sonucunda
        if (requestCode == 1){
            // izin verilince yapılacaklar
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
            binding.imageView.visibility = View.VISIBLE
            selectedImage = data.data
            if (selectedImage != null){
                if (Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(this.contentResolver, selectedImage!!)
                    selectedBitmap = ImageDecoder.decodeBitmap(source)
                    binding.imageView.setImageBitmap(selectedBitmap)

                }else{
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
                    binding.imageView.setImageBitmap(selectedBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }


}