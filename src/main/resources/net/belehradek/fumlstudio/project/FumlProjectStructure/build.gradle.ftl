buildscript {
    repositories {
        maven {
            url uri('../repo') //loklani repozitar
            //url "https://plugins.gradle.org/m2/" //vzdaleny repozitar
        }
    }
    dependencies {
        classpath group: 'net.belehradek.fuml.gradle', name: 'fUmlPlugin', version: '0.1'
    }
}
apply plugin: 'fUmlPlugin'
