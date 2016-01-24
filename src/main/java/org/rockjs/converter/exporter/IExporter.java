package org.rockjs.converter.exporter;

import java.util.List;

import org.rockjs.ConverterException;
import org.rockjs.converter.ConverterConfig;
import org.rockjs.model.Model;

/**
 * An exporter is the responsible to export a model to an specific format
 * 
 * @author Luis Alberto Jiménez
 */
public interface IExporter {
    
    /**
     * Export the model to JSON rock format and store in file
     * @param models
     *      list of the model to export
     *  * @param config
     *      configuration used to export the model
     */
    void export(List<Model> models, ConverterConfig config) throws ConverterException;
}
