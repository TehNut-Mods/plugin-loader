# plugin-loader

plugin-loader is a simple system mods can use to detect and load plugin implementations from within other mod jars.

## Creating a loader

Loaders are a soft-dependency. If plugin-loader is not installed, the mod should still load fine, it just won't load plugins.

### Loader Builder Detection

Create a `pluginloader.json` file inside the root of your resources directory with the following contents:

```json
{
  "id": "modid",
  "initializer": "com.website.foo.MyLoaderCreator"
}
```

* `id` is your modid.
* `initializer` points to a class that implements `LoaderCreator`.

plugin-loader looks for this JSON file and uses it to discover if you have a loader, and where it is.

### Loader Creation

Create a class at the location you pointed at in your JSON and implement `LoaderCreator`. Use the `PluginLoaderBuilder`
to create a new `PluginLoader` instance. An extreme barebones example would look like this:

```java
public class MyLoader implements LoaderCreator {
    
    public void createLoaders() {
        new PluginLoaderBuilder("modid").build();
    }
}
```

To have more control over the behavior, you can use some of the other methods:

```java
public class MyLoader implements LoaderCreator {
    
    public void createLoaders() {
        new PluginLoaderBuilder("some_id")
                .withValidator(ValidationStrategy.instanceOf(Runnable.class))
                .withInitializer((aClass, container) -> {
                    try {
                        aClass.newInstance();
                    } catch (IllegalAccessException | InstantiationException e) {
                        e.printStackTrace();
                    }
                })
                .build();
    }
}
```

See the javadoc for more information.

## Creating a Plugin

Plugin implementation is very simple. Create a `plugins` directory in the root of your resources directory. In there, create
a JSON file named `modid.plugin.json`, where `modid` is the ID of the mod you want to be loaded by. The JSON file should 
be structured like so:

```json
{
  "id": "my_plugin",
  "initializer": "com.website.foo.MyPlugin",
  "data": {
    "foo": "bar"
  }
}
```

* `id` is the unique ID of your plugin.
* `initializer` points to a your plugin class.
* `data` is an optional field that depends on the mod's implementation. It is passed as a raw `JsonObject` to the loader.

After this, everything is left up to the loader, so see their docs to know what other requirements are needed.