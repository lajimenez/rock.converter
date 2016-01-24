package org.rockjs;

import org.rockjs.converter.Converter;
import org.rockjs.converter.ConverterConfig;
import org.rockjs.converter.exporter.IExporter;
import org.rockjs.converter.exporter.JSONRockExporter;
import org.rockjs.converter.loader.ILoader;
import org.rockjs.converter.loader.obj.OBJLoader;

/**
 * Command line application main class
 * 
 * @author Luis Alberto Jiménez
 */
public class CommandLineApp {

    public static void main(String[] args) {
        try {
            ConverterConfig config = new ConverterConfig();
            config.initFromArguments(args);
            
            ILoader loader = getLoader(config.getFormat());
            IExporter exporter = new JSONRockExporter();
            Converter converter = new Converter(loader, exporter);

            converter.convert(config);
        } catch (ConverterException ce) {
            Logger.log(ce.getMessage());
        }
    }

    public static ILoader getLoader(String format) {
        if (OBJLoader.FORMAT.equalsIgnoreCase(format)) {
            return new OBJLoader();
        }

        return null;
    }
}
