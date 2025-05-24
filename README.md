# mobile-project-team

모바일 프로그래밍 팀 프로젝트

# Google Gemini API 연동 기본 설정 및 테스트<br>

API 키 발급 및 보안 설정<br>
API 요청 함수 기본 코드 작성<br>
API 실패 및 오류 처리 로직 작성

# API 연동 및 API 키 보안설정 완료<br>

local.properties를 사용해 api 키 보안설정 기능.<br>
LogCat에서 API 응답 성공 메시지 출력 확인함.

# 꿈 분석 기능 구현 완료<br>

시스템 프롬프트를 활용하여 꿈 분석 요청 구성:<br>
"You are a creative novelist and an expert in psychological analysis. 
When you hear the user’s dream, delve into its symbols and emotions, 
interpret it in a richly narrative style, 
respond only in Korean without any English translation or additional languages,
limit your response to 1000 characters,
and focus solely on analyzing the dream content provided—do not ask any clarifying or follow-up questions."<br>

사용자가 입력한 꿈 내용을 기반으로 Gemini API 해석 결과를 UI의 `tvAnalysis`에 표시하도록 구현<br>
고정 높이 내 스크롤, 가독성 높인 줄바꿈 및 최대 1000자 제한 적용
