package com.room.crazydatabase.roomDB.repo

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import com.room.crazydatabase.roomDB.db.ImageDatabase
import com.room.crazydatabase.roomDB.model.ImageModel

class ImageRepository {
    companion object {
        var imageDatabase: ImageDatabase? = null

        var loginModel: LiveData<ImageModel>? = null
        var loginAllUsers: LiveData<List<ImageModel>>? = null
        var loginUserList: List<ImageModel>? = null

        private fun initializeDB(context: Context): ImageDatabase {
            return ImageDatabase.getDataBaseClient(context)
        }

        fun insertData(context: Context, username: String, image: ByteArray) {
            imageDatabase = initializeDB(context)

            var userInfo = ImageModel(username, image)
            imageDatabase!!.imageDao().insertLoginData(userInfo)

            /*CoroutineScope(Main).launch {
                var userInfo = LoginUser(username, password, comment, image)
                loginDatabase!!.loginUserDao().insertLoginData(userInfo)
            }*/
        }

        /*fun getLoginDetails(context: Context, username: String): LiveData<ImageModel>? {
            imageDatabase = initializeDB(context)

            loginModel = imageDatabase!!.loginUserDao().getLoginDetails(username)
            return loginModel
        }*/

        /*fun getUserListsRepo(context: Context): LiveData<List<LoginUser>>? {
            loginDatabase = initializeDB(context)

            loginAllUsers = loginDatabase!!.loginUserDao().getUserLists()
            return loginAllUsers
        }

        fun deleteUserRepo(context: Context, loginUser: LoginUser) {
            loginDatabase = initializeDB(context)

            loginDatabase!!.loginUserDao().deleteUser(loginUser)
        }

        fun deleteUserListsRepo(context: Context) {
            loginDatabase = initializeDB(context)

            loginDatabase!!.loginUserDao().deleteUserList()
        }
    }*/

        fun getUserListsRepo(context: Context): List<ImageModel>? {
            imageDatabase = initializeDB(context)

            loginUserList = imageDatabase!!.imageDao().getUserLists()
            return loginUserList
        }

        fun deleteUserRepo(context: Context, loginUser: ImageModel) {
            imageDatabase = initializeDB(context)

            imageDatabase!!.imageDao().deleteUser(loginUser)
        }

        fun deleteUserListsRepo(context: Context) {
            imageDatabase = initializeDB(context)

            imageDatabase!!.imageDao().deleteUserList()
        }
    }
}