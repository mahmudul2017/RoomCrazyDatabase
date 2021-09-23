package com.room.crazydatabase.roomDB.vm

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.room.crazydatabase.roomDB.model.ImageModel
import com.room.crazydatabase.roomDB.repo.ImageRepository

class ImageViewModel: ViewModel() {
    private var liveDataLogin: LiveData<ImageModel>? = null
    var liveDataUserList: MutableLiveData<List<ImageModel>>? = null
    private var liveDataUserLists: List<ImageModel>? = null

    fun insertDataVM(context: Context, username: String, image: ByteArray) {
        ImageRepository.insertData(context, username, image)
    }

    /*fun getLoginDetailsVM(context: Context, username: String): LiveData<ImageModel>? {
        liveDataLogin = ImageRepository.getLoginDetails(context, username)
        return liveDataLogin
    }*/

    /*fun getUserListsVM(context: Context): LiveData<List<LoginUser>>? {
        liveDataUserList = LoginRepository.getUserListsRepo(context)
        return liveDataUserList
    }*/

    fun getUserListsVM(context: Context): List<ImageModel>? {
        liveDataUserLists = ImageRepository.getUserListsRepo(context)
        //liveDataUserList!!.value = liveDataUserLists
        return liveDataUserLists
    }

    fun deleteUserVM(context: Context, loginUser: ImageModel) {
        ImageRepository.deleteUserRepo(context, loginUser)
    }

    fun deleteUserListsVM(context: Context) {
        ImageRepository.deleteUserListsRepo(context)
    }
}