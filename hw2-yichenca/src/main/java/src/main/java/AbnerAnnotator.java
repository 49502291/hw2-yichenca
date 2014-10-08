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
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import edu.cmu.deiis.types.*;

import abner.Tagger;

public class AbnerAnnotator extends JCasAnnotator_ImplBase {
	
	private static Tagger model;
	
	public void initialize(UimaContext context) throws ResourceInitializationException {
		
		model =new Tagger();
	}

	 /**
	   * @author Yichen Cai
	   * 
	   * Description: This method uses Abner to recognize entities. The named entity recognition mainly
	   * involves a statistical named entity recognizer.
	   * Then put recognized gene, and its id, start point, end point, 
	   * casProcessID, confidence into annotation,respectively.
	   * 
	   * @param JCas
	   * 
	   */
	
	@Override
	public void process(JCas aJcas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
		String id = "";
		String sentence ="";
		
		FSIterator<Annotation> it =aJcas.getAnnotationIndex(Text.type).iterator();
		
		while(it.hasNext())
		{
			Text annotation = (Text) it.next();
			id = annotation.getID();
			sentence = annotation.getSentence();
			String [][] annotatedsentence = model.getEntities(sentence);
			
			for (int i = 0; i< annotatedsentence[0].length; i++)
			{
				Genetype gene = new Genetype(aJcas);
				String word = annotatedsentence[0][i];
				int begin = sentence.indexOf(word);
				
				if (begin != -1)
				{
				begin = begin - countspaces(sentence.substring(0, begin));
				int end = begin+word.length()-countspaces(word)-1;
				
				gene.setBegin(begin);
				gene.setEnd(end);
				gene.setCasProcessorId("ABNER");
				gene.setConfidence(0.5d);
				gene.setGene(word);
				gene.setID(id);
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
