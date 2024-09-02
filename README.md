[![Latest version](https://img.shields.io/maven-central/v/software.xdev/find-and-replace-maven-plugin?logo=apache%20maven)](https://mvnrepository.com/artifact/software.xdev/find-and-replace-maven-plugin)
[![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/find-and-replace-maven-plugin/check-build.yml?branch=develop)](https://github.com/xdev-software/find-and-replace-maven-plugin/actions/workflows/check-build.yml?query=branch%3Adevelop)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xdev-software_find-and-replace-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=xdev-software_find-and-replace-maven-plugin)
[![Plugin docs](https://img.shields.io/badge/Plugin-docs-017cee?logo=apachemaven)](https://xdev-software.github.io/find-and-replace-maven-plugin/plugin-info)

# find-and-replace-maven-plugin

A maven plugin for replacing content in files, filenames and directories.

> [!NOTE]
> This is a fork of [floverfelt/find-and-replace-maven-plugin](https://github.com/floverfelt/find-and-replace-maven-plugin) with some [additional functionality](CHANGELOG.md#100).

## Usage
A short usage guide is available [in the plugin docs](https://xdev-software.github.io/find-and-replace-maven-plugin/plugin-info).

Example: Replace underscores with hyphen
```xml
<plugin>
   <groupId>software.xdev</groupId>
   <artifactId>find-and-replace-maven-plugin</artifactId>
   <executions>
      <execution>
         <id>replace-underscore-with-hyphen</id>
         <phase>process-sources</phase>
         <goals>
            <goal>file-contents</goal>
         </goals>
         <configuration>
            <baseDir>testing/</baseDir>
            <fileMask>test.txt</fileMask>
            <findRegex>_</findRegex>
            <replaceValue>-</replaceValue>
         </configuration>
      </execution>
   </executions>
</plugin>
```

<details><summary>Replace contents of auto-generated files (OpenAPI generator)</summary>

```xml
<plugin>
   <groupId>software.xdev</groupId>
   <artifactId>find-and-replace-maven-plugin</artifactId>
   <executions>
      <execution>
         <!-- Remove so that we don't need additional dependency -->
         <id>remove-unused-import-com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider</id>
         <phase>process-sources</phase>
         <goals>
            <goal>file-contents</goal>
         </goals>
         <configuration>
            <baseDir>${generatedDirRelative}/software/xdev/${componentName}/client/</baseDir>
            <fileMask>ApiClient.java</fileMask>
            <!-- @formatter:off DO NOT INTRODUCE LINE BREAK -->
            <findRegex>^import com\.fasterxml\.jackson\.jaxrs\.json\.JacksonJsonProvider;(\r?\n)</findRegex>
            <!-- @formatter:on -->
            <replaceLineBased>false</replaceLineBased>
         </configuration>
      </execution>
      <execution>
         <!-- Changes with each generator version -->
         <id>remove-generated-annotation</id>
         <phase>process-sources</phase>
         <goals>
            <goal>file-contents</goal>
         </goals>
         <configuration>
            <baseDir>${generatedDirRelative}/software/xdev/${componentName}/</baseDir>
            <recursive>true</recursive>
            <fileMask>.java</fileMask>
            <findRegex>^@jakarta\.annotation\.Generated.*(\r?\n)</findRegex>
            <replaceAll>false</replaceAll>
            <replaceLineBased>false</replaceLineBased>
         </configuration>
      </execution>
      <execution>
         <!-- Requiring Java serialization indicates a serious misuse of the API -->
         <id>remove-serialVersionUID</id>
         <phase>process-sources</phase>
         <goals>
            <goal>file-contents</goal>
         </goals>
         <configuration>
            <baseDir>${generatedDirRelative}/software/xdev/${componentName}/</baseDir>
            <recursive>true</recursive>
            <fileMask>.java</fileMask>
            <findRegex>^.*serialVersionUID.*(\r?\n)(\s*\r?\n)?</findRegex>
            <replaceAll>false</replaceAll>
            <replaceLineBased>false</replaceLineBased>
         </configuration>
      </execution>
   </executions>
</plugin>
```

</details>

## Installation
[Installation guide for the latest release](https://github.com/xdev-software/find-and-replace-maven-plugin/releases/latest#Installation)

## Support
If you need support as soon as possible and you can't wait for any pull request, feel free to use [our support](https://xdev.software/en/services/support).

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

## Dependencies and Licenses
View the [license of the current project](LICENSE) or the [summary including all dependencies](https://xdev-software.github.io/find-and-replace-maven-plugin/dependencies)
