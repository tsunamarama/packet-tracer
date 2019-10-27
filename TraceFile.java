import java.io.*;
import java.io.File;
import java.lang.Integer;
import java.util.*;

/**
 * @author Michael Johnson
 */
public class TraceFile {
    private ArrayList<traceFileRecord> traceFile = new ArrayList<traceFileRecord>();
    private ArrayList<String> sourceHostList = new ArrayList<String>();
    private ArrayList<String> destHostList = new ArrayList<String>();
    public HashMap<String, LinkedHashMap<Integer, Integer>> hostPackets = new HashMap<String, LinkedHashMap<Integer, Integer>>();

    /**
     * Constructs a <code>TraceFile</code> object from a given <code>File</code>.
     * @param newFile the file to read from
     */
    public TraceFile(File newFile) {
        readFile(newFile);
    }

    private void readFile(File newFile) {
        traceFileRecord record;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(newFile));
            String currLine;
            while ((currLine = reader.readLine()) != null) {
                LinkedHashMap<Integer, Integer> sourceGraphData = new LinkedHashMap<Integer, Integer>();
                LinkedHashMap<Integer, Integer> destGraphData = new LinkedHashMap<Integer, Integer>();
                record = parseLine(currLine);
                if (!record.sourceAddr.matches("0")) {
                    traceFile.add(record);
                    buildGraphData(record, sourceGraphData, destGraphData);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private traceFileRecord parseLine(String currLine) {
        currLine = currLine.replaceAll("\t{2}", "\t0\t");
        String[] currLineArray = currLine.split("\t");
        Double timeStampOrig = Double.parseDouble(currLineArray[1]);
        Integer timeStamp = timeStampOrig.intValue();
        String sourceAddr = currLineArray[2];
        String destAddr = currLineArray[4];
        Integer pktSize = Integer.parseInt(currLineArray[7]);
        return new traceFileRecord(timeStamp, sourceAddr, destAddr, pktSize);
    }

    private void buildGraphData(traceFileRecord record, LinkedHashMap<Integer, Integer> sourceGraphData, LinkedHashMap<Integer, Integer> destGraphData) {
        if (!sourceHostList.contains(record.sourceAddr)) {
            sourceHostList.add(record.sourceAddr);
        }
        if (!destHostList.contains(record.destAddr)) {
            destHostList.add(record.destAddr);
        }
        if (!hostPackets.containsKey(record.destAddr)) {
            destGraphData.put(record.timeStamp, record.pktSize);
            hostPackets.put(record.destAddr, destGraphData);
        } else {
            destGraphData = hostPackets.get(record.destAddr);
            if (destGraphData.containsKey(record.timeStamp)) {
                Integer mergePackets = destGraphData.get(record.timeStamp);
                mergePackets += record.pktSize;
                destGraphData.put(record.timeStamp, mergePackets);
                hostPackets.put(record.destAddr, destGraphData);
            } else {
                destGraphData.put(record.timeStamp, record.pktSize);
                hostPackets.put(record.destAddr, destGraphData);
            }
        }
        if (!hostPackets.containsKey(record.sourceAddr)) {
            sourceGraphData.put(record.timeStamp, record.pktSize);
            hostPackets.put(record.sourceAddr, sourceGraphData);
        } else {
            sourceGraphData = hostPackets.get(record.sourceAddr);
            if (sourceGraphData.containsKey(record.timeStamp)) {
                Integer mergePackets = sourceGraphData.get(record.timeStamp);
                mergePackets += record.pktSize;
                sourceGraphData.put(record.timeStamp, mergePackets);
                hostPackets.put(record.sourceAddr, sourceGraphData);
            } else {
                sourceGraphData.put(record.timeStamp, record.pktSize);
                hostPackets.put(record.sourceAddr, sourceGraphData);
            }
        }
    }

    /**
     * Returns a sorted list of source host IP addresses intended for use in creating a <code>DefaultComboBoxModel</code> object.
     * @return the sorted list of source host IP addresses
     */
    public ArrayList<String> getSourceHostList() {
        return sourceHostList;
    }

    /**
     * Returns a sorted list of destination host IP addresses intended for use in creating a <code>DefaultComboBoxModel</code> object.
     * @return the sorted list of destination host IP addresses
     */
    public ArrayList<String> getDestHostList() {
        return destHostList;
    }

    private class traceFileRecord {
        private Integer timeStamp;
        private String sourceAddr;
        private String destAddr;
        private Integer pktSize;

        /**
         * Constructs a <code>traceFileRecord</code> object for the given parameters.
         * @param timeStamp     an <code>Integer</code> time stamp
         * @param sourceAddr    a <code>String</code> source host IP address
         * @param destAddr      a <code>String</code> destination host IP address
         * @param pktSize       an <code>Integer</code> packet size in bytes
         */
        private traceFileRecord(Integer timeStamp, String sourceAddr, String destAddr, Integer pktSize) {
            this.timeStamp = timeStamp;
            this.sourceAddr = sourceAddr;
            this.destAddr = destAddr;
            this.pktSize = pktSize;
        }
    }
}
