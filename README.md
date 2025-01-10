## Simple gradle plugin that loads properties file and adds it to project properties

### Usage:
Declare plugin repository in `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        // ...
        maven("https://jitpack.io")
    }
    // ...
}
```

Add plugin to `build.gradle.kts`:
```kotlin
plugins {
    // ...
    id("com.github.BoBkiNN.RepoSecrets") version "1.0.0" // replace 1.0.0 with latest release
}
```

Use extension functions to add your repository:
```kotlin
import xyz.bobkinn.repoSecrets.credRepository
// ...
repositories {
    // ...
    credRepository("REPO_ID", rootProject.properties)
}

```
**Replace `REPO_ID` with your custom repository id.**

### How to use `cred.properties`:
Example:
```properties
REPO_ID_URL = https://user:password@repo.example.com/
```
You can replace `REPO_ID` with any other repository name to reference it.<br>
Also you can use `_LOGIN` and `_PASSWORD` suffixes after repository name to specify credentials

Example use of `_LOGIN` and `_PASSWORD`:
```properties
REPO_ID_URL = https://repo.example.com/
REPO_ID_LOGIN = user
REPO_ID_PASSWORD = password
```

### How it works:
When plugin is applied it loads `cred.properties` file in project directory.<br>
If no file is found then no exception is loaded

### Additional extension methods:
* `Project.loadCredProperties(file: String = DEFAULT_CRED_PROPERTIES)`: loads file, can be called manually if plugin not applied
* `PublishingExtension.printPublishResults(pubName: String)`: prints pretty list of artifacts and repositories (without credentials) where artifacts was uploaded

### TODO:
* Ability to throw exception when no file is found or no repo is found, or it requires auth
* Plugin settings
* Lazy-loaded file to apply settings