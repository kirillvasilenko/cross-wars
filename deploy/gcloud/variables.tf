variable "server_port" {
  description = "The port the server will use for HTTP requests"
  type        = number
  default     = 8080
}

variable "gce_ssh_user" {
  type = string
  default = "kir"
}

variable "gce_ssh_pub_key_file" {
  type = string
  default = "~/.ssh/id_rsa.pub"
}
