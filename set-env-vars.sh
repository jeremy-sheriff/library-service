#!/bin/zsh

# Function to delete an environment variable from ~/.zshrc
delete_env_var() {
    local key="$1"

    if grep -q "^export $key=" ~/.zshrc; then
        # If the variable exists, delete it
        sed -i '' "/^export $key=/d" ~/.zshrc
        echo "Deleted $key from .zshrc"
    else
        echo "$key not found in .zshrc"
    fi
}

# Function to add or update an environment variable in ~/.zshrc
add_env_var() {
    local key="$1"
    local value="$2"

    if grep -q "^export $key=" ~/.zshrc; then
        # If the variable exists, update it
        sed -i '' "s|^export $key=.*|export $key=\"$value\"|" ~/.zshrc
        echo "Updated $key in .zshrc"
    else
        # If the variable doesn't exist, add it
        echo "export $key=\"$value\"" >> ~/.zshrc
        echo "Added $key to .zshrc"
    fi
}

# List of environment variables and their values
env_vars=(
    "KEY_CLOAK_CLIENT_ID=students-service"
    "KEY_CLOAK_USERNAME=app-user"
    "KEY_CLOAK_PASSWORD=gitpass2016"
    "KEY_CLOAK_TOKEN_ENDPOINT=http://localhost:8080/keycloak/auth/realms/school/protocol/openid-connect/token"
    "KEY_CLOAK_ISSUER_URI=http://localhost:8080/keycloak/auth/realms/school"
    "KEY_CLOAK_DB_PASSWORD=gitpass2016"
    "KEY_CLOAK_DB_USERNAME=postgres"
    "KEY_CLOAK_USERS_DB=users_db"
    "KEY_CLOAK_CORS_ALLOWED_ORIGINS=http://localhost:3000"
    "KEY_CLOAK_COURSE_URL=http://localhost:8083"
    "DB_USER=postgres"
    "DB_USERNAME=postgres"
    "DB_PASSWORD=gitpass2016"
    "DB_URL=jdbc:postgresql://localhost:5432/users_db"
    "CORS_ALLOWED_ORIGINS=http//localhost:3000"
    # Add any other environment variables you want to manage here
)

# Delete all specified environment variables from ~/.zshrc
echo "Deleting environment variables..."
for var in "${env_vars[@]}"; do
    key="${var%%=*}"
    delete_env_var "$key"
done

# Add or update all specified environment variables in ~/.zshrc
echo "Adding/updating environment variables..."
for var in "${env_vars[@]}"; do
    key="${var%%=*}"
    value="${var#*=}"
    add_env_var "$key" "$value"
done

# Source the .zshrc file to apply the changes
source ~/.zshrc

echo "All specified environment variables have been deleted, updated, or added to .zshrc and changes applied."
