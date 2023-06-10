rootProject.name = "PwingRaces-Parent"

include(":core")
include(":paper")
include(":PwingRaces-API")

project(":core").projectDir = file("PwingRaces/core")
project(":paper").projectDir = file("PwingRaces/paper")
