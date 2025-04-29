# mobile-project-team
모바일 프로그래밍 팀 프로젝트

# 꿈 보관소 (Dream Archive)
이 앱은 사용자가 자유롭게 꿈을 기록하면, AI가 꿈의 내용을 분석하여 그 의미를 해석하고, 이를 바탕으로 창작 소설을 자동으로 생성한다.
사용자는 자신의 꿈을 새로운 시각으로 바라볼 수 있으며, 꿈이 흥미로운 이야기로 재구성되는 과정을 통해 기록을 넘어 창작의 즐거움을 경험할 수 있다.
이 앱은 꿈을 창작 자원으로 활용하여 개인의 상상력과 창의력을 자극하고, 동시에 꿈의 의미를 확장하는 것을 목표로 한다.

## 기술 스택
- Android (Java)
- Google Gemini API
- RoomDB (SQLite)
- GitHub 협업 (Organization 기반 브랜치 전략)

## 🧵 브랜치 설명
'develop' 모든 기능이 통합되는 브랜치. 테스트 완료 후 `main`으로 병합 예정 
`feature/login` 로그인 기능 구현 (UI 및 인증 로직 포함) 
`feature/api-setup`  Google Gemini API 연동 기본 설정 및 테스트 
`feature/db-connection`  RoomDB를 통한 데이터베이스 연결 및 구조 구축 
`feature/dream-input`  사용자가 꿈 내용을 입력하는 기능 개발 
`feature/dream-analysis`  입력된 꿈 내용을 Gemini API로 해석하는 기능 구현 
`feature/dream-visualization`  해석 결과를 시각적으로 출력하는 화면 개발 
`feature/dream-list`  저장된 꿈 목록을 불러오고, 날짜별 정렬 등 UI 구현 
`feature/dream-delete`  꿈 기록 삭제 기능 구현
`feature/integration-test`  전체 기능 통합 테스트 및 오류 수정 전담 브랜치 
`feature/ui-ux-improvement`  전체 앱의 UI/UX 개선 작업
