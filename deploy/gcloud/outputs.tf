output "vm_instance_external_ip" {
  value       = google_compute_instance.cross-wars_app_server.network_interface.0.access_config.0.nat_ip
  description = "Public ip of the vm instance"
}

