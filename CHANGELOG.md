## 1.0.0
<i>Initial release</i>
* Forked from [floverfelt/find-and-replace-maven-plugin](https://github.com/floverfelt/find-and-replace-maven-plugin)
* Broken down into multiple goals
  * ``directory-name``: Allows replacing directory names
  * ``file-names``: Allows replacing file names 
  * ``file-contents``: Allows replacing file contents
    * New config option: ``replaceLineBased`` -> If set to ``false`` the whole file is replaced, see docs
    * Use ``System.lineSeparator()`` instead of ``\n``
  * ``find-and-replace``
    * Same as in the old plugin
    * Deprecated only exists for compatibility reasons
* Added possibility for empty ``replacementValue``
