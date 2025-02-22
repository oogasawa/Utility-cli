# Utility-cli

## Overview.

Like docker or maven, the program name + command name + command line options allow a number of functions to be performed. 
The `DefaultParser` of the Apache Commons CLI is used to parse command line options.

### Program invocation

A program written using `Utility-cli` can be called as follows

``` sh
java -jar your_program.jar command options
```

If the program is executed without a command name, a list of commands will be output. 
(Note that in this case, `Utility-cli` itself is both a library and an example program.)

``` sh
$ java -jar target/Utility-cli-2.1.0-fat.jar 

## Usage

java -jar Utility-cli-VERSION-fat.jar <command> <options>

## Commands

difference   Calculate the difference between two sets.
filter       Choose columns from each line (tab delimited).
getColumns   Choose columns from each line (tab delimited).
split        Split each line into fields.
```

### Output of command usage

Running the command with the command name will display help for the command.

``` sh
$ java -jar target/Utility-cli-2.1.0-fat.jar difference
Parsing failed. Reason: Missing required options: f1, f2

Usage: difference
 -f1,--file1 <file1> f1 of the set difference f1 - f2
 -f2,--file2 <file2> f2 of the set difference f1 - f2
```

### Program invocation using jbang

Program calls can be further shortened by using [jbang]().

``` sh
$ cli.java 
[jbang] [WARN] Detected missing or out-of-date dependencies in cache.
[jbang] Resolving dependencies...
[jbang] com.github.oogasawa:Utility-cli:2.1.0
[jbang] Dependencies resolved

## Usage

java -jar Utility-cli-VERSION-fat.jar <command> <options> ## Commands

## Commands

difference   Calculate the difference between two sets.
filter       Choose columns from each line (tab delimited).
getColumns   Choose columns from each line (tab delimited).
split        Split each line into fields.
```


Similarly, if you specify the command name and run the command, help for the command will be displayed.

```
$ cli.java difference
Reason: Missing required options: f1, f2

usage: difference
 -f1,--file1 <file1> f1 of the set difference f1 - f2
 -f2,--file2 <file2> f2 of the set difference f1 - f2
```

The contents of cli.java is as follows.

``` sh
//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.github.oogasawa:Utility-cli:2.1.0

class cli {
  public static void main(String[] args) throws Exception {
    com.github.oogasawa.utility.cli.App.main(args);
  }
}
```

## How to install

Since it is not registered in the Maven repository, install it under `.m2` as follows.

### Operating environment

- JDK21 or later
- Apache Commons CLI

### How to build and install

Since we are using the Maven wrapper, there is no need to install maven.

``` sh
git clone https://github.com/oogasawa/Utilit-cli
cd Utility-cli
. /mvnw clean install
```

## How to add to `pom.xml`

``` xml
<dependency>
    <groupId>com.github.oogasawa</groupId>
    <artifactId>Utility-cli</artifactId>
    <version>2.1.0</version>
</dependency> <dependency> <artifactId>Utility-cli</artifactId
```

## How to use

`App.java` of `Utility-cli` itself is an example of usage.

- https://github.com/oogasawa/Utility-cli/blob/main/src/main/java/com/github/oogasawa/utility/cli/App.java


---

## 概要

dockerやmavenのように、プログラム名 + コマンド名 + コマンドラインオプションで、多数の機能を実行できるようにする。
コマンドラインオプションのパースには[Apache Commons CLI](https://commons.apache.org/proper/commons-cli/index.html)のDefaultParserを使っている。


`Utility-cli`を使って書かれたプログラムは以下のように呼ぶことができる。

```
java -jar your_program.jar command options
```

コマンド名なしで実行するとコマンドのリストが出力される。
（この場合`Utility-cli`自身がライブラリであると同時にプログラムの例になっていることに注意。）

```
$ java -jar target/Utility-cli-2.1.0-fat.jar 

## Usage

java -jar Utility-cli-VERSION-fat.jar <command> <options>

## Commands

difference      Calculate the difference between two sets.
filter          Choose columns from each line (tab delimited).
getColumns      Choose columns from each line (tab delimited).
split           Split each line into fields.
```

コマンド名を指定して実行すると、コマンドのヘルプが表示される。

```
$ java -jar target/Utility-cli-2.1.0-fat.jar difference
Parsing failed.  Reason: Missing required options: f1, f2

usage: difference
 -f1,--file1 <file1>   f1 of the set difference f1 - f2
 -f2,--file2 <file2>   f2 of the set difference f1 - f2

```

プログラム呼び出しは[jbang](https://www.jbang.dev/)を使うと更に短縮できる。

```
$ cli.java 
[jbang] [WARN] Detected missing or out-of-date dependencies in cache.
[jbang] Resolving dependencies...
[jbang]    com.github.oogasawa:Utility-cli:2.1.0
[jbang] Dependencies resolved

## Usage

java -jar Utility-cli-VERSION-fat.jar <command> <options>

## Commands

difference      Calculate the difference between two sets.
filter          Choose columns from each line (tab delimited).
getColumns      Choose columns from each line (tab delimited).
split           Split each line into fields.
```

同様にコマンド名を指定して実行すると、コマンドのヘルプが表示される。

```
$ cli.java difference
Parsing failed.  Reason: Missing required options: f1, f2

usage: difference
 -f1,--file1 <file1>   f1 of the set difference f1 - f2
 -f2,--file2 <file2>   f2 of the set difference f1 - f2
```


ここで`cli.java`の内容は以下の通り。

```
//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.github.oogasawa:Utility-cli:2.1.0

class cli {
  public static void main(String[] args) throws Exception {
    com.github.oogasawa.utility.cli.App.main(args);
  }
}
```


## インストール方法

Maven repositoryには登録されていないので、以下のようにして`.m2`以下にインストールする。

### 動作環境

- JDK21以降
- Apache Commons CLI

### ビルドおよびインストールの方法

Maven wrapperを使っているのでmavenのインストールは不要。

```
git clone https://github.com/oogasawa/Utilit-cli
cd Utility-cli
./mvnw clean install
```

### `pom.xml`への追加方法

```
<dependency>
    <groupId>com.github.oogasawa</groupId>
    <artifactId>Utility-cli</artifactId>
    <version>2.1.0</version>
</dependency>
```

## 使用方法

Utility-cliのApp.java自体が使用方法の例になっている。

- https://github.com/oogasawa/Utility-cli/blob/main/src/main/java/com/github/oogasawa/utility/cli/App.java

