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

### Docker Configuration

The application is deployed using Docker Compose with the following optimizations:

#### Space Efficiency
- Uses Alpine-based images which are much smaller
- Multi-stage build that only copies necessary files to the production image
- Automatic cleanup of unused Docker resources during deployment
- Resource limits to prevent container from using excessive memory
- Log rotation to prevent log files from growing too large

#### Network Configuration
The application connects to an external Docker network named `autodl-network`. This allows the application to communicate with other services on the same network.

The network is automatically created during deployment if it doesn't already exist.

#### Security
- Runs as a non-root user inside the container
- Uses healthchecks to ensure the application is running correctly

Deployment triggered on: $(date)
