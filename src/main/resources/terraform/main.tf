// Run:
//    terraform plan
//    terraform apply

provider "heroku" {
  version = "~> 2.5"
  email = "callumnewlands@goglemail.com"
  api_key = var.heroku_api_key
}

resource "heroku_app" "website-updater" {
  name = "website-updater"
  region = "eu"
  stack = "heroku-18"

  config_vars = {
    MAVEN_CUSTOM_OPTS = "-P prod -DskipTests"
  }

  sensitive_config_vars = {
    DATABASE_URL = var.heroku_database_url
    DB_SECRET = var.database_secret
  }

}