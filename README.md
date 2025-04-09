# Auto-DL Backend

Backend service for the Auto-DL application.

## Deployment

This application is automatically deployed to the Azure server when changes are pushed to the main branch.

### Deployment Process

When changes are pushed to the main branch, the application is automatically deployed to the production environment on port 50021.

The deployment process:
1. Builds the application using Maven
2. Creates a deployment package
3. Transfers the package to the Azure server
4. Extracts the package to the deployment directory
5. Configures the application with the necessary environment variables
6. Starts the application using Docker Compose

### Network Configuration

The application is deployed using Docker Compose and connects to an external Docker network named `autodl-network`. This allows the application to communicate with other services on the same network.

The network is automatically created during deployment if it doesn't already exist.

Deployment triggered on: $(date)
