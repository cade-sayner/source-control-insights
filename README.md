# Source Control Insights CLI Commands

This CLI provides commands for managing repositories and retrieving source control insights.

## Commands

login  
Logs in the user.

jwt  
Prints the currently logged-in user's JWT.

create_repo <name> <repoUrl>  
Creates a new repository.  
- `<name>`: The name of the repository.  
- `<repoUrl>`: The URL of the repository.

get-repos  
Fetches and displays the user's repositories.

update-repo <repoId> <path>  
Updates the specified repository with the most up-to-date information from the given Git repository path.  
- `<repoId>`: The ID of the repository.  
- `<path>`: The local path to the Git repository.

get-latest-commit <repoId>  
Retrieves the date of the latest commit for a repository.  
- `<repoId>`: The ID of the repository.

## Notes  
- You must be logged in to use commands that interact with repositories.  
- If you are not logged in, you will be prompted to log in before using certain commands.
