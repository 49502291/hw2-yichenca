/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package src.main.java;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.jcas.tcas.Annotation;

import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import edu.cmu.deiis.types.*;


public class GeneAnnotator extends JCasAnnotator_ImplBase {

	
	private static ConfidenceChunker chunker;
	public static final String GeneFilename = "Directory";
	
	/**
	 * @author Yichen Cai
	 * 
	 * Description: Initialize method is used to load trained model of LingPipe
	 * 
	 * @param UimaContext
	 *
	 */
	
	public void initialize(UimaContext context) throws ResourceInitializationException {
		
		chunker =null;
		
		//File modelFile = new File(GeneFilename);
		//System.out.println("Reading chunker from file=" + modelFile);
		String file = (String) context.getConfigParameterValue(GeneFilename);
		
		try {
			//chunker  = (ConfidenceChunker) AbstractExternalizable.readObject(modelFile);
			chunker = (ConfidenceChunker) AbstractExternalizable.readResourceObject(GeneAnnotator.class, file);
			System.out.println("Reading chunker from file=" + file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	 /**
	   * @author Yichen Cai
	   * 
	   * Description: This method uses LingPipe to recognize entities. The named entity recognition mainly
	   * involves a statistical named entity recognizer.
	   * Then put recognized gene, and its id, start point, end point, 
	   * casProcessID, confidence into annotation,respectively.
	   * 
	   * @param JCas
	   * 
	   */
	
	@Override
	public void process(JCas aJcas) throws AnalysisEngineProcessException {
		
	String id = "";
	String sentence ="";
	Chunking chunking =null;

	FSIterator<Annotation> it =aJcas.getAnnotationIndex(Text.type).iterator();

	
	while(it.hasNext())
	{
		Text annotation = (Text) it.next();
		id = annotation.getID();
		sentence = annotation.getSentence();
		char cs[] =sentence.toCharArray();
		
	//  The chunker will get only top 15 recognized genes. 
		
		Iterator<Chunk> chunkit = chunker.nBestChunks(cs, 0, cs.length, 30);
		
		//chunking=chunker.chunk(sentence);
		
	//	for(Chunk chunk : chunking.chunkSet())
		
		for(int n=0; chunkit.hasNext(); n++)
		{
			
			Chunk chunk = chunkit.next();
			double conf = Math.pow(2.0, chunk.score());
						
			String temp = sentence.substring(0, chunk.start());
			String words =sentence.substring(chunk.start(),chunk.end());
//			System.out.println(conf +" " + words);
		
		// The recognized gene will be further screened by confidence 
			if(conf>0.35)
			{
			int gap1 = countspaces(temp);
			int gap2 =countspaces(words);
			
			Genetype gene = new Genetype(aJcas);
			gene.setID(id);
			gene.setBegin(chunk.start()-gap1);
			gene.setEnd(chunk.end()-gap1-gap2-1);
			gene.setGene(words);
			gene.setCasProcessorId("LING");
			gene.setConfidence(conf);
			
			gene.addToIndexes();
			}
	
		}
		
	} 
	
	}
	
	
	/**
	 * @author Yichen Cai
	 * 
	 * Description: The countspaces method will count the spaces of a given string.
	 * 
	 * @param String
	 *
	 */
	
	int countspaces(String s)
	{
		int value =0;
		for (int i =0; i<s.length(); i++)
		{
			if(s.charAt(i)==' ')
			{
				value++;
			}
		}
		return value;
	}
	
}
