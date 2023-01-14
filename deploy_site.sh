#!/bin/bash

mvn site:site javadoc:javadoc
mv target/site ~/public_html/javadoc/Utility-cli
