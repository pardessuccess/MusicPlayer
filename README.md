이 프로젝트는 노인 친화적인 음악 스트리밍 앱으로, 간편한 UI와 음성 인식 기능을 통해 스마트폰 사용이 익숙하지 않은 사용자도 쉽게 음악을 검색하고 감상할 수 있도록 설계되었습니다.

https://github.com/user-attachments/assets/3e121e86-1bcb-4b77-832e-32eb2e4ad554

---

## 주요 기술 스택

### UI & UX
- **Jetpack Compose** → 선언형 UI로 간결하고 유지보수 용이한 코드 작성
- **AutoResizeText** → 노인 친화적인 가변 크기 텍스트 제공
- **LazyColumnScrollbar** (라이브러리) → 직관적인 스크롤바 제공

### 데이터 관리 및 로컬 저장소
- **Room Database** → 로컬 음악 파일 메타데이터 저장 및 관리
- **DataStore** → 간단한 설정 값 저장

### 음악 재생 & 미디어 관련 기술
- **ExoPlayer** → 오디오 및 미디어 재생 최적화
- **Foreground Service** → 앱 종료 후에도 음악이 계속 재생
- **MediaSession** → 백그라운드에서도 음악 제어 가능

### 음성 인식
- **SpeechRecognizer API** → 음성 입력을 통해 음악 검색 및 플레이리스트 생성
- **RecognizerIntent** → 음성 명령을 위한 인텐트 처리

### 비동기 데이터 처리 (Coroutine Flow 적용)
- **Flow** → Room에서 데이터 변경 감지 및 실시간 UI 업데이트
- **StateFlow** → ViewModel에서 UI 상태(State) 관리
- **Channel** → UI 이벤트 처리 (예: 음악 재생 완료 알림)

### 의존성 주입 및 아키텍처
- **MVI (Model-View-Intent)** → 단방향 데이터 흐름 유지 및 UI 상태(State) 관리
- **StateFlow & SharedFlow** → UI 상태 및 이벤트 처리를 위한 비동기 데이터 흐름 관리
- **Clean Architecture** → 도메인 계층을 나누어 가독성 및 테스트 용이성 확보
- **Hilt (Dagger Hilt)** → 의존성 주입을 통해 결합도를 낮추고 모듈화된 코드 구성

### Multi Module 구조
- **app 모듈** → 최종 실행 가능한 메인 앱 모듈
- **core 모듈** → 공통적으로 사용되는 기능을 한 곳에서 관리
- **build-logic 모듈** → Gradle 빌드 설정을 중앙에서 관리하여 일관성 유지
- **feature 모듈** → 개별적인 기능(Feature) 담당
