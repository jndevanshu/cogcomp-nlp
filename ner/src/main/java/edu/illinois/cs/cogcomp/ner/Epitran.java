package edu.illinois.cs.cogcomp.ner;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Epitran {

    public static HashMap<String, String> epitranMapping = new HashMap<String, String>();
    
    public static final Logger logger = Logger.getLogger(Epitran.class);

    public static void loadEpitrainMapping(String path) throws IOException {
        List<String> lineList = FileUtils.readLines(new File(path));
        for (String line : lineList) {
            if (line.split("\\s+").length != 2) {
                continue;
            }
            String src = line.split("\\s+")[0].trim();
            String tgt = line.split("\\s+")[1].trim();
            epitranMapping.put(src.toLowerCase(), tgt.toLowerCase());
        }
    }

    public static String getIPARepresentation(String word) {
        if (epitranMapping.containsKey(word.toLowerCase())) {
            return epitranMapping.get(word.toLowerCase());
        } else {
            logger.debug("No IPA mapping for " + word + " in Epitran file");
            return "";
        }
    }

}
