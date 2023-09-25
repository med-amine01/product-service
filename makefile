# Define variables

MYSQL_CONTAINER_NAME := docker-mysql
MYSQL_USER_NAME := root
MYSQL_USER_PASSWORD := Mad123**
MYSQL_DATABASE_NAME := product

# Display information about available targets
.PHONY: info
info:
	@echo "Available targets:"
	@echo "  start              - Start product-service using docker-compose"
	@echo "  start-debug        - Start Debug mode using docker-compose-debug"
	@echo "  stop               - Stop Docker containers using docker-compose"
	@echo "  drop-database      - Drop the MySQL database"
	@echo "  purge-containers   - Remove all running and stopped containers and prune volumes"
	@echo "  purge-images       - Remove all Docker images (depends on purge-containers)"
	@echo "  regenrate-jar-file - Clean and package Maven project (skip tests)"
	@echo "  compile            - Restart 'product-service' Docker container"


# Start Docker containers using docker-compose
.PHONY: start
start:
	@docker-compose -f docker-compose.yml up -d
	@echo ""
	@echo "                     _            _                            _           "
	@echo " _ __  _ __ ___   __| |_   _  ___| |_      ___  ___ _ ____   _(_) ___ ___  "
	@echo "| '_ \| '__/ _ \ / _\`| | | |/ __| __|____/ __|/ _ \ '__\ \ / / |/ __/ _ \\"
	@echo "| |_) | | | (_) | (_| | |_| | (__| ||_____\__ \  __/ |   \ V /| | (_|  __/ "
	@echo "| .__/|_|  \___/ \__,_|\__,_|\___|\__|    |___/\___|_|    \_/ |_|\___\___| "
	@echo "|_|                                                                        "
	@echo ""
	@echo "PRODUCT-SERVICE STARTED !"



# Start Docker containers using docker-compose
.PHONY: start-debug
start-debug:
	@docker-compose -f docker-compose-debug.yml up -d
	@echo "=========================== START DEBUGGING ===============================";

# Stop Docker containers using docker-compose
.PHONY: stop
stop:
	@docker-compose -f docker-compose.yml stop

# Drop the MySQL database
.PHONY: drop-database
drop-database:
	@echo "Dropping $(MYSQL_DATABASE_NAME) database"
	@docker exec -i $(MYSQL_CONTAINER_NAME) mysql -u$(MYSQL_USER_NAME) -p$(MYSQL_USER_PASSWORD) -e "DROP DATABASE IF EXISTS $(MYSQL_DATABASE_NAME);"
	@echo "MySQL database dropped"
	@echo "restart to launch migrations"

# Remove all running and stopped containers and prune volumes
.PHONY: purge-containers
purge-containers:
	@docker rm $$(docker ps -aq) -f
	@docker volume prune --force

# Remove all Docker images (depends on purge-containers)
.PHONY: purge-images
purge-images: purge-containers
	@docker rmi $$(docker images -aq) -f

# Clean and package Maven project (skip tests)
.PHONY: regenrate-jar-file
regenrate-jar-file:
	@mvn clean package -DskipTests

# Restart 'product-service' Docker container (depends on regenrate-jar-file)
.PHONY: compile
compile: regenrate-jar-file
	@docker restart product-service
