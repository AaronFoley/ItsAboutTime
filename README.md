It's About Time
===============

This plugin allows you to create a lore time system that actually works in game. This plugin can expand the length of days and nights, to suit your Lore Calendar.
It also adds an action to the watch to display the current time and date.

As this plugin allows you to configure the interval to a custom one, reducing the number time between updates of the time.
It also allows you to configure a cache so that people are not able to spam the watch to re-calculate the time, instead they are given a cached version until it expires.

The default config that comes with this file was made using the Lore Calendar from [Gazamo's A'therys Ascended RPG server](http://atherys.com)

I also intended to add support for a real moon cycle, but I did not have time to implement this, and could not get the moon to be set correctly each night.

Compilation
-----------

We use maven to handle our dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Check out this repo and: `mvn clean install`