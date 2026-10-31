#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Load environment variables from .env.local
echo -e "${YELLOW}📋 Loading environment variables from .env.local...${NC}"
export $(cat .env.local | grep -v '^#' | xargs)
echo -e "${GREEN} Environment variables loaded${NC}"
echo ""


# Run tests
echo -e "${GREEN} Running tests...${NC}"
echo ""
./mvnw clean install
