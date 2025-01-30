import json
import finnhub
import io
import avro.schema
import avro.io
from kafka import KafkaProducer


def load_client(token):
    """Set up and return a Finnhub client with the provided API token.

    Args:
        token (str): The API token for Finnhub.

    Returns:
        finnhub.Client: The initialized Finnhub client.
    """
    return finnhub.Client(api_key=token)


def lookup_ticker(finnhub_client, ticker):
    """Look up a ticker in Finnhub.

    Args:
        finnhub_client (finnhub.Client): The Finnhub client.
        ticker (str): The ticker symbol to look up.

    Returns:
        dict: The response from Finnhub containing ticker information.
    """
    return finnhub_client.symbol_lookup(ticker)


def ticker_validator(finnhub_client, ticker):
    """Validate if the specified ticker exists in Finnhub.

    Args:
        finnhub_client (finnhub.Client): The Finnhub client.
        ticker (str): The ticker symbol to validate.

    Returns:
        bool: True if the ticker exists, False otherwise.
    """
    for stock in lookup_ticker(finnhub_client, ticker)['result']:
        if stock['symbol'] == ticker:
            return True
    return False


def load_producer(kafka_server):
    """Set up and return a Kafka producer connected to the specified server.

    Args:
        kafka_server (str): The Kafka server address.

    Returns:
        KafkaProducer: The initialized Kafka producer.
    """
    return KafkaProducer(bootstrap_servers=kafka_server)


def load_avro_schema(schema_path):
    """Load an Avro schema from the specified file path.

    Args:
        schema_path (str): The file path to the Avro schema.

    Returns:
        avro.schema.Schema: The parsed Avro schema.
    """
    with open(schema_path) as schema_file:
        return avro.schema.parse(schema_file.read())


def avro_encode(data, schema):
    """Encode data into Avro format.

    Args:
        data (dict): The data to encode.
        schema (avro.schema.Schema): The Avro schema to use for encoding.

    Returns:
        bytes: The encoded Avro data.
    """
    writer = avro.io.DatumWriter(schema)
    bytes_writer = io.BytesIO()
    encoder = avro.io.BinaryEncoder(bytes_writer)
    writer.write(data, encoder)
    return bytes_writer.getvalue()
