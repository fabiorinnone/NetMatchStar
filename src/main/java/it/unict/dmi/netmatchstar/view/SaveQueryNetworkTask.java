/*
 * Copyright (c) 2015, Rosalba Giugno.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the University of Catania nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY ROSALBA GIUGNO ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL ROSALBA GIUGNO BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package it.unict.dmi.netmatchstar.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

/**
 * 
 * @author Fabio Rinnone
 *
 */
public class SaveQueryNetworkTask extends AbstractTask {
	
	private static boolean completedSuccessfully;
	
	//private CySwingAppAdapter adapter;
	private HashMap<String, String> nodesMap;
	private HashMap<String, String> edgesMap;
	private File file;
	//private String fileName;
	
	private String fileName;
	
	private BufferedWriter writer1 = null;
	private BufferedWriter writer2 = null;
	private BufferedWriter writer3 = null;

	private TaskMonitor taskMonitor;
	private boolean interrupted;
	
	public SaveQueryNetworkTask(HashMap<String, String> nodesMap, HashMap<String, String> edgesMap, File file) {
		//this.adapter = adapter;
		this.nodesMap = nodesMap;
		this.edgesMap = edgesMap;
		this.file = file;
		//this.fileName = fileName;
		
		fileName = file.getAbsolutePath();
	}
	
	@Override
	public void run(TaskMonitor tm) throws Exception {
		taskMonitor = tm;
		
		if (taskMonitor == null) {
			throw new IllegalStateException("Task Monitor is not set.");
		}
		
		System.out.println("Saving Query Network...");
		taskMonitor.setProgress(-1.0);
		taskMonitor.setStatusMessage("Saving Query Network...");
		
		writeFiles();
		
		if (interrupted) 
			return;
	}
	
	private void writeFiles() throws IOException {
		//Scrittura su file temporanei
		File tempFile1 = File.createTempFile("tmp",null,new File("."));
	    writer1 = new BufferedWriter(new FileWriter(tempFile1));

	    File tempFile2 = null;
	    File fileNA = new File(fileName.substring(0, fileName.length()-3)+"NA");

	    File tempFile3 = null;
	    File fileEA = new File(fileName.substring(0, fileName.length()-3)+"EA");

	    if (writer2 == null) {
	    	tempFile2 = File.createTempFile("na_tmp", null, new File("."));
	    	writer2 = new BufferedWriter(new FileWriter(tempFile2));
	    }
	    
	    writer2.write("Node Attributes\n");
	    Set<Entry<String, String>> nodes = nodesMap.entrySet();
	    Iterator<Entry<String, String>> nodesIterator = nodes.iterator();
	    while(nodesIterator.hasNext()) {
	    	Map.Entry<String, String> pair = (Entry<String, String>) nodesIterator.next();
	    	String sharedName = pair.getKey();
	    	String label = pair.getValue();
	    	writer2.write(sharedName + " = " + label + "\n");
	    	
	    	//nodesMap.remove(sharedName);
	    }
	    
	    if (writer3 == null) {
	    	tempFile3 = File.createTempFile("ea_tmp", null, new File("."));
    		writer3 = new BufferedWriter(new FileWriter(tempFile3));
	    }
	    
	    writer3.write("Edge Attributes\n");
	    Set<Entry<String, String>> edges = edgesMap.entrySet();
	    Iterator<Entry<String, String>> edgesIterator = edges.iterator();
	    while(edgesIterator.hasNext()) {
	    	Map.Entry<String, String> pair = (Entry<String, String>) edgesIterator.next();
	    	String sharedName = pair.getKey();
	    	String label = pair.getValue();
	    	writer3.write(sharedName + " = " + label + "\n");
	    	
	    	String[] split1 = sharedName.split("\\(");
	    	String sourceLabel = split1[0].trim();
	    	String tmp = split1[1];
	    	String[] split2 = tmp.split("\\)");
	    	String targetLabel = split2[1].trim();
	    	
	    	sharedName = sharedName.replace(" (", "\t");
	    	sharedName = sharedName.replace(") ", "\t");
	    	writer1.write(sharedName + "\n");
	    	
	    	if (nodesMap.containsKey(sourceLabel))
	    		nodesMap.remove(sourceLabel);
	    	if (nodesMap.containsKey(targetLabel))
	    		nodesMap.remove(targetLabel);
	    }
	    
	    if (nodesMap.size() > 0) { //ci sono nodi isolati
	    	nodes = nodesMap.entrySet();
	    	nodesIterator = nodes.iterator();
		    while(nodesIterator.hasNext()) {
		    	Map.Entry<String, String> pair = (Entry<String, String>) nodesIterator.next();
		    	String sharedName = pair.getKey();
		    	String label = pair.getValue();
		    	writer1.write(sharedName + "\n");
		    	//writer2.write(sharedName + " = " + label + "\n");
		    }
	    }
	    
	    writer1.close();
	    tempFile1.renameTo(file);
	    if(writer2 != null) { //Se è stato creato il file con attributi dei nodi
	    	writer2.close();
	    	tempFile2.renameTo(fileNA);
	    }
	    if(writer3 != null) {//Se è stato creato il file con attributi degli archi
	    	writer3.close();
	    	tempFile3.renameTo(fileEA);
	    }
	}
	
	public static boolean isCompletedSuccessfully() {
		return completedSuccessfully;
	}
    
    public String getTitle() {
        return "NetMatch";
    }

    public void halt() {
    	interrupted = true;
    }

    public void setTaskMonitor(TaskMonitor tm) throws IllegalThreadStateException {
		if(taskMonitor != null)
			throw new IllegalStateException("Task Monitor is already set.");
    	taskMonitor = tm;
    }
    
    @Override
	public void cancel() {
		//this.interrupted = true;
	}
}
