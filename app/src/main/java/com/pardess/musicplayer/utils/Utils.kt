package com.pardess.musicplayer.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import com.pardess.musicplayer.domain.model.Album
import com.pardess.musicplayer.domain.model.Artist
import com.pardess.musicplayer.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Utils {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun isOverR(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.M)
    fun isOverM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isOverS(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    suspend fun String.getImage(): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(this@getImage)
                val art = retriever.embeddedPicture
                retriever.release()

                art?.let { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun List<Song>.getArtistsFromSongs(): List<Artist> {
        return this
            .groupBy { it.artistId } // 아티스트 ID 기준으로 그룹화
            .map { (artistId, artistSongs) ->
                Artist(
                    id = artistId, // 첫 번째 곡의 아티스트 ID 사용
                    name = artistSongs.first().artistName, // 첫 번째 곡의 아티스트명 사용
                    albums = emptyList(), // 필요하면 이후에 앨범을 추가
                    songs = artistSongs // 해당 아티스트의 모든 곡 포함
                )
            }
            .sortedBy { it.name } // 아티스트 이름 기준 정렬 (A-Z)
    }


    fun List<Song>.getAlbumsFromSongs(): List<Album> {
        return this
            .groupBy { it.albumId } // 앨범 ID 기준으로 그룹화
            .map { (albumId, albumSongs) ->
                Album(
                    id = albumId,
                    title = albumSongs.first().albumName, // 첫 번째 곡의 앨범명 사용
                    artistId = albumSongs.first().artistId, // 같은 앨범의 곡들은 같은 아티스트
                    artistName = albumSongs.first().artistName,
                    year = albumSongs.first().year, // 가장 먼저 발견된 연도를 사용
                    songCount = albumSongs.size, // 앨범 내 곡 개수
                    songs = albumSongs // 해당 앨범의 모든 곡 포함
                )
            }
            .sortedBy { it.title } // 앨범 이름 기준 정렬 (A-Z)
    }

    fun Long.toTime(): String {
        val stringBuffer = StringBuffer()

        val minutes = (this / 60000).toInt()
        val seconds = (this % 60000 / 1000).toInt()

        stringBuffer
            .append(String.format("%02d", minutes))
            .append(":")
            .append(String.format("%02d", seconds))

        return stringBuffer.toString()
    }

}