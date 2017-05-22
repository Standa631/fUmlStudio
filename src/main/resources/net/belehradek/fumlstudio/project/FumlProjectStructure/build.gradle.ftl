apply plugin: 'java'
apply plugin: 'FumlPlugin'

buildscript {
    repositories {
        maven {
            url uri('../repo') //loklani repozitar
            //url "https://plugins.gradle.org/m2/" //vzdaleny repozitar
        }
    }
    dependencies {
        classpath group: 'net.belehradek.fuml.gradle', name: 'FumlPlugin', version: '0.1'
    }
}

fUmlSettings {
	libPath = gradle.ext.FumlSettingsLibPath
	toolsPath = gradle.ext.FumlSettingsToolsPath
	unitName = gradle.ext.FumlSettingsUnitName
	transFile = gradle.ext.FumlSettingsTransformationFile
	namespacePrefix = gradle.ext.FumlSettingsNamespacePrefix
}