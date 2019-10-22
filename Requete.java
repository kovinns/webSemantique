
import java.io.*;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class Requete {

    static final String file  = "base.ttl";

    public static void main (String args[]) {
    	Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open( file );
        if (in == null) {
            throw new IllegalArgumentException( "File: " + file + " not found");
        }

        model.read(in, null, "Turtle");

        String prefix = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
        		"PREFIX dbo: <http://fr.dbpedia.org/ontology/>\n" +
        		"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
        		"PREFIX wdws: <http://www.wikidata.org/wiki/Special:EntityData/>\n" +
        		"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";

        String queryString1 = prefix + "select ?commonName\n"
        		+ "where {\n"
        		+ "?x dbo:family ?family;\n"
        		+ "   wdws:P225 ?commonName;\n"
        		+ "   wdws:P141 ?status.\n"
        		+ "?family ex:group ?group.\n"
        		+ "?group rdfs:label ?groupName.\n"
        		+ "filter(?groupName = \"Mammals\").\n"
        		+ "filter(?status = \"Endangered\")\n"
        		+ "} limit 100";

    	Query query1 = QueryFactory.create(queryString1) ;
    	try (QueryExecution qexec = QueryExecutionFactory.create(query1, model)) {
    		ResultSet results = qexec.execSelect() ;
    		ResultSetFormatter.out(System.out, results, query1) ;
	    }

    	String queryString2 = prefix + "select ?familyName (count(?scientificName) as ?nbEndangered)\n"
        		+ "where {\n"
        		+ "?x rdfs:label ?scientificName;\n"
        		+ "   wdws:P141 ?status;\n"
        		+ "   dbo:family ?family.\n"
        		+ "?family rdfs:label ?familyName;\n"
        		+ "        ex:group ?group.\n"
        		+ "?group ex:type ?type.\n"
        		+ "?type rdfs:label ?typeName.\n"
        		+ "filter(?typeName = \"Plant\").\n"
        		+ "filter(?status = \"Endangered\").\n"
        		+ "} group by ?familyName\n"
        		+ "order by desc(?nbEndangered)";
    	Query query2 = QueryFactory.create(queryString2) ;
    	try (QueryExecution qexec = QueryExecutionFactory.create(query2, model)) {
    		ResultSet results = qexec.execSelect() ;
    		ResultSetFormatter.out(System.out, results, query2) ;
	    }
    }
}
