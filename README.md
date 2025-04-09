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

You can manually deploy any pull request to the production environment. This is useful for testing changes in the production environment before merging them to the main branch.

#### Option 1: Deploy directly from the PR

Simply add a comment to the PR with the command:

```
/deploy
```

This will trigger the deployment workflow automatically. The workflow will add a comment to the PR with a link to the running workflow.

**Note**: Only repository collaborators or the PR author can trigger deployments using this method.

#### Option 2: Deploy from the Actions tab

Alternatively, you can trigger the deployment from the Actions tab:

1. Go to the GitHub repository
2. Click on the "Actions" tab
3. Select the "Deploy PR to Azure" workflow
4. Click on "Run workflow"
5. Enter the PR number you want to deploy
6. Click "Run workflow"

The selected pull request will be deployed to the production environment, temporarily replacing the current deployment. It will use the same port (50021) and directory as the main branch deployment.

**Note**: When a new commit is pushed to the main branch, it will automatically override any manual PR deployment and restore the main branch to production.

Deployment triggered on: $(date)
