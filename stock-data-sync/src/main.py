#!/usr/bin/env python3

import sys
import json
import logging
from datetime import datetime
from config import Config
from sync_service import StockDataSyncService

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(sys.stdout)
    ]
)

logger = logging.getLogger(__name__)

def main():
    """Main entry point for the stock data sync application"""
    
    # Get sync type from command line arguments
    sync_type = sys.argv[1] if len(sys.argv) > 1 else None
    
    try:
        # Validate configuration
        Config.validate_config()
        
        # Create sync service and run
        sync_service = StockDataSyncService()
        results = sync_service.run_sync(sync_type)
        
        # Log results
        logger.info(f"Sync results: {json.dumps(results, indent=2)}")
        
        # Exit with appropriate code
        if 'error' in results:
            logger.error("Sync failed")
            sys.exit(1)
        elif results.get('results', {}).get('error_count', 0) > 0:
            logger.warning("Sync completed with errors")
            sys.exit(1)
        else:
            logger.info("Sync completed successfully")
            sys.exit(0)
            
    except Exception as e:
        logger.error(f"Application error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()