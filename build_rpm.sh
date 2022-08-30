set -e

pushd "$(dirname "$0")"

TEMP_PACKAGE_DIR=`pwd -P`"/.package"    #"${TEMP_RPM_DIR}/package"
RANGER_JSG_SERVICE_RPM_BASE_PATH="/opt/guavus/ranger-janusgraph-service/"
RANGER_JSG_SERVICED_DIST_DIR_PLUGIN="./dist/ranger-janusgraph-service"
RANGER_JSG_SERVICE_RPM_NAME="guavus-ranger-janusgraph-plugin"

VERSION=$1
REL=$2

if [ -z "$BUILD_NUMBER" ]
then
      echo "BUILD_NUMBER is not set. setting it 0"
      BUILD_NUMBER=0
fi

 echo "###### START: RPM CREATION FOR RANGER JSG PLUGIN ######"
 echo -e "# # # # # # # START : Creating RPM package for ranger plugin # # # # # # #"
 #cleanup
 rm -rf ${TEMP_PACKAGE_DIR}/*
 mkdir -p ${TEMP_PACKAGE_DIR}/${RANGER_JSG_SERVICE_RPM_BASE_PATH}
 mkdir -p ${TEMP_PACKAGE_DIR}/${RANGER_JSG_SERVICE_RPM_BASE_PATH}/lib

 cp -r ./ranger-janusgraph-service/conf ${TEMP_PACKAGE_DIR}/${RANGER_JSG_SERVICE_RPM_BASE_PATH}
 cp -r ./ranger-janusgraph-service/target/*.jar ${TEMP_PACKAGE_DIR}/${RANGER_JSG_SERVICE_RPM_BASE_PATH}/lib

 fpm -f -s dir -t rpm --rpm-os linux -v ${VERSION} --iteration ${REL} --chdir $TEMP_PACKAGE_DIR -p $RANGER_JSG_SERVICED_DIST_DIR_PLUGIN -n $RANGER_JSG_SERVICE_RPM_NAME .

 echo "###### END: RPM CREATION FOR RANGER JSG PLUGIN ######"

 rm -rf ${TEMP_PACKAGE_DIR}

popd > /dev/null
