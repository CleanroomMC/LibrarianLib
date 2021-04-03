plugins {
    `minecraft-conventions`
    `kotlin-conventions`
}

apply<LibLibModulePlugin>()

dependencies {
    api(project(":core"))
    api(project(":mirage"))
    api(project(":prism"))
    api("org.junit.jupiter:junit-jupiter-api:5.6.2")
    api("org.junit.jupiter:junit-jupiter-engine:5.6.2")
    api("org.junit.platform:junit-platform-launcher:1.6.2")
//    devClasspath("org.junit.jupiter:junit-jupiter-api:5.6.2")
//    devClasspath("org.junit.jupiter:junit-jupiter-engine:5.6.2")
//    devClasspath("org.junit.platform:junit-platform-launcher:1.6.2")
}
