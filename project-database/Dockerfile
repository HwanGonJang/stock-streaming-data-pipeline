FROM postgres:16-alpine

# 환경 변수 설정
ENV POSTGRES_DB=gon_stock_dashboard
ENV POSTGRES_USER=admin
ENV POSTGRES_PASSWORD=password123!
ENV PGDATA=/var/lib/postgresql/data/pgdata

# 초기화 스크립트 복사
COPY ddl.sql /docker-entrypoint-initdb.d/01-ddl.sql
COPY init-user.sql /docker-entrypoint-initdb.d/02-init-user.sql

# 포트 노출
EXPOSE 5432

# PostgreSQL 시작
CMD ["postgres"]