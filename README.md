# Source Control Insights CLI

## Commands

* `login`: Logs in a user. Example: `login`
* `jwt`: Prints the logged-in user's JWT. Example: `jwt`
* `create-repo  <name>  <repourl>`: Creates a repository (Must be a project manager, Clive and I have that role in the DB so far). Example: `create-repo my-repo https://github.com/user/repo`
* `get-repos`: Lists repositories. Example: `get-repos`
* `update-repo <repo_id> <path>`: Updates a repository. Example: `update-repo 123 /path/to/repo`
* `get-latest-commit <repo_id>`: Gets latest commit date. Example: `get-latest-commit 123`
