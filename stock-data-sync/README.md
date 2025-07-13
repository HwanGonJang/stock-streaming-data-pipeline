# Stock Data Synchronization Service

이 서비스는 주식 데이터를 Alpha Vantage와 Finnhub API에서 가져와서 PostgreSQL 데이터베이스에 동기화하는 Python 애플리케이션입니다.

## 기능

- **일일 동기화**: 매일 장 마감 후 주식 시세 데이터 수집
- **주간 동기화**: 매주 일요일 회사 프로필 데이터 수집
- **분기별 동기화**: 분기별로 재무제표 데이터 수집
- **Rate Limiting**: API 호출 제한 준수
- **에러 처리**: 실패한 요청에 대한 재시도 로직
- **Kubernetes CronJob**: 스케줄링된 자동 실행

## 프로젝트 구조

```
stock-data-sync/
├── src/
│   ├── config.py          # 설정 관리
│   ├── database.py        # 데이터베이스 연결 및 쿼리
│   ├── api_client.py      # API 클라이언트 (Alpha Vantage, Finnhub)
│   ├── sync_service.py    # 동기화 서비스 로직
│   └── main.py           # 메인 애플리케이션
├── k8s/
│   ├── namespace.yaml     # Kubernetes 네임스페이스
│   ├── secrets.yaml       # API 키 및 DB 인증 정보
│   ├── cronjob-daily.yaml    # 일일 동기화 CronJob
│   ├── cronjob-weekly.yaml   # 주간 동기화 CronJob
│   └── cronjob-quarterly.yaml # 분기별 동기화 CronJob
├── Dockerfile
├── requirements.txt
├── .env.example
└── README.md
```

## 설치 및 설정

### 1. 환경 변수 설정

`.env.example`을 참고하여 `.env` 파일을 생성하고 필요한 값들을 설정하세요.

```bash
cp .env.example .env
# .env 파일을 편집하여 API 키와 데이터베이스 정보를 입력
```

### 2. 로컬 실행

```bash
# 의존성 설치
pip install -r requirements.txt

# 동기화 실행
python src/main.py daily      # 일일 동기화
python src/main.py weekly     # 주간 동기화
python src/main.py quarterly  # 분기별 동기화
python src/main.py full       # 전체 동기화
```

### 3. Docker 빌드

```bash
# Docker 이미지 빌드
docker build -t stock-data-sync:latest .

# Docker 실행
docker run --env-file .env stock-data-sync:latest daily
```

## Kubernetes 배포

### 1. 네임스페이스 및 시크릿 생성

```bash
# 네임스페이스 생성
kubectl apply -f k8s/namespace.yaml

# 시크릿 생성 (먼저 secrets.yaml 파일의 값들을 실제 값으로 변경)
kubectl apply -f k8s/secrets.yaml
```

### 2. CronJob 배포

```bash
# 일일 동기화 CronJob
kubectl apply -f k8s/cronjob-daily.yaml

# 주간 동기화 CronJob
kubectl apply -f k8s/cronjob-weekly.yaml

# 분기별 동기화 CronJob
kubectl apply -f k8s/cronjob-quarterly.yaml
```

### 3. 수동 Job 실행

```bash
# 일일 동기화 수동 실행
kubectl create job --from=cronjob/stock-data-sync-daily manual-daily-sync -n stock-pipeline

# Job 상태 확인
kubectl get jobs -n stock-pipeline

# 로그 확인
kubectl logs job/manual-daily-sync -n stock-pipeline
```

## 스케줄링

- **일일 동기화**: 평일 오후 5시 ET (21:00 UTC) - 장 마감 후
- **주간 동기화**: 매주 일요일 오전 2시 ET (06:00 UTC)
- **분기별 동기화**: 분기별 1일 오전 3시 ET (07:00 UTC)

## 데이터 소스

### Alpha Vantage API
- 일일 주식 시세
- 회사 개요 정보
- 재무제표 데이터
- API 제한: 분당 5회 호출

### Finnhub API
- 회사 프로필 (보조 데이터 소스)
- API 제한: 분당 60회 호출

## 모니터링

### 로그 확인
```bash
# CronJob 로그 확인
kubectl logs cronjob/stock-data-sync-daily -n stock-pipeline

# 특정 Job 로그 확인
kubectl logs job/stock-data-sync-daily-xxxxx -n stock-pipeline
```

### 상태 확인
```bash
# CronJob 상태 확인
kubectl get cronjobs -n stock-pipeline

# Job 히스토리 확인
kubectl get jobs -n stock-pipeline
```

## 문제 해결

### 일반적인 문제들

1. **API 키 오류**
   - 환경 변수 또는 Kubernetes Secret의 API 키 확인
   - API 키 유효성 및 할당량 확인

2. **데이터베이스 연결 오류**
   - 데이터베이스 호스트, 포트, 인증 정보 확인
   - 네트워크 연결 및 방화벽 설정 확인

3. **Rate Limiting**
   - API 호출 제한 준수 확인
   - 로그에서 rate limiting 메시지 확인

4. **Docker 이미지 문제**
   - 이미지 빌드 및 push 상태 확인
   - Kubernetes에서 이미지 pull 정책 확인

### 디버깅

```bash
# 수동으로 컨테이너 실행하여 디버깅
kubectl run debug-pod --image=stock-data-sync:latest -it --rm --restart=Never -n stock-pipeline -- /bin/bash

# 환경 변수 확인
kubectl exec -it debug-pod -n stock-pipeline -- env
```

## 개발

### 코드 수정 후 배포

1. 코드 수정
2. Docker 이미지 재빌드
3. Kubernetes에 이미지 업데이트
4. CronJob 재배포

```bash
# 이미지 빌드 및 푸시
docker build -t your-registry/stock-data-sync:v1.1 .
docker push your-registry/stock-data-sync:v1.1

# CronJob 이미지 업데이트
kubectl patch cronjob stock-data-sync-daily -n stock-pipeline -p '{"spec":{"jobTemplate":{"spec":{"template":{"spec":{"containers":[{"name":"stock-data-sync","image":"your-registry/stock-data-sync:v1.1"}]}}}}}}'
```