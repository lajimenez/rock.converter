package org.rockjs.converter;

import java.util.List;

import org.rockjs.ConverterException;
import org.rockjs.Logger;
import org.rockjs.converter.exporter.IExporter;
import org.rockjs.converter.loader.ILoader;
import org.rockjs.model.Model;

/**
 * This class is responsible for converting from on 3d format to rock format
 * 
 * @author Luis Alberto Jiménez
 */
public class Converter {

    private ILoader loader = null;
    private IExporter exporter = null;
    
    public Converter(ILoader loader, IExporter exporter) {
        this.loader = loader;
        this.exporter = exporter;
    }
    
    /**
     * Do conversion between formats
     * 
     * @param config
     *      configuration used to convert the model
     *
     * @throws ConverterException
     */
    public void convert(ConverterConfig config) throws ConverterException {
        List<Model> models = loader.loadModels(config);
        exporter.export(models, config);
        Logger.log("Model exported successfully");
    }
}
