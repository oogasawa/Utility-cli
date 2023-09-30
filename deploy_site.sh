#!/bin/bash

LANG=en_US.UTF8 mvn javadoc:javadoc
rm -Rf ~/public_html/javadoc/Utility-cli
mv target/site ~/public_html/javadoc/Utility-cli
