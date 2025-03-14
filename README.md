# Stock Streaming Data Pipeline System

## Overview
The Stock Streaming Data Pipeline System collects real-time stock data via the Finnhub WebSocket, processes messages through Kafka, and utilizes Spark Streaming for data processing. The processed data is stored in Cassandra for analysis and visualization using Grafana. The system runs on a Kubernetes-based infrastructure.

## System Architecture
### 0. Infrastructure Layer
- **Operating System**: Ubuntu 24.02 LTS
- **Containerization**: Kubernetes v1.31, Docker v24.0.7, containerd v1.7.23, Calico v3.29.0, Helm v3.16.2
- **Networking**: MetalLB v0.13.11, Nginx Ingress Controller v1.9.4, Cloudflare (hwangonjang.com)
- 
### 1. Finnhub Producer (Data Collection Layer) `./finnhub-producer`
- Stock data transmission application based on Python 3.9.6
- Real-time WebSocket client implementation using the Finnhub API
- Message encoding based on Avro schema

### 2. Kafka Producer (Message Broker Layer) `./kafka`
- Real-time stock data message storage and transmission using Kafka & Zookeeper
- Kafka v6.2.0
- Zookeeper v6.2.0
- Integrated Kafdrop monitoring tool

### 3. Spark Streaming (Stream Processing Layer) `./stream-processor`
- Spark v3.5.3
- StreamProcessor application based on Scala v2.12.18
- Cluster management using Kubeflow Spark Operator

### 4. Cassandra (Database Layer) `./cassandra`
- Cassandra v3.11.17
- Multi-node cluster setup
- Schema management using CQL

### 5. Grafana (Visualization Layer) `./grafana`
- Grafana v11.3.0
- Integrated Cassandra plugin
- Custom dashboard templates

## Installation Guide

```sh
export S3_BUCKET_NAME=gon-data-pipeline-helm
helm s3 init s3://$S3_BUCKET_NAME
# Initialized empty repository at s3://$S3_BUCKET_NAME

helm repo add gon-data-pipeline s3://$S3_BUCKET_NAME
# "gon-data-pipeline" has been added to your repositories
```

## Repository Structure
```
.
├── stock-streaming-data-pipeline/  # Infrastructure layer - Helm Chart
├── finnhub-producer/               # Data collection layer
├── kafka/                          # Message broker layer
├── stream-processor/               # Stream processing layer
├── cassandra/                      # Database layer
└── grafana/                        # Visualization layer
```

## Contact
For any inquiries, please reach out via GitHub Issues or email.

---

The Stock Streaming Data Pipeline System is a Kubernetes-based real-time data pipeline leveraging Kafka and Spark Streaming for real-time analysis. Continuous improvements will enhance its efficiency in data processing and analysis.

