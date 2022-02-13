rootProject.name = "cross-wars"

plugins {
    id("de.fayard.refreshVersions") version "0.40.1"
////                            # available:"0.40.0"
////                            # available:"0.40.1"
}

include("common")
include("jsReact")
include("jsCompose")
include("serverKtor")
