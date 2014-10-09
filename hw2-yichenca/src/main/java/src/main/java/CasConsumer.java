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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedWriter;
import java.io.FileWriter;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;
import edu.cmu.deiis.types.*;


public class CasConsumer extends CasConsumer_ImplBase {

//	 public static Performance p = new Performance();
	  public static final String PARAM_OUTPUTDIR = "OutputDirectory";
//	  private static int count =0;
//	  private File mOutputDir;
	  private BufferedWriter write;

	  /**
	   * @author Yichen Cai
	   * 
	   * This method sets configuration parameters, mainly for write file and perform other initialization logic.
	   * The output file is hw2-yichenca.out.  And the golden standard file is sample.out. 
	   *  
	   * @param mOutputDir
	   * 
	   */
	  
	  
	  public void initialize() throws ResourceInitializationException {
		  
		    String aa = ((String) getConfigParameterValue(PARAM_OUTPUTDIR)).trim();
//		    if (!mOutputDir.exists()) {
//		      mOutputDir.mkdirs();
//		    }
	//	    String aa = mOutputDir.getPath();
		   
		    try {
				write = new BufferedWriter (new FileWriter(aa));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
//		    try {
//				p.setAnswerText("5000.out");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		    
		    
		  }
	  
	  /**
	   * @author Yichen Cai
	   * This method reads annotation from annotator, and call function Write2File to write annotation's features to file
	   *  
	   * @param CAS
	   */
	  
	  public void processCas(CAS aCAS) throws ResourceProcessException {
		    JCas jcas;
		    try {
		      jcas = aCAS.getJCas();
		    } catch (CASException e) {
		      throw new ResourceProcessException(e);
		    }

		    FSIterator it = jcas.getAnnotationIndex(Token.type).iterator();
		    File outFile = null;
		    
		    while(it.hasNext())
		    {
		    	Token annotation = (Token)it.next();
		    	//Genetype annotation =(Genetype)it.next();
		    	
		    	try {
					Write2File(annotation.getBegin(), annotation.getEnd(),annotation.getID(), annotation.getAnnotation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    }
	  }
	  
	  /**
	   * @author Yichen Cai
	   * 
	   * This method writes annotation's features to given output file.
	   * 
	   * @param start
	   * @param end
	   * @param ID
	   * @param gene
	   *  
	   */
	  
	  
	  public void Write2File(int start, int end, String ID, String gene) throws Exception 
	  {
		  
		  String out = ID+"|"+start+" "+ end + "|"+gene;
//		  count++;
//		  p.setAnswernum(count);
//		  p.judgeAnswer(out);
		  
		  write.write(out);
		  write.newLine();
		  write.flush();
		  
	  }
	  
	  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,IOException
	  {
		  write.close();
//		  p.printReport();
		  
	  }


}
