package vocab;

import org.semanticweb.owlapi.model.IRI;

public class UniverityOntology {

    public final static String BASE = "http://example.org/";

    public final static IRI Professor = IRI.create(BASE+"onto/Professor");
    public final static IRI Course = IRI.create(BASE+"onto/Course");
    public final static IRI teaches = IRI.create(BASE+"onto/teaches");
}
