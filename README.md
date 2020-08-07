# How it work:

## 1. Load jar file e.g. --i game.jar

## 2. File jar exploration: (packages: --list-packages, classes: --list-classes, methods: --list-methods [full-class-name], fields: --list-fields [full-class-name], constructors: --list-ctors [full-class-name])

## 3. File modification by script: --script [example-modify-game.rts]:
        Adding/Deleting package: add-package [full-name] remove-package [full-name]
        Adding/Deleting class/interface: add-class [full-name] add-interface [full-name] remove-class [full-name] remove-interface [full-name]
        Adding/Deleting method: add-method [full-name] remove-method [full-name]
        Override method body: set-method-body [full-name path-to-src-file]
        Addtion for the beginning/ending of the method: add-before-method [full-name path-to-src-file] add-after-method [full-name path-to-src-file]
        Adding/Deleting field: add-field [full-class-name field-name]
        Adding/Deleting constructor: add-ctors [full-name] delete-ctors [full-name]
        Override constructor body: set-ctor-body [full-name path-to-src-file]
        saving the modified jar file: --o modified-game.jar
