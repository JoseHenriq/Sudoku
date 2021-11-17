Java Random.Org API
Author: Pavel Pscheidl
Version: 1.5
Released: 6.6.2012
Contact: Bugs etc. pls mail to pavel.pscheidl[at]gmail.com

Copyright (C) 2012 Pavel Pscheidl

This program is distributed under GNU LESSER GENERAL PUBLIC LICENSE Version 3, 29 June 2007

Changes in 1.5 version
- Stable version
- Added RANDOM STRING GENERATOR methods
- Timeout settings now applies to all methods correctly

Changes in 1.4 version
- Optimized getSingleRandomInt() method -> these methods provide fast way to get single random number.
- Both methods getSingleRandomInt and getRandomInt() now provide alternatives for numbers in different BASE.
- Added constants to provide background for operations in different BASE.
- Javadoc included

Changes in 1.3 version
- Added methods for quota handling
- Added methods for getting sequences

Changes in 1.2 version
- Methods are now accessed in non-static way
- Implemented methods/constructors to work with timeout.
- Default timeout set to 10 seconds.

Changes in 1.1 version:

- Added methods accepting arguments with same logic as java.util.Random class;
- Methods accepting arguments the java.util.Random way now accept negative limit value argument and can return negative values.
- Memory usage tweaks.
- IllegalArgumentException exceptions now describe thrown exceptions much better.