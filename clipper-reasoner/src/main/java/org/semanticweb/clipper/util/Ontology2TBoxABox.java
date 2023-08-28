package org.semanticweb.clipper.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;

public class Ontology2TBoxABox {

	OWLOntology ontology;

	private OWLOntology abox;

	private OWLOntology tbox;

	OWLOntologyManager manager;

	public Ontology2TBoxABox() {

	}

	public void extractFromDoc(String path) throws OWLOntologyCreationException {
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(path));

//		System.out.println("axioms count "+ontology.getAxiomCount());
		//System.out.println("TBox axioms "+ontology.getAxioms());

		extract(ontology);
	}
	public void extract(OWLOntology ontology) {
		this.ontology = ontology;
		this.manager = OWLManager.createOWLOntologyManager();
		try {

			//System.out.println("RBOX::"+ontology.getRBoxAxioms(Imports.EXCLUDED));

			Set<OWLAxiom> rbox = ontology.getRBoxAxioms(Imports.EXCLUDED);
			rbox.addAll(ontology.getTBoxAxioms(Imports.fromBoolean(false)));

			abox = manager.createOntology(ontology.getABoxAxioms(Imports.fromBoolean(false)));
			tbox = manager.createOntology(rbox);
			//manager.addAxioms(tbox, ontology.getRBoxAxioms(Imports.fromBoolean(false)));

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}

	public OWLOntology getAbox() {
		return abox;
	}

	public OWLOntology getTbox() {
		return tbox;
	}

	public static void main(String[] args) throws OWLOntologyCreationException,
			OWLOntologyStorageException, FileNotFoundException {
		if (args.length < 3) {
			System.out
					.println("Usage: Ontology2TBoxABox onto.owl tbox.owl abox.owl");
			System.exit(0);
		}

		File ontoFile = new File(args[0]);
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = man.loadOntologyFromOntologyDocument(ontoFile);

		Ontology2TBoxABox o2ta = new Ontology2TBoxABox();
		o2ta.extract(ontology);
		man.saveOntology(o2ta.getTbox(), new RDFXMLOntologyFormat(),
				new FileOutputStream(new File(args[1])));

		man.saveOntology(o2ta.getAbox(), new RDFXMLOntologyFormat(),
				new FileOutputStream(new File(args[2])));
	}
}
