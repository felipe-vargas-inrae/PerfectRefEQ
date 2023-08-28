
import com.beust.jcommander.Parameter;

public class MainArgs {

    @Parameter(
            names = "--config",
            description = "A valid JSON with the experiment information",
            required = true
    )
    private String config;

    public String getConfig() {
        return  config;
    }

//    //----------------------------------------------------------------------------
//    @Parameter(
//            names = "--unzip",
//            description = "A flag to indicate that outputs should not be compressed",
//            required = false
//    )
//    private boolean unzipFlag = false;
//
//    public boolean getUnzipFlag() {
//        return  unzipFlag;
//    }
//    //----------------------------------------------------------------------------
//
//    @Parameter(
//            names = "--nestedANR",
//            description = "A flag to indicate that nested ANRs should be taken into account",
//            required = false
//    )
//    private boolean nestedANR = false;
//
//    public boolean getNestedANRFlag() {
//        return  nestedANR;
//    }
//    //--------------------------------------------------------------------------------
//    @Parameter(
//            names = "--inferences",
//            description = "A flag to indicate RDFS entailment should be taken into account for inferences",
//            required = false
//    )
//    private boolean inferences = false;
//
//    public boolean getInferencesFlag() {
//        return  inferences;
//    }
}
