package models.metamap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/*import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;*/

import models.metamap.model.metamap.AcronymsAbbrevs;
import models.metamap.model.metamap.ConceptPair;
import models.metamap.model.metamap.Ev;
import models.metamap.model.metamap.Mapping;
import models.metamap.model.metamap.Negation;
import models.metamap.model.metamap.PCM;
import models.metamap.model.metamap.Position;
import models.metamap.model.metamap.Result;
import models.metamap.model.metamap.Utterance;

import models.metamap.dao.MetaMapApi;
//import models.metamap.dao.QuestionDAO;
import models.metamap.daoimpl.MetaMapApiImpl;

import models.metamap.model.MetamapValues;
import models.metamap.model.MetamapValue;

//@Service
public class MetamapService {

	//@Autowired
	private MetaMapApi api;
	
	//@Autowired
	private DataSource dataSource;
	
	Connection connection = null; 

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public MetamapService(String serverHostname, int serverPort) {
		this.api = new MetaMapApiImpl();
		this.api.setHost(serverHostname);
		this.api.setPort(serverPort);
	}

	void setTimeout(int interval) {
		this.api.setTimeout(interval);
	}

	public MetamapService() {
		this.api = new MetaMapApiImpl();
	}

	public MetamapValues getMetamapDataValues(String[] args) {
		MetamapValues finalMetamapValues = new MetamapValues();
		List<MetamapValue> metamapValueList = new ArrayList<MetamapValue>();
		for(String token: args) {
			String tokenArray[] = token.split(",");
			MetamapValues metaValues = getMetamapData(tokenArray);
			List<MetamapValue> metamapValList = metaValues.getMetamapValues();
			for(MetamapValue mVal: metamapValList) {
				metamapValueList.add(mVal);
			}
		}
		finalMetamapValues.setMetamapValues(metamapValueList);
		return finalMetamapValues;
	}

	public MetamapValues getMetamapData(String[] args) {
		//setDataSource(ds);

		MetamapValues mValues = new MetamapValues();
		List<MetamapValue> mValueList = null;

		String serverhost = MetaMapApi.DEFAULT_SERVER_HOST;
		int serverport = MetaMapApi.DEFAULT_SERVER_PORT; // default port
		int timeout = -1; // use default timeout
		PrintStream output = System.out;
		if (args.length < 1) {
			System.exit(0);
		}
		StringBuffer termBuf = new StringBuffer();
		List<String> options = new ArrayList<String>();
		int i = 0;
		while (i < args.length) {
			if (args[i].charAt(0) == '-') {
				if (args[i].equals("-h") || args[i].equals("--help") || args[i].equals("-?")) {
					System.exit(0);
				} else if (args[i].equals("-%") || args[i].equals("--XML")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-@") || args[i].equals("--WSD")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-J") || args[i].equals("--restrict_to_sts")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-R") || args[i].equals("--restrict_to_sources")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-S") || args[i].equals("--tagger")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-V") || args[i].equals("--mm_data_version")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-Z") || args[i].equals("--mm_data_year")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-e") || args[i].equals("--exclude_sources")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-k") || args[i].equals("--exclude_sts")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("-r") || args[i].equals("--threshold")) {
					options.add(args[i]);
					i++;
					options.add(args[i]);
				} else if (args[i].equals("--metamap_server_host")) {
					i++;
					serverhost = args[i];
				} else if (args[i].equals("--metamap_server_port")) {
					i++;
					serverport = Integer.parseInt(args[i]);
				} else if (args[i].equals("--metamap_server_timeout")) {
					i++;
					timeout = Integer.parseInt(args[i]);
				} else if (args[i].equals("--output")) {
					i++;
					try {
						output = new PrintStream(args[i]);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("output file: " + args[i]);
				} else {
					options.add(args[i]);
				}
			} else {
				termBuf.append(args[i]).append(" ");
			}
			i++;
		}
		System.out.println("serverport: " + serverport);
		MetamapService frontEnd = new MetamapService(serverhost, serverport);
		System.out.println("options: " + options);
		System.out.println("terms: " + termBuf);

		try {
			connection = dataSource.getConnection();
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		if (timeout > -1) {
			frontEnd.setTimeout(timeout);
		}

		try {
			if (termBuf.length() > 0) {
				File inFile = new File(termBuf.toString().trim());
				if (inFile.exists()) {
					System.out.println("input file: " + termBuf);
					BufferedReader ib = new BufferedReader(new FileReader(inFile));
					StringBuffer inputBuf = new StringBuffer();
					String line = "";
					while ((line = ib.readLine()) != null) {
						inputBuf.append(line).append('\n');
					}
					mValueList = frontEnd.process(inputBuf.toString(), output, options, connection);
				} else {
					mValueList = frontEnd.process(termBuf.toString(), output, options, connection);
				}
			} else {
				System.exit(0);
			}
			connection.close();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		
		mValues.setMetamapValues(mValueList);

		return mValues;

	}

	List<MetamapValue> process(String terms, PrintStream out, List<String> serverOptions, Connection connection) throws Exception {
		List<MetamapValue> mValueList = new ArrayList<MetamapValue>();
		
		if (serverOptions.size() > 0) {
			api.setOptions(serverOptions);
		}
		List<Result> resultList = api.processCitationsFromString(terms);
		for (Result result : resultList) {
			if (result != null) {
				out.println("input text: ");
				out.println(" " + result.getInputText());
				List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevsList();
				if (aaList.size() > 0) {
					// out.println("Acronyms and Abbreviations:");
					for (AcronymsAbbrevs e : aaList) {
						// out.println("Acronym: " + e.getAcronym());
						// out.println("Expansion: " + e.getExpansion());
						// out.println("Count list: " + e.getCountList());
						// out.println("CUI list: " + e.getCUIList());
					}
				}
				List<Negation> negList = result.getNegationList();
				if (negList.size() > 0) {
					// out.println("Negations:");
					for (Negation e : negList) {
						// out.println("type: " + e.getType());
						// out.print("Trigger: " + e.getTrigger() + ": [");
						for (Position pos : e.getTriggerPositionList()) {
							// out.print(pos + ",");
						}
						// out.println("]");
						// out.print("ConceptPairs: [");
						for (ConceptPair pair : e.getConceptPairList()) {
							// out.print(pair + ",");
						}
						// out.println("]");
						// out.print("ConceptPositionList: [");
						for (Position pos : e.getConceptPositionList()) {
							// out.print(pos + ",");
						}
						// out.println("]");
					}
				}
				for (Utterance utterance : result.getUtteranceList()) {
					// out.println("Utterance:");
					// out.println(" Id: " + utterance.getId());
					// out.println(" Utterance text: " + utterance.getString());
					// out.println(" Position: " + utterance.getPosition());

					for (PCM pcm : utterance.getPCMList()) {
						// out.println("Phrase:");
						// out.println(" text: " + pcm.getPhrase().getPhraseText());
						// out.println(" Minimal Commitment Parse: " +
						// pcm.getPhrase().getMincoManAsString());
						// out.println("Candidates:");
						for (Ev ev : pcm.getCandidateList()) {
							// out.println(" Candidate:");
							// out.println(" Score: " + ev.getScore());
							String conceptId = ev.getConceptId();
							String conceptName = ev.getConceptName();
							String snomedId = getSnomedCode(connection, conceptId);
							String icdCode = getICDCode(connection, snomedId);

							mValueList.add(new MetamapValue(conceptId, conceptName, snomedId, icdCode));

							out.println("  Concept Id: " + conceptId);
							out.println("  Concept Name: " + conceptName);
							out.println("  SNOMED ID: " + snomedId);
							out.println("  ICD Code: " + icdCode);
							// out.println(" Preferred Name: " + ev.getPreferredName());
							// out.println(" Matched Words: " + ev.getMatchedWords());
							// out.println(" Semantic Types: " + ev.getSemanticTypes());
							// out.println(" MatchMap: " + ev.getMatchMap());
							// out.println(" MatchMap alt. repr.: " + ev.getMatchMapList());
							// out.println(" is Head?: " + ev.isHead());
							// out.println(" is Overmatch?: " + ev.isOvermatch());
							// out.println(" Sources: " + ev.getSources());
							// out.println(" Positional Info: " + ev.getPositionalInfo());
						}

						// out.println("Mappings:");
						for (Mapping map : pcm.getMappingList()) {
							// out.println(" Map Score: " + map.getScore());
							for (Ev mapEv : map.getEvList()) {
								// out.println(" Score: " + mapEv.getScore());

								String conceptId = mapEv.getConceptId();
								String conceptName = mapEv.getConceptName();
								String snomedId = getSnomedCode(connection, conceptId);
								String icdCode = getICDCode(connection, snomedId);

								mValueList.add(new MetamapValue(conceptId, conceptName, snomedId, icdCode));

								out.println("  Concept Id: " + conceptId);
								out.println("  Concept Name: " + conceptName);
								out.println("  SNOMED ID: " + snomedId);
								out.println("  ICD Code: " + icdCode);
								// out.println(" Preferred Name: " + mapEv.getPreferredName());
								// out.println(" Matched Words: " + mapEv.getMatchedWords());
								// out.println(" Semantic Types: " + mapEv.getSemanticTypes());
								// out.println(" MatchMap: " + mapEv.getMatchMap());
								// out.println(" MatchMap alt. repr.: " + mapEv.getMatchMapList());
								// out.println(" is Head?: " + mapEv.isHead());
								// out.println(" is Overmatch?: " + mapEv.isOvermatch());
								// out.println(" Sources: " + mapEv.getSources());
								// out.println(" Positional Info: " + mapEv.getPositionalInfo());
							}
						}
					}
				}
			} else {
				out.println("NULL result instance! ");
			}
		}
		this.api.resetOptions();
		
		return mValueList;
	}

	public String getSnomedCode(Connection connection, String conceptId) {
		String sqlSelect = "select SNOMED_ID from UMLS where UMLS_ID=?";
		String snomedId = "";

		try {
			PreparedStatement statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, conceptId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				snomedId = rs.getString(1);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return snomedId;
	}

	public String getICDCode(Connection connection, String snomedId) {
		String sqlSelect = "select ICD_CODE from UMLS where SNOMED_ID=?";
		String icdCode = "";

		try {
			PreparedStatement statement = connection.prepareStatement(sqlSelect);
			statement.setString(1, snomedId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				icdCode = rs.getString(1);
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		return icdCode;
	}
}
