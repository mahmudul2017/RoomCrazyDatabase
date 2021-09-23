package com.room.crazydatabase.roomDB.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.room.crazydatabase.roomDB.model.ImageModel

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoginData(imageModel: ImageModel)

    /*@Query("SELECT * FROM ImageUpload WHERE username = :username")
    fun getLoginDetails(username: String): LiveData<LoginUser>*/

    /*@Query("SELECT * FROM HiltLogIn")
    fun getUserLists(): LiveData<List<LoginUser>>*/

    @Query("SELECT * FROM ImageUpload")
    fun getUserLists(): List<ImageModel>

    @Delete
    fun deleteUser(imageModel: ImageModel)

    @Query("DELETE FROM ImageUpload")
    fun deleteUserList()
}