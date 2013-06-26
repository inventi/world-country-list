# List of world countries
This code prases country list data from
http://publications.europa.eu/code/en/en-5000500.htm and puts them to
country record map for later use.
For every country there are following records stored:
* short-name
* full-name
* country-code
* capital
* citizenship
* adjective
* currency
* currency-code
* subunit

Its very convinient if you need country or currency list for your app.
Common use case is to take this script, change/manipulate the data and output it into some file.

Tested with LT and EN languages.

## Usage
Install leiningen and type "lein test" to run tests to see whether it works.

## License
Copyright (C) 2012 Inventi

Distributed under the Eclipse Public License, the same as Clojure.
