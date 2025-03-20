terraform {
  backend "s3" {
    bucket = "version-control-tool-bucket"
    key    = "terraform.tfstate"
    region = "eu-west-1"
  }
}