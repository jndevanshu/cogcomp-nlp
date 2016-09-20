#!/bin/bash

#####
# train pos tagger using default paths.
# Due to LBJava quirks when generating code, the simplest
#   way to specify alternative model path is to change 
#   the value(s) in the POSConfigurator class.

DISTDIR="target"
LIBDIR="target/dependency"
MODELDIR="models/edu/illinois/cs/cogcomp/pos/lbjava"


MAIN=

CP=.

if [ ! -d $MODELDIR ]; then
    mkdir -p $MODELDIR 
fi

if [ ! -d $DISTDIR ]; then
    mvn install
fi

if [ ! -e $LIBDIR ]; then
    mvn dependency:copy-dependencies
    rm $LIBDIR/*model*jar
fi



CP="$CP:target/classes/"


#for JAR in `ls $DISTDIR/*jar`; do
#    CP="$CP:$JAR"
#done

for JAR in `ls $LIBDIR/*jar`; do
    CP="$CP:$JAR"
done


MAIN=edu.illinois.cs.cogcomp.pos.POSTagConll

DATADIR=/shared/corpora/ner/wikifier-features/es/ere-NOM-train
OUTDIR=/shared/corpora/ner/wikifier-features/es/ere-NOM-train-pos

CMD="java -Xmx8g -cp $CP $MAIN $DATADIR $OUTDIR"
echo "$0: running command '$CMD'..."

$CMD
    
echo "$0: done."

exit

