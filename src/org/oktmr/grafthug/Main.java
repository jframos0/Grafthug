package org.oktmr.grafthug;

import org.oktmr.grafthug.graph.prefixtree.Manager;
import org.oktmr.grafthug.graph.prefixtree.TreeNode;
import org.oktmr.grafthug.graph.rdf.Dictionnaire;
import org.oktmr.grafthug.graph.rdf.RdfEdge;
import org.oktmr.grafthug.graph.rdf.RdfNode;
import org.oktmr.grafthug.query.Condition;
import org.oktmr.grafthug.query.Query;
import org.oktmr.grafthug.query.QueryParser;
import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public final class Main {

    private static final DataStore ds = new DataStore();
    private static final Dictionnaire dico = new Dictionnaire();
    private static final Manager manager = new Manager();


    static String queryString = " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
            + "PREFIX ub: <http://swat.cse.lehigh.edu/onto/univ-bench.owl#>" + " SELECT ?x "
            + "WHERE {?x rdf:type ub:Subj18Student .  ?x rdf:type ub:GraduateStudent . ?x rdf:type ub:TeachingAssistant }";

    static String filePath = "University0_0.owl";

    public static void main(String args[]) throws Exception {
        // beginning indexation
        indexation(filePath);
        //dico.index();
        System.out.println("Number of treeNodes : " + manager.treeNodes.size());
        System.out.println("Number of edges : " + dico.getEdges().size());

        Query query = QueryParser.parse(queryString);

        System.out.println(query);

        ArrayList<Condition> conds = query.getConditions();

        // fin du pretraitement
        HashMap<RdfNode, ArrayList<RdfEdge>> queryGraph = new HashMap<>();
        for (Condition cond : conds) {
            RdfEdge predicate = dico.getEdge(ds.getIndex(cond.getPredicate().stringValue())); // Return edge
            // (predicate) of a
            // condition
            RdfNode indexNode = dico.getNode(ds.getIndex(cond.getObject().stringValue())); // Return node
            // (object) of a condition
            queryGraph.computeIfAbsent(indexNode, k -> new ArrayList<>()).add(predicate);

            //ArrayList<RdfNode> subjects = indexNode.requestIndexStructure(predicate);

            /*if (results.isEmpty()) {
                results.addAll(subjects);
            } else {
                results.removeIf(result -> !subjects.contains(result));
            }*/
        }

        HashSet<TreeNode> results = manager.evaluate(queryGraph);
        ArrayList<String> finalResults = new ArrayList<>(results.size());
        for (TreeNode result : results) {
            finalResults.add(ds.getValue(result.getId()));
        }

        System.out.println("Resultat de la requete : " + finalResults);


    }

    /**
     * Indexes the given dataset
     *
     * @param filePath
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    private static void indexation(String filePath) throws IOException, RDFParseException, RDFHandlerException {
        Reader reader = new FileReader(filePath);

        RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
        rdfParser.setRDFHandler(new RDFListener());

        rdfParser.parse(reader, "");
        reader.close();

        dico.index();

        for (RdfNode rdfNode : dico.nodes.values()) {
            manager.add(rdfNode);
        }
    }

    private static class RDFListener extends RDFHandlerBase {

        @Override
        public void handleStatement(Statement st) {
            // System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t "
            // + st.getObject());
            // Transformer les valeurs en index (int)
            int indexSubject = ds.add(st.getSubject().stringValue());
            int indexPredicate = ds.add(st.getPredicate().stringValue());
            int indexObject = ds.add(st.getObject().stringValue());
            // Ajout des index au dictionnaire
            dico.add(indexSubject, indexPredicate, indexObject);
        }

    }

}
