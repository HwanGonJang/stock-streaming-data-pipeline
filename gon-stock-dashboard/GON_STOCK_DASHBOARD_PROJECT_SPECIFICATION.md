# Gon Stock Dashboard - Project Specification

## 🎯 Project Overview

**서비스 이름**: Gon Stock Dashboard  
**목표**: 20개의 주요 나스닥 기술 주식에 대해 실시간 주식 정보를 제공하고 투자 인사이트를 제공하는 대시보드  
**타겟 종목**: ["AAPL", "MSFT", "GOOGL", "AMZN", "META", "NVDA", "TSLA", "AVGO", "CRM", "ORCL", "NFLX", "ADBE", "AMD", "INTC", "PYPL", "CSCO", "QCOM", "TXN", "AMAT", "PLTR"]

## 🏗️ System Architecture

### Backend Infrastructure
- **실시간 스트리밍**: Kafka + Spark Streaming을 통한 실시간 주식 데이터 처리
- **데이터 소스**: Finnhub WebSocket API를 통한 실시간 트레이딩 데이터
- **데이터베이스**: Cassandra (거래 데이터), PostgreSQL (기본 정보, 뉴스, 추천)
- **API**: Spring Boot REST API + Server-Sent Events (SSE)
- **인프라**: Kubernetes 기반 마이크로서비스 아키텍처

### Frontend Requirements
- **실시간 데이터 처리**: Server-Sent Events (SSE) 지원
- **차트 라이브러리**: 캔들스틱 차트, 라인 차트, 거래량 차트
- **반응형 디자인**: 데스크톱, 태블릿, 모바일 지원
- **성능**: 실시간 데이터 업데이트 최적화

## 📱 주요 기능

### 1. 메인 페이지 (Dashboard)

#### 주식 종목 리스트
- **표시 정보**:
  - 종목명 (Symbol) 및 회사명
  - 현재가 (실시간)
  - 등락률 (전일 대비 퍼센트)
  - 등락액 (전일 대비 절댓값)
  - 거래량
  - 투자 추천 점수 (recommendationScore)
  - 투자 추천 라벨 (recommendationLabel)

- **정렬 기능**:
  - 추천 점수 (recommendationScore) 순
  - 등락률 순
  - 거래량 순
  - 시가총액 순
  - 알파벳 순

#### 주식 추천 섹션
- **최고 추천 종목**: recommendationScore가 가장 높은 종목 강조 표시
- **추천 이유**: 요약 정보 (summary) 표시
- **빠른 액세스**: 해당 종목 상세 페이지로 이동

#### 주요 뉴스 섹션
- **최신 뉴스**: 최근 5-10개 뉴스 표시
- **뉴스 정보**: 제목, 요약, 게시 시간, 관련 종목
- **감정 분석**: 긍정/부정/중립 라벨 표시

### 2. 주식 상세 페이지

#### 실시간 차트 영역
- **가격 차트**: 실시간 라인 차트 (SSE 기반 자동 업데이트)
- **캔들스틱 차트**: 일별/주별/월별 선택 가능
- **거래량 차트**: 가격 차트와 연동된 거래량 표시
- **기술 지표**: 이동평균선, 볼린저 밴드 등

#### 종목 기본 정보
- **기업 정보**: 회사명, 섹터, 산업, 국가, 거래소
- **주요 지표**: 시가총액, P/E 비율, EPS, 배당률
- **52주 최고/최저**: 연간 주가 범위
- **베타**: 시장 대비 변동성

#### 금융 정보
- **재무제표**: 손익계산서, 대차대조표, 현금흐름표
- **주요 재무 비율**: ROE, ROA, 부채비율 등
- **성장률**: 매출/순이익 성장률

#### 뉴스 정보
- **종목별 뉴스**: 해당 종목과 관련된 뉴스 리스트
- **감정 분석**: 각 뉴스의 긍정/부정 감정 점수
- **연관성 점수**: 뉴스와 종목의 관련도

#### 투자 정보
- **추천 등급**: 강력 매수 ~ 강력 매도 (5단계)
- **추천 점수**: 0.0000 ~ 1.0000 범위의 정밀 점수
- **투자 보고서**: 마크다운 형식의 상세 분석 보고서
- **목표 주가**: 애널리스트 목표 주가

## 🎨 UI/UX 디자인 가이드라인

### 디자인 콘셉트
- **모던하고 심플한 디자인**: 토스 증권 스타일의 깔끔한 인터페이스
- **다크/라이트 모드**: 사용자 선택 가능
- **색상 가이드**: 
  - 상승: #FF6B6B (빨간색)
  - 하락: #4ECDC4 (파란색)
  - 중립: #95A5A6 (회색)
  - 강조: #3498DB (메인 블루)

### 반응형 디자인
- **데스크톱**: 3-4컬럼 레이아웃
- **태블릿**: 2컬럼 레이아웃
- **모바일**: 1컬럼 레이아웃, 스와이프 네비게이션

### 성능 최적화
- **가상 스크롤**: 대용량 데이터 리스트 처리
- **메모이제이션**: 불필요한 리렌더링 방지
- **지연 로딩**: 이미지 및 컴포넌트 지연 로딩

## 🔧 기술 스택 권장사항

### Frontend
- **Framework**: React 18+ with TypeScript
- **State Management**: Zustand or Redux Toolkit
- **Styling**: Tailwind CSS or Styled Components
- **Charts**: Recharts or Chart.js
- **Real-time**: EventSource API (SSE)
- **HTTP Client**: Axios or Fetch API

### 실시간 데이터 처리
- **Server-Sent Events**: 실시간 주가 데이터 스트리밍
- **WebSocket**: 향후 양방향 통신 필요시
- **Auto-reconnection**: 연결 끊김 시 자동 재연결

## 📊 데이터 플로우

### 실시간 데이터
1. **Finnhub WebSocket** → **Kafka** → **Spark Streaming** → **Cassandra**
2. **API Server** → **SSE** → **Frontend**

### 정적 데이터
1. **외부 API** → **PostgreSQL** → **API Server** → **Frontend**

### 캐싱 전략
- **Redis**: 빈번히 조회되는 데이터 캐싱
- **브라우저 캐시**: 정적 리소스 캐싱
- **CDN**: 이미지 및 정적 파일 배포

## 🚀 개발 단계별 우선순위

### Phase 1: 기본 기능 (MVP)
1. 메인 페이지 주식 리스트 표시
2. 기본 정렬 기능
3. 상세 페이지 기본 정보 표시
4. 캔들스틱 차트 구현

### Phase 2: 실시간 기능
1. SSE 기반 실시간 데이터 연동
2. 실시간 가격 업데이트
3. 알림 시스템 구현

### Phase 3: 고급 기능
1. 기술 지표 추가
2. 포트폴리오 기능
3. 알림 설정
4. 사용자 맞춤 대시보드

## 📈 성능 목표

- **초기 로딩 시간**: 3초 이내
- **실시간 업데이트 지연**: 1초 이내
- **API 응답 시간**: 500ms 이내
- **동시 접속자**: 1000명 이상 지원

## 🔒 보안 및 규정 준수

- **API 인증**: JWT 토큰 기반 인증
- **Rate Limiting**: API 호출 제한
- **CORS**: 적절한 CORS 정책 설정
- **데이터 보안**: 민감 정보 암호화

## 📋 개발 가이드라인

- **코드 품질**: ESLint, Prettier 적용
- **테스트**: Jest, React Testing Library
- **문서화**: Storybook, API 문서
- **버전 관리**: Git Flow 적용
- **CI/CD**: GitHub Actions 또는 Jenkins

---

## 다음 단계

1. **API 명세서 검토**: 첨부된 API 문서 확인
2. **프로토타입 개발**: 핵심 기능 위주로 MVP 개발
3. **디자인 시스템**: 컴포넌트 라이브러리 구축
4. **성능 테스트**: 실시간 데이터 처리 성능 검증