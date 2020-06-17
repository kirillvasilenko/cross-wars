provider "google" {
  project = "otus-cloud-2019-09-274812"
  region  = "europe-north1"
  zone    = "europe-north1-a"
}

terraform {
  backend "gcs" {
    bucket  = "state-bucket"
    prefix  = "cross-wars"
  }
}

resource "google_compute_instance" "cross-wars_app_server" {
  name         = "cross-wars-app-server"
  machine_type = "n1-standard-1"

  tags = ["cross-wars-app-server"]
  
  metadata = {
    ssh-keys = "${var.gce_ssh_user}:${file(var.gce_ssh_pub_key_file)}"
  }
  
  boot_disk {
    initialize_params {
      image = "ubuntu-1804-bionic-v20200610"
    }
  }
  
  network_interface {
    # A default network is created for all GCP projects
    network       = "default"
    access_config {
    }
  }
}

resource "google_compute_firewall" "cross_wars_allow_all_needed_ports" {
  name    = "cross-wars-allow-all-needed-ports"
  network = "default"

  allow {
    protocol = "tcp"
    ports    = [var.server_port]
  }
  
  target_tags = ["cross-wars-app-server"]
}