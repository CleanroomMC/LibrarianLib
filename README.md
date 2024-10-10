# LibrarianLib-Continuous

## [Documentation WIP](https://docs.teamwizardry.com/index.php?title=Main_Page)

LibrarianLib-Continuous is the library mod to end all library mods. 
It doesn't attempt to do much else than take the pain out of modding. 
It can handle a great deal of boilerplate code that you really shouldn't have to touch. Its largest components are 
an automatic serialization system, automatic model creation and registration, a powerful GUI library that takes out 
the magic numbers and complex control stuctures by making everything modular, and a powerful and flexable particle system.

LibrarianLib-Continuous depends on [Forgelin-Continuous](https://github.com/ChAoSUnItY/Forgelin-Continuous). 
Make sure you have it if you want to use it.

## Difference between LibrarianLib and LibrarianLib-Continuous

LibrarianLib is previously developed by TeamWizardry, which depends on outdated Kotlin library bundle provider mod 
[Shadowfacts' Forgelin](https://minecraft.curseforge.com/projects/shadowfacts-forgelin); while LibrarianLib-Continuous is currently maintained by CleanroomMC, which depends on
the successor of Shadowfact's Forgelin named [Forgelin-Continuous](https://github.com/ChAoSUnItY/Forgelin-Continuous).

Please notice that you can replace Shadowfact's Forgelin with Forgelin-Continuous since version 2.0.0.0, meanwhile mods 
originally depends on LibrarianLib should also compatible with LibrarianLib-Continuous and Forgelin-Continuous.

If you have found any mod that is incompatible with either LibrarianLib-Continuous or Forgelin-Continuous, feel free to 
report issues in their GitHub repository!

## Using LibrarianLib-Continuous as a dev

### build.gradle
```groovy
repositories {
    maven {
        name = "CleanroomMC"
        url = uri("https://maven.cleanroommc.com")
    }
}

dependencies {
    compile "com.teamwizardry.librarianlib:LibrarianLib-Continuous-1.12:4.22-2.2"
}
```

### build.gradle.kts
```kt
repositories {
    maven {
        name = "CleanroomMC"
        url = uri("https://maven.cleanroommc.com")
    }
}

dependencies {
    compile("com.teamwizardry.librarianlib:LibrarianLib-Continuous-1.12:4.22-2.2")
}
```
