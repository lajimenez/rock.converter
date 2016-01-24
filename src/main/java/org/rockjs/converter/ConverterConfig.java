package org.rockjs.converter;

import java.io.File;

import org.rockjs.ConverterException;
import org.rockjs.converter.loader.obj.OBJLoader;

public class ConverterConfig {

    public static String PARAM_INPUT_FORMAT = "-f";
    
    public static String PARAM_INPUT_MODEL_FILE = "-i";
    
    public static String PARAM_OUTPUT_RESULT_FILE = "-o";
    
    public static String PARAM_COMPUTE_NORMALS = "-c";
    
    public static String PARAM_REPARE_NORMALS = "-r";

    public static String PARAM_PREFIX = "-";
    
    private String format = null;

    private File modelFile = null;

    private File resultFile = null;

    private boolean computeNormals = false;

    private boolean repairNormals = false;

    public void initFromArguments(String[] args) throws ConverterException {
        parseParams(args);
        validateParams();
    }
    
    private void parseParams(String[] args) throws ConverterException {
        for (int i = 0; i < args.length; i++) {
            String param = args[i];
            String paramOption;
            
            if (PARAM_INPUT_MODEL_FILE.equals(param)) {
                i++;
                paramOption = getParamValue(i, args);
                modelFile = new File(paramOption);
            } else if (PARAM_OUTPUT_RESULT_FILE.equals(param)) {
                i++;
                paramOption = getParamValue(i, args);
                resultFile = new File(paramOption);
            } else if (PARAM_INPUT_FORMAT.equals(param)) {
                i++;
                format = getParamValue(i, args);
            } else if (PARAM_COMPUTE_NORMALS.equals(param)) {
                computeNormals = true;
            } else if (PARAM_REPARE_NORMALS.equals(param)) {
                repairNormals = true;
            } else {
                throw new ConverterException("Invalid option");
            }
        }
    }
    
    private void validateParams() throws ConverterException {
        if (modelFile == null) {
            throw new ConverterException("Not input file specified");
        }
        
        if (resultFile == null) {
            throw new ConverterException("Not result file specified");
        }
        
        if (format == null) {
            throw new ConverterException("No input format specified");
        }
        
        if (!OBJLoader.FORMAT.equalsIgnoreCase(format)) {
            throw new ConverterException("Only input format supported is OBJ");
        }
        
        if (!modelFile.exists()) {
            throw new ConverterException("File with the 3d model to convert doesn't exists");
        }

        // If the result file exits, it will be deleted
        if (resultFile.exists()) {
            resultFile.delete();
        }
    }
    
    private String getParamValue(int position, String[] args) throws ConverterException {
        if (position >= args.length) {
            throw new ConverterException("There is no param value");
        }
        
        String paramValue = args[position];
        if (paramValue.startsWith(PARAM_PREFIX)) {
            throw new ConverterException("Invalid param value");
        }
        return paramValue;        
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    public File getModelFile() {
        return modelFile;
    }

    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public File getResultFile() {
        return resultFile;
    }

    public void setResultFile(File resultFile) {
        this.resultFile = resultFile;
    }

    public boolean isComputeNormals() {
        return computeNormals;
    }

    public void setComputeNormals(boolean computeNormals) {
        this.computeNormals = computeNormals;
    }
    
    public boolean isRepairNormals() {
        return repairNormals;
    }

    public void setRepairNormals(boolean repairNormals) {
        this.repairNormals = repairNormals;
    }
}
