package br.com.wsilva.opencvfacerecognition.model.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by wellingtonasilva on 24/12/17.
 */
@Entity(tableName = "person")
data class PersonEntity(@ColumnInfo(name = "pers_name") var name: String = "",
                        @ColumnInfo(name = "pers_phone") var phone: String = "",
                        @ColumnInfo(name = "pers_email") var email: String = "",
                        @ColumnInfo(name = "pers_address") var address: String = "",
                        @ColumnInfo(name = "pers_photoFilename1") var photoFilename1: String = "",
                        @ColumnInfo(name = "pers_photoFilename2") var photoFilename2: String = "",
                        @ColumnInfo(name = "pers_uuid") var uuid: String = "")
{
    @ColumnInfo(name = "pers_id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}