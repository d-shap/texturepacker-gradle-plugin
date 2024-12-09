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
Configuration consist of a set of pipelines.
Each pipeline is a configuration applied to a set of source directories.
For example, for one directory a sheet with the size of 512x512 should be created.
For another directory a sheet with the size of 2048x2048 should be created.
```
texturepacker {
    tex512 {
        ...
    }
    tex2048 {
        ...
    }
}
```

For each pipeline the following parameters should be specified:
* `src` - the source folder with images
* `dst` - the destination folder for a sheet and an atlas
* `sheet` - the closure to create a sheet file name
* `data` - the closure to create an atlas file name
* `parameters` - parameters for texture packing

The source directory should contain child directories, each child directory contains a set of images.
For each directory a separate sheet and atlas files are created.
The name of this child directory is passed to closures to generate a sheet file name and an atlas file name.

To filter child directories pipeline can use the following parameters:
* `include`
* `exclude`

## Example
```
texturepacker {
    tex512 {
        src 'build/gen'
        include 'dir1'
        dst 'resources/main/assets/texture'
        sheet { name -> "${name}.png" }
        data { name -> "${name}.json" }
        parameters {
            format('phaser')
            width(512)
            height(512)
        }
    }
    tex1024 {
        src 'build/gen'
        include 'dir2', 'dir3'
        dst 'resources/main/assets/texture'
        sheet { name -> "${name}.png" }
        data { name -> "${name}.json" }
        parameters {
            format('phaser')
            width(1024)
            height(1024)
        }
    }
    tex2048 {
        src 'build/gen'
        exclude 'dir1', 'dir2', 'dir3'
        dst 'resources/main/assets/texture'
        sheet { name -> "${name}.png" }
        data { name -> "${name}.json" }
        parameters {
            format('phaser')
            width(2048)
            height(2048)
        }
    }
}
```
