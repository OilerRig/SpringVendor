# SpringVendor

## Overview

**SpringVendor** is a Spring Boot microservice that provides vendor-specific product and order management APIs. It exposes RESTful endpoints to retrieve a vendor’s product catalog (including detailed specifications) and to create and track orders for those products. The service is designed with a focus on modularity and reliability, using multiple databases (relational and NoSQL) and robust authentication to ensure secure and dependable operations.

In practice, SpringVendor acts as the "Vendor Service" in a broader system, handling all vendor-related data and business logic. It integrates with a PostgreSQL database for core data (products, orders, and API keys for authentication) and a MongoDB database for complementary product details. This allows the service to efficiently manage structured data (orders and inventory) while also storing flexible, unstructured data (product specifications). The service is containerized for easy deployment and comes with built-in API documentation for quick integration and testing.

## Key Features

* **Product Catalog API:** Provides endpoints to list all products and fetch detailed information for a specific product. Product data (ID, name, price, stock, etc.) is stored in PostgreSQL, while extended specifications for each product (e.g. technical specs, descriptions) are stored in MongoDB and seamlessly combined in the API responses.

* **Order Management:** Allows clients to place orders for products via a REST API. The order endpoint will decrement product stock accordingly and record the order details in the database. The service also offers an endpoint to retrieve order details by order ID (including product, quantity, timestamp, etc.).

* **API Key-Based Authentication:** Secures sensitive endpoints (such as order placement and retrieval) using API keys. Clients must include a valid API key in the `X-API-KEY` header for protected requests. The service maintains a cache of valid API keys (stored in the PostgreSQL `users` table) and uses a custom security filter to authenticate requests. Public endpoints (like fetching product info or API docs) do not require a key, while any order-related endpoints do.

* **Polyglot Persistence (PostgreSQL + MongoDB):** Demonstrates integration of two database systems. Uses Spring Data JPA with PostgreSQL for structured data (ensuring transactional integrity for orders and inventory) and Spring Data MongoDB with MongoDB for unstructured data (storing rich product details). This combination showcases how to leverage the strengths of both SQL and NoSQL databases in one application.

* **Resilience with Spring Retry:** Employs Spring Retry in the order placement logic to handle transient failures or concurrency issues. For example, if two orders are placed simultaneously for the last items in stock, the service uses optimistic locking and retry mechanisms to gracefully handle the race condition. The order placement method is annotated to automatically retry a few times with a brief backoff if a concurrency conflict (like an `ObjectOptimisticLockingFailureException`) is detected, improving reliability in high-concurrency scenarios.

* **Dockerized Setup:** Provides a Dockerfile and a Docker Compose configuration for easy containerized deployment. The Docker Compose file will launch the SpringVendor service along with PostgreSQL and MongoDB containers, pre-configured with the necessary environment variables. This makes it simple to get the whole stack running locally or in a cloud environment with minimal setup.

* **API Documentation & Monitoring:** Includes built-in OpenAPI/Swagger documentation via Springdoc. When the service is running, an interactive API docs UI is available at the `/docs` path (and the OpenAPI JSON at `/docs/json`), allowing developers to explore and test the endpoints. Additionally, Spring Boot Actuator is included, providing health checks and other monitoring endpoints (these are secured behind API key auth by default, except for the basic health check if configured). This helps in observing service status and metrics in production.

* **Data Seeding for Demo:** On first startup, the service can optionally seed initial data into the databases for demonstration purposes. CSV files for sample products and details (for vendors like **Intel**, **Nvidia**, or **Asus**) are bundled with the application. Using an environment variable, you can choose which supplier’s dataset to load. This feature makes it easier to test the service out-of-the-box with realistic data without manual data entry.

## Technologies & Dependencies

SpringVendor is built with a modern Java stack and various Spring frameworks and libraries:

* **Java 17** – The application runs on Java 17 (LTS), leveraging its performance and language improvements.
* **Spring Boot 3 (3.4.x)** – Provides the backbone of the service, auto-configuring the web server and components:

    * *Spring Boot Starter Web* (embedded Tomcat, REST controllers),
    * *Spring Boot Starter Security* (used for configuring API key auth filter in a stateless manner),
    * *Spring Boot Starter Data JPA* (ORM and repository support for PostgreSQL),
    * *Spring Boot Starter Data MongoDB* (Mongo repository support),
    * *Spring Boot Starter Actuator* (operational endpoints for health and metrics).
* **Spring Security** – Configured for HTTP header API key authentication. A custom `OncePerRequestFilter` checks for a valid `X-API-KEY` header on protected endpoints.
* **Spring Retry** – Used in the service layer (order processing) to automatically retry transactions that encounter concurrency issues.
* **Springdoc OpenAPI (Swagger UI)** – For API documentation and testing endpoints via a web UI at `/docs`.
* **PostgreSQL** – Primary relational database for storing persistent data:

    * **Spring Data JPA** with Hibernate is used to map Java entities (Orders, Products, Users) to relational tables.
    * PostgreSQL Driver (JDBC) is included to connect to the database.
* **MongoDB** – Secondary NoSQL database for product details:

    * **Spring Data MongoDB** simplifies access to Mongo collections. Product specification documents are stored here and joined with product data in responses.
* **Docker & Docker Compose** – Containerization tools:

    * A Dockerfile is provided to containerize the SpringVendor application.
    * Docker Compose configuration is provided to run the app alongside PostgreSQL and MongoDB for local development or testing.
* **OpenCSV** – Used at startup to parse CSV files containing seed data for products and product details.
* **JUnit 5 & Mockito** – Testing frameworks:

    * Unit and web layer tests are written with JUnit Jupiter (5.x) and use Mockito for mocking dependencies.
    * AssertJ is included for fluent assertions in tests.
* **Maven** – Build system for the project (with the Maven Wrapper included for convenience). It manages dependencies and packaging of the application into an executable JAR.

Other notable details:

* The application uses **Spring Boot DevTools** (included as a dev dependency) to speed up development (auto-restart, etc., not used in production).
* **spring-dotenv** library is included to allow loading environment variables from a `.env` file during development, which can simplify managing configuration outside of Docker. (If a `.env` file is present in the project root, its variables will be loaded automatically when running locally.)

## Setup and Running the Application

You can run SpringVendor either in a local development environment (with the required databases available) or more easily via Docker Compose which sets up everything for you. Below are instructions for both methods:

### Prerequisites

* **Java 17+** installed (ensure `JAVA_HOME` is set appropriately if running directly).
* **Maven 3+** installed, or use the provided Maven Wrapper script (`mvnw` on Linux/Mac, `mvnw.cmd` on Windows) which will download the correct Maven version.
* **Docker** installed, if you plan to use Docker Compose for running the application and databases.
* (Optional) **Docker Compose** (if not included with your Docker installation) to orchestrate multi-container setup.

### Running with Docker Compose (Recommended)

Using Docker Compose is the quickest way to get the SpringVendor service up and running along with its required databases:

1. **Build the application JAR:**
   Before bringing up the containers, build the SpringVendor application. This step will compile the code and package it into an executable jar.

   ```bash
   ./mvnw clean package
   ```

   This will produce a jar file in the `target` directory (for example, `vendor-0.0.1-SNAPSHOT.jar`). The Dockerfile expects a jar to be present in `target/` to include in the container image.

2. **Start the containers:**
   Use Docker Compose to build the Docker image and start all services (the SpringVendor app, PostgreSQL, and MongoDB).

   ```bash
   docker-compose up --build
   ```

   The first run will build the custom Docker image for the SpringVendor service (using the jar from the previous step), and then start three containers:

    * `springvendor` – the Spring Boot application container.
    * `postgres` – a PostgreSQL 13+ database container (initialized with a database for the app).
    * `mongo` – a MongoDB container.

   Compose will automatically wire the network between the containers. The SpringVendor service will wait for the databases to be ready (due to Docker Compose’s dependency configuration) before fully starting.

3. **Environment configuration (handled by Docker Compose):**
   The Docker Compose file provides the necessary environment variables for the containers. Key environment variables and their roles:

    * `SPRING_PROFILE=prod`: Runs the app with the **production** profile (which expects external DB connections and runs on port 8080).
    * `DDL=update`: Instructs JPA to auto-create or update database tables on startup (via `spring.jpa.hibernate.ddl-auto=update`). This ensures the schema is prepared in the fresh PostgreSQL instance.
    * **PostgreSQL settings:**
      `SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/SpringVendor`
      `SPRING_DATASOURCE_USERNAME=oiler`
      `SPRING_DATASOURCE_PASSWORD=secret`
      These correspond to the PostgreSQL container’s credentials. The compose file sets up a database named `SpringVendor` and a user `oiler` with password `secret`. The JDBC URL uses the service name `postgres` (Docker DNS) on the default port 5432. If you change these values, ensure they match the Postgres container’s config.
    * **MongoDB settings:**
      `SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/springmongo`
      `SPRING_DATA_MONGODB_PASSWORD=` (empty, since the default MongoDB has no auth by default)
      This points to the Mongo container on the default port, using a database name `springmongo`. Authentication is not enabled for the default Mongo image (you can ignore the password variable or set up a user if needed).
    * `APP_SEED_SUPPLIER=nvidia`: Determines which dataset to seed on startup. In this case, the application will load the **Nvidia** product catalog from CSV files into the databases. You can change this value to `intel` or `asus` to seed a different vendor's data, or omit it to default to Intel.

   These environment variables are injected into the Spring Boot application container, so no additional configuration is needed. The Postgres container itself uses its own `POSTGRES_*` variables to initialize the database (`POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`), which are also set in the compose file.

4. **Accessing the application:**
   Once Docker Compose finishes starting all services, the SpringVendor app will be running on port **8080** (mapped to your host’s 8080). You can verify it’s up by checking the logs (`docker-compose logs springvendor`) or by visiting the health or docs endpoints:

    * Open a browser to [http://localhost:8080/docs](http://localhost:8080/docs) – you should see the Swagger UI with the API documentation.
    * Alternatively, check [http://localhost:8080/products](http://localhost:8080/products) – this should return a JSON array of products (if seeding was successful, you'll see several products listed).

5. **Stopping the application:**
   When done, you can stop and remove the containers with:

   ```bash
   docker-compose down
   ```

   This will stop the SpringVendor, Postgres, and Mongo containers. The Postgres container data will persist in a Docker volume named `springvendor_postgres_data` (created automatically), so if you bring the environment up again, the data (including any orders or user API keys you added) will still be there. The Mongo data is stored in an ephemeral container (no volume by default), so it will be fresh on each `up` unless you modify the compose file to add a volume for Mongo.

**Note:** The Docker image can also be built and run standalone. For example, after packaging, you could do:

```bash
docker build -t springvendor:latest .
docker run --env SPRING_PROFILE=prod --env ... (other env vars) -p 8080:8080 springvendor:latest
```

However, using Docker Compose is simpler as it also launches the required Postgres and Mongo services and provides all necessary environment configuration.

### Running Locally (without Docker Compose)

If you prefer to run the SpringVendor application on your host (e.g., via your IDE or command line) and connect to databases manually, you can do so with a bit of setup:

1. **Set up a PostgreSQL database:** If you have Postgres installed locally, create a database for the app (e.g., `SpringVendor`) and create a user with proper credentials, or reuse an existing one. Alternatively, you can run a PostgreSQL Docker container separately. For example:

   ```bash
   docker run -d --name local-postgres -e POSTGRES_DB=SpringVendor -e POSTGRES_USER=oiler -e POSTGRES_PASSWORD=secret -p 5432:5432 postgres:latest
   ```

   This will launch a Postgres instance on port 5432 with the same credentials used in the SpringVendor configuration.

2. **Set up a MongoDB database:** Ensure a MongoDB instance is running locally. You can install MongoDB or run a Docker container for it:

   ```bash
   docker run -d --name local-mongo -p 27017:27017 mongo:latest
   ```

   (The default MongoDB has no root user and no authentication; if you set one up, adjust the connection URI accordingly.)

3. **Provide configuration to SpringVendor:** The application expects database connection details via environment variables (or a `.env` file). You can create a `.env` file in the project root with the following content (adjust values if needed):

   ```properties
   SPRING_PROFILE=prod
   DDL=update

   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/SpringVendor
   SPRING_DATASOURCE_USERNAME=oiler
   SPRING_DATASOURCE_PASSWORD=secret

   SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/springmongo
   SPRING_DATA_MONGODB_PASSWORD=

   APP_SEED_SUPPLIER=intel
   ```

   This assumes your local Postgres is on localhost:5432 with the database `SpringVendor` and user/password as shown, and that MongoDB is on localhost:27017. You can change the `APP_SEED_SUPPLIER` to seed a different vendor’s data as explained earlier. The `.env` file will be picked up automatically thanks to the `spring-dotenv` dependency, or you can export these variables in your shell.

4. **Run the application:**

    * Using Maven: `./mvnw spring-boot:run` (or use your IDE to run the `VendorApplication.main()` method).
    * Using the packaged jar: after building with `mvn package`, run `java -jar target/vendor-0.0.1-SNAPSHOT.jar`.

   Since we set `SPRING_PROFILE=prod`, the app will listen on port 8080 by default (as configured in `application-prod.properties`). If you prefer to use the dev profile (which runs on port 8081), you can set `SPRING_PROFILE=dev`, but note you still need to supply the database configs via environment because the dev profile does not define any default DB connection (it’s mainly for convenience during development).

5. **Verify it’s working:**
   Check [http://localhost:8080/docs](http://localhost:8080/docs) for the Swagger UI, or call [http://localhost:8080/products](http://localhost:8080/products) to fetch the product list. If the environment variables were set correctly and the databases are running, you should see data being returned. Logs in the console will also indicate if the seeding ran and how many products were saved.

**Tip:** When running locally without Docker, you might need to manually refresh the API key cache if you add new API keys (users) on the fly. You can call the `GET /caches/init` endpoint (which is unprotected) to force the service to reload API keys from the database into its cache. This is usually not necessary unless you manually inserted a new user/API key after startup.

## Deployment (CI/CD Pipeline)

This project is set up for continuous integration and deployment using **GitHub Actions**, targeting deployments on Azure (both Virtual Machines and container-based services). There are two workflow files in the repository under `.github/workflows/`:

* **Pipeline for VM Deployment (`pipeline.yml`):** This workflow runs on each push (or manually via dispatch) and is designed to deploy the application JAR to a VM (for example, an Azure Linux VM):

    * **Build & Test:** It checks out the code, sets up Java 17, and runs the Maven build (`mvn clean package`) including all tests. This ensures that the code compiles and tests pass before deployment.
    * **Artifact Packaging:** After a successful build, it renames the generated jar to a generic name (e.g., `app.jar`) and uploads it as a build artifact.
    * **Deployment to VM:** Using SSH, the pipeline connects to the remote VM and deploys the new jar:

        * It uses secrets for the VM’s host address, SSH username, port, and an SSH private key (`VM_HOST`, `VM_USER`, `VM_SSH_PORT`, `VM_SSH_KEY`). These should be configured in your GitHub repository settings under *Secrets*.
        * The workflow copies the `app.jar` to a specified directory on the VM (e.g., `/home/<user>/vendor/`).
        * It then runs remote shell commands to stop the existing service (assumed to be running as a Systemd service, e.g., `oiler-vendor.service`) and start the updated service. The pipeline calls `sudo systemctl stop oiler-vendor` and `sudo systemctl start oiler-vendor`, then checks the status. This means on the VM, you should have a systemd service set up named "oiler-vendor" that knows how to run the jar (with the appropriate environment variables for DB connections, etc., configured in the service file or environment).
    * **Environment Variables on VM:** The VM approach assumes that the VM itself holds the necessary configuration (likely via environment or an `.env` file loaded by the service). For example, on an Azure VM you might configure the environment in the systemd service unit or use a .env file in the deployment directory. The pipeline does not directly set those environment variables on the VM; it only deploys the new application artifact and restarts the service. Ensure that the VM has all required variables (DB credentials, etc.) set, so that the application can connect to the databases which should also be accessible from the VM (either running on the VM, or the VM has network access to the DB servers or cloud services).

* **Pipeline for Container Deployment (`pipeline-docker.yml`):** This workflow is intended for building and publishing a Docker image of the SpringVendor service, suitable for deployment to container platforms (e.g., Azure Container Instances, Azure App Service, Kubernetes):

    * **Build & Test:** Similar to the VM pipeline, it compiles the application and runs tests first.
    * **Docker Build:** It then builds a Docker image using the provided Dockerfile. The image is tagged (for example as `your-dockerhub-username/vendor:latest`).
    * **Docker Registry Push:** The workflow logs in to Docker Hub (or another container registry) using credentials stored in secrets (`DOCKER_USERNAME` and `DOCKER_ACCESS_TOKEN`) and pushes the newly built image. By default, it’s pushing to Docker Hub under the repository name "vendor".
    * Once the image is in a registry, it can be deployed to an Azure container service:

        * For Azure Container Instances or Azure Web App for Containers, you would configure the service to pull the image from the registry.
        * For Kubernetes (AKS), you would update your deployment manifests to use the new image tag.
        * (The pipeline doesn’t directly deploy to a Kubernetes cluster or Azure service; it stops at publishing the image. Further deployment steps could be added or handled by another process, depending on your infrastructure.)
    * **Environment Variables for Containers:** When deploying the Docker image, remember to provide the required environment variables in the container environment settings. For Azure App Service, for example, you'd set the `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, etc., in the Application Settings. In Kubernetes, these would go into the Deployment’s environment variables. The variables needed are the same as those used in docker-compose (database URLs, credentials, `SPRING_PROFILE`, etc.). It’s critical to provide those so the container knows how to reach the database and so on. Secrets like passwords or API keys should be stored securely (in Azure, use Key Vault or App Settings secrets, in Kubernetes, use Secrets objects) and referenced appropriately.

**Security and Secrets:** All sensitive information (database passwords, SSH keys, API keys, etc.) are kept out of the codebase and are injected via environment variables or GitHub secrets:

* The GitHub Actions workflows use encrypted secrets for credentials (no secrets are stored in the repository).
* The application itself reads configuration from environment variables at runtime. This means you should configure those values in whatever environment you deploy to (be it the VM’s OS environment, a `.env` file not checked into source control, or the configuration section of a cloud service). This approach keeps secrets like database credentials and API keys out of the code and version control, aligning with best practices.

By setting up these pipelines, every code change can be automatically tested and deployed, making the release process consistent and minimizing manual steps. For deploying to Azure, ensure your Azure resources (VM or container service and databases) are set up and accessible. After deployment, you can use the Swagger UI or other monitoring to verify the service is running correctly on Azure.

## Testing

SpringVendor includes a comprehensive test suite covering both the web layer (API endpoints) and the service layer (business logic). To run the tests, you can use Maven:

```bash
./mvnw test
```

This will execute all unit tests and integration tests (if any). The testing approach in this project is as follows:

* **Unit Tests for Services:** The core business logic in service classes (e.g. order placement, product retrieval) is tested with JUnit 5. These tests use **Mockito** to mock repository dependencies, allowing us to simulate various scenarios (like product not found, insufficient stock, concurrent updates) without needing a real database. For example, `OrderServiceTest` verifies that placing an order reduces stock and creates an order, and that it throws a `ResourceNotFoundException` in cases like invalid product ID or not enough stock. It also tests the retry logic by simulating an optimistic locking exception and ensuring the method retries the operation.

* **Web Layer Tests (MockMVC):** We use Spring’s **MockMvc** framework to test the controllers in isolation. With `@WebMvcTest`, the `ProductControllerTest` and `OrderControllerTest` start up a lightweight web application context with just the web layer. The actual service beans are mocked (using `@MockBean` or similar) so we can control their behavior. These tests send HTTP requests to the controller endpoints and assert on the responses (status codes and JSON content). For example, the product API tests confirm that `GET /products` returns an OK status and a JSON array of products, and `GET /products/{id}/details` returns the product info with details. The order API tests ensure that posting an order returns the expected order confirmation and that authentication is enforced (although in the test context we might bypass security or provide a dummy authentication setup).

* **Integration Tests:** Currently, there are no full end-to-end integration tests that start the application with the real databases. (All tests run with in-memory or mocked components for speed and simplicity, meaning you don’t need a running Postgres or Mongo for the test suite.) If needed, you could add integration tests using something like Testcontainers to spin up databases, but that’s beyond the current scope.

After running tests, you should see a summary of tests passed/failed in the console. All tests are designed to pass if the application is working as expected. The CI pipeline also runs these tests on each commit to prevent regressions.

## API Endpoints and Documentation

Once the SpringVendor service is running (locally or on a server), you can interact with its RESTful APIs. Below is a summary of the available endpoints:

* **Product Catalog Endpoints (Public):**

    * `GET /products` – Retrieve the list of all products. Returns a JSON array of product objects. Each product includes basic info such as `id`, `name`, `price`, and `stock`. *(No API key needed)*
    * `GET /products/{id}/details` – Get detailed information for a specific product by its ID. Returns a JSON object containing the product’s basic info (same as above) plus a `details` field with a map of additional specifications (fetched from MongoDB). For example, this might include specs like `"memory": "16GB", "color": "Black"`, etc. *(No API key needed)*

* **Order Management Endpoints (Protected):**

    * `POST /orders` – Place a new order for a product. Expects a JSON body with the order details (productId and quantity, see the `OrderRequest` schema in the API docs). If successful, returns an `OrderResponse` containing the created order’s information (order ID, timestamp, product info, quantity, etc.). This operation will also decrement the stock of the ordered product. **Requires** a valid API key header.
      **Authentication:** Include `X-API-KEY: <your-api-key>` in the request header. If the API key is missing or invalid, the service will respond with an error (HTTP 401 Unauthorized).
    * `GET /orders/{id}` – Retrieve details of an existing order by order ID (UUID). Returns an `OrderResponse` JSON with details such as order id, product, quantity, and order timestamp. **Requires** `X-API-KEY` header (the service will authenticate the request the same way as the POST). The data is fetched from the PostgreSQL database. If an order with the given ID is not found (or the API key is not authorized), an error will be returned (e.g., HTTP 404 for not found, or 401/403 for unauthorized).

* **Authentication/Utility Endpoints:**

    * `GET /caches/init` – This endpoint manually triggers a refresh of the API key cache on the server. It will reload all API keys from the database. In normal operation, the service refreshes the keys every 5 minutes automatically (and also on startup), so you typically don’t need to call this. It’s mostly for administrative or development use. This endpoint does **not** require an API key (it’s unprotected), but it’s not exposed in the documentation UI since it’s meant for internal use.

* **Documentation and Misc:**

    * `GET /docs` – The Swagger/OpenAPI UI, which is a web interface where you can see all the endpoints, their request/response schemas, and even execute test calls directly from the browser. This is a convenient way to explore the API.
    * `GET /docs/json` – The raw OpenAPI specification in JSON format (can be used for generating clients or for importing into tools).
    * **Actuator Endpoints:** If you enabled Spring Boot Actuator in production, endpoints like `/actuator/health` and `/actuator/info` are available. By default (as per the security config), these endpoints are not explicitly set to permitAll, so they are secured behind the API key as well. You can adjust the security configuration to open health checks if needed for your monitoring systems. For instance, to check health you might do:

      ```bash
      curl -H "X-API-KEY: <your-api-key>" http://localhost:8080/actuator/health
      ```

      which should return an "UP" status if everything is running correctly. (If you want `/actuator/health` to be accessible without auth, you’d need to modify the security configuration to permit that path.)

**Obtaining API Keys:** The service uses API keys stored in the `users` table (PostgreSQL) for authentication. Each entry in that table has an `apiKey` field (typically a long random string or UUID) that is considered valid. In a real deployment, you would have a process for creating and distributing these keys to clients (for example, an onboarding process for vendors or a separate UI to generate a key). In the current setup, there isn’t an API endpoint to create a user/key (that could be a future enhancement), so for testing purposes you have a couple of options:

* Use the database directly: Insert a row into the `users` table with a new UUID or token of your choosing. For example:

  ```sql
  INSERT INTO users(api_key) VALUES('my-test-key-12345');
  ```

  Then use `my-test-key-12345` as your `X-API-KEY` in requests. (If the service is already running, call `/caches/init` to immediately load this new key into memory; otherwise it will pick it up on the next scheduled refresh.)
* Alternatively, you can pre-populate a key via a data migration or include it in the seed process (the current CSV seeding does not handle users, but you could extend it).

After you have a valid API key, you can test the protected endpoints (orders) via Swagger UI or any HTTP client by including the header. The Swagger UI provides an authorize button where you can input the API key for convenience.

## Conclusion

SpringVendor provides a robust starting point for a vendor-focused service, demonstrating how to build a Spring Boot application with multiple data stores, secure it with API key authentication, and deploy it in a cloud-friendly way. The project’s README covers all essential information to understand, run, test, and deploy the service. For any further details, refer to the inline code documentation or the Swagger UI for insight into request/response models. Feel free to extend the service by adding new features, such as more endpoints (e.g., updating products, listing orders, etc.), or integrating it with other components of your system. Happy coding!
