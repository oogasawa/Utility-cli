

function install() {
  git clone https://github.com/oogasawa/$1
  cd $1
  git checkout tags/$2
  bash install_dependencies.sh
  mvn clean install
  cd ..
  rm -Rf $1
}


install Utility-types v0.12.0

