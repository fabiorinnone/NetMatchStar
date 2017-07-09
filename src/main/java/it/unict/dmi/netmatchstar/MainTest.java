package it.unict.dmi.netmatchstar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import javax.swing.JPanel;

import it.unict.dmi.netmatchstar.algorithm.*;
import it.unict.dmi.netmatchstar.algorithm.significance.RandomGenerator;
import it.unict.dmi.netmatchstar.exceptions.InvalidSIFException;
import it.unict.dmi.netmatchstar.graph.Graph;
import it.unict.dmi.netmatchstar.graph.GraphLoader;
import it.unict.dmi.netmatchstar.utils.Common;

import it.unict.dmi.netmatchstar.utils.PrintHeapInfo;
import org.apache.commons.io.FilenameUtils;

/**
 * Created by fabior on 06/07/17.
 */
public class MainTest {

    private static boolean isApproximate;
    private static boolean isUnlabeled;
    private static Vector approxPaths;

    private static boolean isDirected;

    private GraphLoader loader;

    private static ArrayList<int[]> array;

    private static long totalMatches;
    private static long distinctMatches;

    private static Hashtable<String,Long> table;

    private static int RI_ALGORITHM_ID = 0;
    private static int RIDS_ALGORITHM_ID = 1;

    private static String ALGORITHM_KEY = "algorithm_id";
    private static String TARGET_PATH_KEY = "target_path";
    private static String QUERY_PATH_KEY = "query_path";
    private static String LOADING_TIME_KEY = "loading_time";
    private static String TOTAL_MATCHES_KEY = "total_matches";
    private static String DISTINCT_MATCHES_KEY = "distinct_matches";
    private static String TOTAL_TIME_KEY = "total_time";

    public static void main(String[] args) {
        String jarFileName = Common.APP_NAME_ALIAS + "-" + Common.APP_VERSION + ".jar";
        String argsErrorMsg = "Usage: java -jar " + jarFileName + " [labeled unlabeled] [directed undirected] " +
                "directory";

        if (args.length != 3) {
            System.out.println(argsErrorMsg);
            System.exit(1);
        }

        String labelsOpt = args[0];
        if (!labelsOpt.equals("labeled") && !labelsOpt.equals("unlabeled")) {
            System.out.println(argsErrorMsg);
            System.exit(4);
        }

        String graphOpt = args[1];
        if (!graphOpt.equals("directed") && !graphOpt.equals("undirected")) {
            System.out.println(argsErrorMsg);
            System.exit(5);
        }

        String directoryPath = args[2];

        int interval = 60000 * 60;
        Timer timer = new Timer();
        timer.schedule(new PrintHeapInfo(), interval, interval);

        ArrayList<File> queryFiles = new ArrayList<>();
        ArrayList<File> targetFiles = new ArrayList<>();
        listFiles(directoryPath, queryFiles, targetFiles);

        HashMap<String,ArrayList<String>> networksFiles = loadNetworksFiles(queryFiles, targetFiles);

        HashMap<String,Object> results1;
        HashMap<String,Object> results2;

        Set<Map.Entry<String,ArrayList<String>>> entrySet = networksFiles.entrySet();
        Iterator<Map.Entry<String,ArrayList<String>>> entrySetIterator = entrySet.iterator();
        while (entrySetIterator.hasNext()) {
            Map.Entry<String,ArrayList<String>> next = entrySetIterator.next();
            String target = next.getKey();
            ArrayList<String> queries = next.getValue();
            for (String query : queries) {
                System.out.println("Target Network: " + target);
                System.out.println("Query Network: " + query);

                results1 = benchmarkMatching(labelsOpt, query, target, RI_ALGORITHM_ID);
                results2 = benchmarkMatching(labelsOpt, query, target, RIDS_ALGORITHM_ID);

                System.out.println("Writing results to output file...");
                try {
                    writeResultsToFile(results1, results2);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.exit(0);
    }

    private static HashMap<String,ArrayList<String>> loadNetworksFiles(ArrayList<File> queryFiles,
                                                                       ArrayList<File> targetFiles) {
        HashMap<String,ArrayList<String>> networksFiles = new HashMap<>();

        for (File targetFile : targetFiles) {
            String targetName = targetFile.getName();

            for (File queryFile : queryFiles) {
                if (queryFile.getAbsolutePath().contains(targetName)) {
                    ArrayList<String> queries = networksFiles.get(targetFile.getAbsolutePath());
                    if (queries == null)
                        queries = new ArrayList<>();
                    queries.add(queryFile.getAbsolutePath());
                    networksFiles.put(targetFile.getAbsolutePath(), queries);
                }
            }
        }

        return networksFiles;
    }

    private static void listFiles(String directoryName, ArrayList<File> queryFiles, ArrayList<File> targetFiles) {
        File directory = new File(directoryName);

        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                if (!file.getAbsolutePath().contains("_queries"))
                    targetFiles.add(file);
                else
                    queryFiles.add(file);
            } else if (file.isDirectory()) {
                listFiles(file.getAbsolutePath(), queryFiles, targetFiles);
            }
        }
    }

    private static HashMap<String,Object> benchmarkMatching(String labelsOpt, String queryPath,
                                                            String targetPath, int algorithmId) {
        if (algorithmId == RI_ALGORITHM_ID)
            System.out.println("Matching Query to Target using RI Algorithm");
        else
            System.out.println("Matching Query to Target using RI-DS Algorithm");

        long t_start = System.currentTimeMillis();
        System.out.println("Loading Network...");
        Graph db = null;
        try {
            GraphLoader dbLoader = loadTargetGraphFromFile(targetPath);
            db = new Graph(dbLoader, false);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(99);
        }
        long t_end = System.currentTimeMillis();
        double loadingTime = ((double)(t_end-t_start))/(1000.0);
        System.out.println("Loading Network time: " + loadingTime);

        isApproximate = false;
        isUnlabeled = false;

        if (labelsOpt.equals("directed"))
            isDirected = true;
        else
            isDirected = false;

        approxPaths = new Vector<String>();

        t_start = System.currentTimeMillis();
        System.out.println("Loading Query...");
        Graph q = null;
        try {
            GraphLoader qLoader = loadQueryGraphFromFile(queryPath);
            q = new Graph(qLoader, isDirected);
        }
        catch(Exception e) {
            e.printStackTrace();
            System.exit(99);
        }
        t_end = System.currentTimeMillis();
        double time = ((double)(t_end-t_start))/(1000.0);
        System.out.println("Loading Query time: "+time);

        if(labelsOpt.equals("labeled")) {
            q.setNodeComparator(new ExactNodeComparator());
            q.setEdgeComparator(new ExactEdgeComparator());
        }
        else {
            q.setNodeComparator(new ApproxNodeComparator());
            q.setEdgeComparator(new ApproxEdgeComparator());
        }

        t_start = System.currentTimeMillis();
        RIMatch m = null;
        for (int i = 0; i < 10; i++) {
            if (!isUnlabeled)
                m = new RIMatch(q, db);
            else
                m = new RIMatch(q, db, isApproximate, approxPaths);

            Set<Integer> nodiTarget = db.nodes().keySet();
            try {
                if (algorithmId == RI_ALGORITHM_ID)
                    m.match_simple(nodiTarget.iterator());
                else
                    m.match(nodiTarget.iterator());
            }
            catch(Exception e) {
                e.printStackTrace();
                System.exit(99);
            }
        }
        t_end = System.currentTimeMillis();
        time = ((double)(t_end-t_start))/(1000.0);
        System.out.println("Matching time: "+time/10);

        array = m.getMatchesList();
        totalMatches = m.getNofMatches();

        table = m.getMatchesOccurrences();
        distinctMatches = table.size();

        System.out.println("Total Matches: " + totalMatches);
        System.out.println("Distinct Matches: " + distinctMatches);

        HashMap<String,Object> results = new HashMap<>();
        results.put(ALGORITHM_KEY, algorithmId);
        results.put(TARGET_PATH_KEY, targetPath);
        results.put(QUERY_PATH_KEY, queryPath);
        results.put(LOADING_TIME_KEY, loadingTime);
        results.put(TOTAL_MATCHES_KEY, totalMatches);
        results.put(DISTINCT_MATCHES_KEY, distinctMatches);
        results.put(TOTAL_TIME_KEY, time);

        return results;
    }

    /*public static void printDistinctMatches(ArrayList array) {
        Iterator iterator = array.iterator();
        System.out.print("{");
        while(iterator.hasNext()) {
            int[] match = (int[]) iterator.next();
            System.out.print("[");
            for (int i = 0; i < match.length; i++) {
                System.out.print(match[i]);
                if (i != match.length - 1)
                    System.out.print(",");
            }
            System.out.print("]");
            if (iterator.hasNext())
                System.out.print(",");
        }
        System.out.println("}");
    }*/

    private static void writeResultsToFile(HashMap<String,Object> results1,
                                           HashMap<String,Object> results2) throws IOException {
        String targetFileName = (String) results1.get(TARGET_PATH_KEY);
        String queryFileName = (String) results1.get(QUERY_PATH_KEY);
        Double loadingNetTimeRi = (Double) results1.get(LOADING_TIME_KEY);
        Long totalMatches = (Long) results1.get(TOTAL_MATCHES_KEY);
        Long distinctMatches = (Long) results1.get(DISTINCT_MATCHES_KEY);
        Double totalTimeRi = (Double) results1.get(TOTAL_TIME_KEY);

        Double loadingNetTimeRiDs = (Double) results2.get(LOADING_TIME_KEY);
        Double totalTimeRiDs = (Double) results2.get(TOTAL_TIME_KEY);

        String targetFileBaseName = FilenameUtils.getBaseName(targetFileName);
        String queryFileBaseName = FilenameUtils.getBaseName(queryFileName);
        File resultsFile = new File("netmatch-matching-results.csv");
        BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFile, true));
        if (resultsFile.length() == 0)
            writer.write("Target Network;Query Network;Loading Network Time (RI);Matching Time (RI);" +
                    "Loading Network Time (RI-DS);Matching Time (RI-DS);Total Matches;Distinct Matches\n");
        writer.write(targetFileBaseName + ";" + queryFileBaseName + ";" +
                loadingNetTimeRi + ";" + totalTimeRi + ";" + loadingNetTimeRiDs + ";" + totalTimeRiDs + ";" +
                totalMatches + ";" + distinctMatches + "\n");
        writer.close();
    }

    private static GraphLoader loadTargetGraphFromFile(String fileName) throws IOException {
        GraphLoader loader = new GraphLoader(new JPanel());

        ArrayList<String> nodeAttributes = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        reader.readLine();
        reader.readLine();

        String line = "";
        while ((line = reader.readLine()).split(" ").length != 2 ) {
            String nodeAttr = line;
            nodeAttributes.add(nodeAttr);
        }
        nodeAttributes.remove(nodeAttributes.size()-1);
        addNodes(loader, nodeAttributes, true);

        //edgeCount = 0;
        addEdge(loader, line, true);

        while((line = reader.readLine()) != null)
            addEdge(loader, line, true);

        reader.close();

        return loader;
    }

    private static GraphLoader loadQueryGraphFromFile(String fileName) throws IOException, InvalidSIFException {
        GraphLoader loader = new GraphLoader(new JPanel());

        File file = new File(fileName);
        if (FilenameUtils.getExtension(file.getName()).equals("gff"))
            loader = loadGFFFile(loader, fileName);
        else if (FilenameUtils.getExtension(file.getName()).equals("sif"))
            loader = loadSIFFile(fileName);
        else
            loader = null;

        return loader;
    }

    private static GraphLoader loadSIFFile(String fileName) throws IOException, InvalidSIFException {
        GraphLoader loader = new GraphLoader(new JPanel());

        String nodeAttrFileName = fileName.substring(0,fileName.length()-3)+"NA";
        File nodeAttrFile = new File(nodeAttrFileName);
        String edgeAttrFileName = fileName.substring(0,fileName.length()-3)+"EA";
        File edgeAttrFile = new File(edgeAttrFileName);

        HashMap<Integer,String> nodeAttributes = null;

        HashMap<Integer,Integer> nodesMap = new HashMap<Integer,Integer>();

        loadNodes(loader, nodesMap, nodeAttrFileName);
        loadEdges(loader, nodesMap, edgeAttrFileName);

        return loader;
    }

    @SuppressWarnings("unchecked")
    private static void loadEdges(GraphLoader loader, HashMap<Integer,Integer> nodesMap,
                                  String edgeAttrFileName) throws InvalidSIFException {
        try {
            File edgeAttrFile = new File(edgeAttrFileName);
            BufferedReader reader = new BufferedReader(new FileReader(edgeAttrFile));
            String line = reader.readLine(); //Legge la prima riga
            int lineNumber = 1; //Gli attributi cominciano dalla seconda riga
            while((line = reader.readLine()) != null) {
                lineNumber++;
                String l = line.trim();
                String ss[] = l.split(" = ");
                if (ss.length == 2) {
                    String e = ss[0].trim();
                    String a = ss[1].trim();

                    String[] e1 = e.split("\\(");
                    String[] e2 = e.split("\\)");
                    Integer sourceId = Integer.parseInt(e1[0].trim());
                    Integer targetId = Integer.parseInt(e2[1].trim());

                    if (!Common.isApproximatePath(a)) {
                        loader.insertEdge(nodesMap.get(sourceId), nodesMap.get(targetId), a, false);
                        if (!isDirected)
                            loader.insertEdge(nodesMap.get(targetId), nodesMap.get(sourceId), a, false);
                    }
                    else {
                        String approxPath = nodesMap.get(sourceId)+","+nodesMap.get(targetId)+","+a;
                        if (!approxPaths.contains(approxPath))
                            approxPaths.add(nodesMap.get(sourceId)+","+nodesMap.get(targetId)+","+a);
                    }
                }
                else
                    throw new InvalidSIFException(lineNumber);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static boolean areThereAttributeFiles(File nodeAttrFile, File edgeAttrFile) {
        return nodeAttrFile.exists() && edgeAttrFile.exists();
    }

    private static HashMap<Integer,String> loadNodes(GraphLoader loader, HashMap<Integer,Integer> nodesMap,
                                                     String nodeAttrFileName) throws InvalidSIFException {
        HashMap<Integer,String> nodeAttributes = new HashMap<Integer,String>();
        try {
            File nodeAttrFile = new File(nodeAttrFileName);
            BufferedReader reader = new BufferedReader(new FileReader(nodeAttrFile));
            String line = reader.readLine(); //Legge la prima riga
            int lineNumber = 1; //Gli attributi cominciano dalla seconda riga
            int i = 0;
            while((line = reader.readLine()) != null) {
                lineNumber++;
                String l = line.trim();
                String ss[] = l.split(" = ");
                if (ss.length == 2) {
                    Integer n = Integer.parseInt(ss[0].trim());
                    String a = ss[1].trim();
                    nodeAttributes.put(n, a);
                    nodesMap.put(n, i++);
                    loader.insertNode(a, false);
                }
                else
                    throw new InvalidSIFException(lineNumber);
            }
        } catch (IOException ex) {
            System.err.println(ex.getCause());
        }

        return nodeAttributes;
    }

    private static GraphLoader loadGFFFile(GraphLoader loader, String fileName) throws IOException {
        ArrayList<String> nodeAttributes = new ArrayList<String>();
        HashMap<Integer,String> nodesMap = new HashMap<Integer,String>();

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        reader.readLine(); //network name (but we use file name)
        reader.readLine(); //node number

        String line = "";
        while ((line = reader.readLine()).split(" ").length != 2 ) {
            String nodeAttr = line;
            nodeAttributes.add(nodeAttr);
        }
        nodeAttributes.remove(nodeAttributes.size()-1);
        addNodes(loader, nodeAttributes, false);

        addEdge(loader, line, false);

        while((line = reader.readLine()) != null)
            addEdge(loader, line, false);

        reader.close();

        return loader;
    }

    private static void addNodes(GraphLoader loader, ArrayList<String> nodeAttributes,
                                 boolean target) {
        Iterator<String> nodeAttributesIterator = nodeAttributes.iterator();
        if (target) {
            int nodeId = 0;
            while(nodeAttributesIterator.hasNext()) {
                ArrayList<String> attributesList = new ArrayList<String>();
                String nodeAttribute = nodeAttributesIterator.next();
                attributesList.add(nodeAttribute);
                attributesList.add(0, nodeId + Common.STD_EDGE);
                loader.insertNode(attributesList, false);
                nodeId++;
            }
        }
        else {
            while(nodeAttributesIterator.hasNext()) {
                String nodeAttribute = nodeAttributesIterator.next();
                loader.insertNode(nodeAttribute, false);
            }
        }
    }

    private static void addEdge(GraphLoader loader, String line, boolean target) {
        String[] split = line.trim().split(" ");
        int sourceId = Integer.parseInt(split[0]);
        int targetId = Integer.parseInt(split[1]);
        if (target) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("?");
            loader.insertEdge(sourceId, targetId, list, false);
            if (!isDirected)
                loader.insertEdge(targetId, sourceId, list, false);
        }
        else {
            loader.insertEdge(sourceId, targetId, "?", false);
            if (!isDirected)
                loader.insertEdge(targetId, sourceId, "?", false);
        }
    }
}