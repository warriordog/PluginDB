PluginDB
An SQL library for Bukkit plugins
---
PluginDB is an SQL helper library for bukkit plugins.  It can automatically access or create and initialize a database
based on a structure defined in the plugin jar.  It also allows asynchronous queries, so database lookups do not slow down
the main thread.  A WIP goal is to implement a high-level language for defining queries in Java so that they can mesh
more cleanly into a java codebase.
