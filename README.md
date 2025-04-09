# Auto-DL Backend

Backend service for the Auto-DL application.

## Deployment

This application can be deployed to the Azure server in two ways:

1. **Automatic Main Branch Deployment**: When changes are pushed to the main branch, the application is automatically deployed to the production environment
2. **Manual PR Deployment**: Any pull request can be manually deployed to temporarily replace the main deployment

### Main Branch Deployment

When changes are pushed to the main branch, the application is automatically deployed to the production environment on port 50021.

This is the default deployment method and ensures that the main branch is always deployed to production when updated.

### Manual PR Deployment

You can manually deploy any pull request to the production environment using the GitHub Actions workflow. This is useful for testing changes in the production environment before merging them to the main branch.

To manually deploy a pull request:

1. Go to the GitHub repository
2. Click on the "Actions" tab
3. Select the "Deploy PR to Azure" workflow
4. Click on "Run workflow"
5. Enter the PR number you want to deploy
6. Click "Run workflow"

The selected pull request will be deployed to the production environment, temporarily replacing the current deployment. It will use the same port (50021) and directory as the main branch deployment.

**Note**: When a new commit is pushed to the main branch, it will automatically override any manual PR deployment and restore the main branch to production.

Deployment triggered on: $(date)
