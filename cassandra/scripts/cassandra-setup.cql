CREATE KEYSPACE IF NOT EXISTS market 
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE market;

CREATE TABLE IF NOT EXISTS trades(
    uuid uuid,
    symbol text,
    trade_conditions text,
    price double,
    volume double,
    trade_timestamp timestamp,
    ingest_timestamp timestamp,
    PRIMARY KEY((symbol),trade_timestamp))
WITH CLUSTERING ORDER BY (trade_timestamp DESC);

CREATE INDEX IF NOT EXISTS ON trades (uuid);

CREATE TABLE IF NOT EXISTS running_averages_15_sec(
    uuid uuid,
    symbol text,
    price_volume_multiply double,
    ingest_timestamp timestamp,
    PRIMARY KEY((uuid),ingest_timestamp))
WITH CLUSTERING ORDER BY (ingest_timestamp DESC);

CREATE INDEX IF NOT EXISTS ON running_averages_15_sec (symbol);

CREATE TABLE IF NOT EXISTS news(
    uuid uuid,
    symbol text,
    category text,
    datetime timestamp,
    headline text,
    news_id bigint,
    image text,
    source text,
    summary text,
    url text,
    ingest_timestamp timestamp,
    PRIMARY KEY((symbol), datetime))
WITH CLUSTERING ORDER BY (datetime DESC);

CREATE INDEX IF NOT EXISTS ON news (uuid);