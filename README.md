# Documentation

## What is rock.converter?

rock.converter is a project which aims to convert between different 3D formats to rock custom JSON 3D format (although at this moment only OBJ is supported).
[rock.game.js](https://github.com/lajimenez/rock.game.js) has utilities to load 3D models using this custom JSON format.
rock.converter is a java project that generates an executable jar.

## How to convert between formats

You have the next options:

* -f (mandatory): input file format (only OBJ is supported)
* -i (mandatory): input file path
* -o (mandatory): output file path
* -c (optional): you can force rock.converter to compute normals (it will replace existing normals)
* -r (optional): you can say rock.converter to try to repair normals (it will only apply basic checks) 

Example:

```Batchfile
java -jar rock.converter-1.0.jar -f OBJ -i youfile.obj -o yourfile.json -c -r
```

## Output format

rock.converter generates a JSON that contain an array of objects like:

```javascript
[
{
  "mesh": {
    "vertices": [...],
    "normals": [...],
    "textureCoordinates": [...],
    "indexes": [...],
    "BBOX": [...]
  },
  "material": {
    "phongColor": {
      "ambient": [...],
      "diffuse": [...],
      "specular": [...],
      "shininess": ...,
      "alpha": ...
    } ,
    "phongTexture": {
      "ambient": "...",
      "diffuse": "...",
      "specular": "..."
    }
  }
}    
]
``` 

### Textures

rock.game.js is able to load models from this JSON. However, how are textures treated?
If you want to use rock.game.js utilities, you have to assure that there will be a texture image resource identified by the value in the JSON (it's highly recommended to see how test application load resources and models in [rock.game.js](https://github.com/lajimenez/rock.game.js)).

### Other JSON resources

Although this converter only generates one JSON format, rock.game.js is able to load some other JSON formats.
For example, this is a model that uses only a texture when rendering.

```javascript
[
{
  "mesh": {
    "vertices": [...],
    "normals": [...],
    "textureCoordinates": [...],
    "indexes": [...],
    "BBOX": [...]
  },
  "material" : {
    "texture": {
      "image": "..."
    }
  }
}
]
```

If you want to generate this kind of file, you will have to use the converter and once you have the JSON do any changes manually.

## Development

The project has been 'mavenized' so if you are familiar with maven you shouldn't have problems with the project folder structure. If not, you can found maven tutorials anyplace :P
At this moment, you will have to revise the code yourself as there is no technical documentation.

## How to generate jar

Open the terminal and execute:
```Batchfile
mvn clean package
```