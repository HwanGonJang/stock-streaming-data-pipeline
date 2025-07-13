-- 어드민 권한을 가진 사용자 생성
CREATE USER gonadmin WITH PASSWORD 'password123!';

-- 데이터베이스 생성 권한 부여
ALTER USER gonadmin CREATEDB;

-- 슈퍼유저 권한 부여 (필요한 경우)
ALTER USER gonadmin WITH SUPERUSER;

-- 현재 데이터베이스에 대한 모든 권한 부여
GRANT ALL PRIVILEGES ON DATABASE gon_stock_dashboard TO gonadmin;

-- 스키마 권한 부여
GRANT ALL ON SCHEMA public TO gonadmin;

-- 향후 생성될 테이블에 대한 기본 권한 설정
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO gonadmin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO gonadmin;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO gonadmin;