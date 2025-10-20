# 1.0.4
* Migrated deployment to _Sonatype Maven Central Portal_ [#155](https://github.com/xdev-software/standard-maven-template/issues/155)
* Updated dependencies

# 1.0.3
* Updated dependencies

# 1.0.2
* Fix ``baseDir`` detection not working in multi module project
* Improved logging
* Updated various dependencies

# 1.0.1
* Updated docs
* Updated various dependencies

# 1.0.0
<i>Initial release</i>
* Forked from [floverfelt/find-and-replace-maven-plugin](https://github.com/floverfelt/find-and-replace-maven-plugin)
* Broken down into multiple goals #6
  * ``directory-name``: Allows replacing directory names
  * ``file-names``: Allows replacing file names 
  * ``file-contents``: Allows replacing file contents
    * New config option: ``replaceLineBased`` -> If set to ``false`` the whole file is replaced, see docs
    * Use ``System.lineSeparator()`` instead of ``\n``
  * ``find-and-replace``
    * Same as in the old plugin
    * Deprecated only exists for compatibility reasons
* Added possibility for empty ``replacementValue`` #3
* Added option to replace multiple lines #2
* Remove not needed dependencies / Fixed maven warnings #1
