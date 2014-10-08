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

import java.util.*;
import java.util.Map.Entry;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.cmu.deiis.types.*;

public class Evaluator extends JCasAnnotator_ImplBase {

	//private Iterator mapLing;

	 /**
	   * @author Yichen Cai
	   * 
	   * Description: This method will evaluate the results from Lingpipe and Abner.
	   * I evaluate the results by judging the confidence and comparing between and Lingpipe and Abner.
	   * And set the result into annotation if it satisfy the conditions below:
	   * 1. the confidence of result from Lingpipe is bigger than 0.6
	   * 2. the condidence of result from Lingpipe is between 0.4 and 0.6, and also Abner outputs this result.
	   * 
	   * @param JCas
	   * 
	   */
	
	@Override
	public void process(JCas aJcas) throws AnalysisEngineProcessException {
		// TODO Auto-generated method stub
        FSIterator<Annotation> it =aJcas.getAnnotationIndex(Genetype.type).iterator();
		
        //Map<String, Genetype> mapLing = new HashMap<String, Genetype>();
		Map<String, Genetype> mapAbner = new HashMap<String, Genetype>();
  		List<Genetype> arrLing = new ArrayList<Genetype>();
	
		while(it.hasNext())
		{
			Genetype annotation = (Genetype) it.next();
			
			if(annotation.getCasProcessorId() == "LING" ){
				//mapLing.put(annotation.getGene(), annotation);
				arrLing.add(annotation);
			}
			else
				mapAbner.put(annotation.getGene(), annotation);
			
	
		}
		
		//Iterator itofLing = mapLing.entrySet().iterator();
		Iterator itofLing = arrLing.iterator();
		
		while(itofLing.hasNext())
		{
			Genetype entry = (Genetype) itofLing.next();
			
			Token token = new Token(aJcas);
			
			if(entry.getConfidence()>0.6 ||  mapAbner.containsKey(entry.getGene()))
			{
				
				token.setBegin(entry.getBegin());
				token.setEnd(entry.getEnd());
				token.setID(entry.getID());
				token.setAnnotation(entry.getGene());
				token.addToIndexes();
				
			}
		
//			else
//			{
//				Genetype abner = likeMatch(entry.getKey(), mapAbner);
//				if(abner != null)
//				{	
//				token.setBegin(abner.getBegin());
//				token.setEnd(abner.getEnd());
//				token.setID(abner.getID());
//				token.setAnnotation(abner.getGene());
//				token.addToIndexes();
//				}
//			}
		}
		
	}
	
//	public boolean likeMatch(String key, Map<String, Genetype> map) {
//	    
//	    Iterator it = map.entrySet().iterator();
//	    while(it.hasNext()) {
//	        Map.Entry<String, Genetype> entry = (Map.Entry<String, Genetype>)it.next();
//	        if (entry.getKey().indexOf(key) != -1) {
//	            return true;
//	        }
//	    }
//	    return false;
//	}
				
//	public Genetype likeMatch(String key, Map<String, Genetype> map) {
//	    
//	    Iterator it = map.entrySet().iterator();
//	    while(it.hasNext()) {
//	        Map.Entry<String, Genetype> entry = (Map.Entry<String, Genetype>)it.next();
//	        if (entry.getKey().indexOf(key) != -1) {
//	            return entry.getValue();
//	        }
//	    }
//	    return null;
//	}

}
