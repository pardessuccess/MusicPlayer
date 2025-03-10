package com.pardess.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.annotation.ChecksSdkIntAtLeast
import com.pardess.model.Album
import com.pardess.model.Artist
import com.pardess.model.Song
import com.pardess.model.enums.SpeechStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer

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

    // 기존 normalizeText 함수 (한글, 영문, 숫자, 공백 정리)
    fun normalizeText(text: String): String {
        // 1. NFD로 분해 (라틴 악센트 제거 용)
        val nfd = Normalizer.normalize(text, Normalizer.Form.NFD)
        // 2. 결합 다이아크리틱 문자 제거
        val withoutDiacritics = nfd.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        // 3. NFC로 재구성하여 한글은 완성형으로 복원
        val recomposed = Normalizer.normalize(withoutDiacritics, Normalizer.Form.NFC)
        // 4. 줄바꿈을 띄어쓰기로 변환하고 연속 공백을 하나로 축소
        val withSpaces = recomposed
            .replace(Regex("[\\r\\n]+"), " ")
            .replace(Regex("\\s+"), " ")
        // 5. 유사 문자 변환
        val replaced = withSpaces
            .replace("ä", "a")
            .replace("ö", "o")
            .replace("ü", "u")
            .replace("ß", "ss")
        // 6. 특수문자 제거 (한글, 영문, 숫자, 공백 이외 제거)
        val cleaned = replaced.replace(Regex("[^ㄱ-ㅎ가-힣a-zA-Z0-9\\s]"), "")
        // 7. 앞뒤 공백 제거 및 소문자 변환
        return cleaned.trim().lowercase()
    }

    val CHOSUNG = arrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
        'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ',
        'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    // 입력 문자열이 초성만으로 이루어졌는지 확인하는 함수
    fun isChosungOnly(query: String): Boolean {
        val filtered = query.filter { !it.isWhitespace() }
        // 빈 문자열이 아니고, 모든 문자가 CHOSUNG 배열에 포함되어 있으면 초성만 입력된 것으로 판단
        return filtered.isNotEmpty() && filtered.all { it in CHOSUNG }
    }

    // 한글 음절(가~힣)을 초성 문자열로 변환하는 함수
    fun extractChosung(text: String): String {
        val result = StringBuilder()
        for (c in text) {
            if (c in '\uAC00'..'\uD7A3') { // 가 ~ 힣
                val syllableIndex = c - '\uAC00'
                val choseongIndex = syllableIndex / (21 * 28)
                result.append(CHOSUNG[choseongIndex])
            } else {
                result.append(c)
            }
        }
        return result.toString()
    }
    fun <T> rearrangeList(list: List<T>, index: Int): List<T> {
        if (index < 0 || index >= list.size) return list // 유효하지 않은 인덱스 처리

        val front = list.subList(0, index) // 앞쪽 리스트 (index 앞 요소들)
        val back = list.subList(index, list.size) // index 포함 뒷쪽 리스트

        return back + front // 뒷쪽 리스트에 앞쪽 리스트를 붙여서 반환
    }

    fun setSpeechRecognizer(
        speechRecognizer: SpeechRecognizer,
        setSpeechStatusMsg: (String) -> Unit,
        setSpeechResult: (String) -> Unit,
        setSpeechStatus: (SpeechStatus) -> Unit,
    ) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                setSpeechStatusMsg("음성 인식 준비 중...")
                setSpeechStatus(SpeechStatus.READY)
            }

            override fun onBeginningOfSpeech() {
                setSpeechStatusMsg("음성 인식 중...")
                setSpeechStatus(SpeechStatus.IN_PROGRESS)
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {

            }

            override fun onEndOfSpeech() {
                setSpeechStatusMsg("음성 인식 완료")
                setSpeechStatus(SpeechStatus.COMPLETE)
            }

            override fun onError(error: Int) {
                setSpeechStatusMsg("오류 발생: $error")
                setSpeechStatus(SpeechStatus.ERROR)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                setSpeechResult(matches?.get(0) ?: "오류")
                setSpeechStatusMsg(matches?.get(0) ?: "결과를 찾을 수 없습니다.")
                println(matches?.get(0) ?: "결과를 찾을 수 없습니다.")
                if (matches?.get(0) != null) {
                    setSpeechStatus(SpeechStatus.COMPLETE)
                } else {
                    setSpeechStatus(SpeechStatus.ERROR)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

}