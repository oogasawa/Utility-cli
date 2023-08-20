#!/bin/bash

mvn javadoc:javadoc
rm -Rf ~/public_html/javadoc/Utility-cli
mv target/site ~/public_html/javadoc/Utility-cli
