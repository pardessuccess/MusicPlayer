package com.pardess.domain

import java.text.Normalizer
object Utils {

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
}