# TexturePacker Gradle Plugin
TexturePacker Gradle Plugin is a plugin to call TexturePacker CLI.

## Prerequisites
TexturePacker should be installed and `TexturePacker` command should be available:
```
TexturePacker --help
```

## Configuration
Apply an appropriate version of a plugin:
```
plugins {
    id 'ru.d-shap.texturepacker' version '1.1'
}
```

Configure the plugin with a `texturepacker` block.
Next parameters should be specified:
* `src` - the source folder with images
* `dst` - the destination folder for a sheet and an atlas
* `sheet` - the closure to create a sheet file name
* `data` - the closure to create a data file name
* `parameters` - parameters for texture packing

The source directory should contain child directories, each child directory contains a set of images.
For each directory a separate sheet and data files are created.
The name of this child directory is passed to closures to generate a sheet file name and a data file name.

## Example
```
texturepacker {
    src 'build/gen'
    dst 'resources/main/assets/texture'
    sheet { name -> "${name}.png" }
    data { name -> "${name}.json" }
    parameters {
        format('phaser')
        width(1024)
        height(1024)
        padding(1)
        disable_rotation()
        trim_mode('None')
        trim_sprite_names()
    }
}
```

# Donation
If you find my code useful, you can [bye me a coffee](https://www.paypal.me/dshapovalov)
