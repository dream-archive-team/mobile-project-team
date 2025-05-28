# mobile-project-team

모바일 프로그래밍 팀 프로젝트

## Google Gemini API 연동 기본 설정 및 테스트<br>

* API 키 발급 및 보안 설정<br>
* API 요청 함수 기본 코드 작성<br>
* API 실패 및 오류 처리 로직 작성

## API 연동 및 API 키 보안설정 완료<br>

*`local.properties`를 사용해 API 키 보안 설정
* LogCat에서 API 응답 성공 메시지 출력 확인

## 꿈 분석 기능 구현 완료<br>

* 시스템 프롬프트를 활용하여 꿈 분석 요청 구성:
"You are a creative novelist and an expert in psychological analysis. "
"Listen to the user’s dream and interpret its symbols and emotions in a richly narrative style. "
"Respond only in Korean, use at least five sentences across multiple paragraphs, and do not ask any follow-up questions. "
"Keep it concise—around 1,000 characters—and finish with a warm, encouraging sentence.";

* 한국어 번역:
  당신은 창의적인 소설가이자 심리 분석 전문가입니다.
  사용자의 꿈을 듣고 상징과 감정을 서사적으로 해석하세요.
  한국어로만 답하고, 여러 단락으로 다섯 문장 이상 작성하며 추가 질문은 하지 마세요.
  간결하게—약 1,000자 내외—정리하고, 마지막엔 따뜻한 격려의 문장을 덧붙이세요.

* 5월 27일 기준 main과 병합 테스트 완료
