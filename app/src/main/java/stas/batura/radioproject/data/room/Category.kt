package stas.batura.radioproject.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "category_table", foreignKeys = [ForeignKey(entity = Podcast::class
    , parentColumns = ["podcastId"]
    , childColumns = ["podcastCatId"]
    , onDelete = ForeignKey.CASCADE
)])
data class Category (

    val podcastCatId: Long,

    val category: String
){
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0L
}