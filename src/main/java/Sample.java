import com.eclipsesource.v8.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Sample {

    private static String JIMP_SCRIPT = "var pdfParser = require('C:/Users/bean/Workspaces/GitHub/pdf-reader/src/main/java/node_modules/pdf-parser');\n" +
            "var PDF_PATH = 'C:/Users/bean/Workspaces/GitHub/pdf-reader/target/classes/test.pdf';\n" +
            "pdfParser.pdf2json(PDF_PATH, function (error, pdf) {\n" +
            "    if(error != null){\n" +
            "        process_in_java(error);\n" +
            "    }else{\n" +
            "        process_in_java(JSON.stringify(pdf));\n" +
            "    }\n" +
            "})";

    /*public static void main(String[] args) throws IOException {

        final NodeJS nodeJS = NodeJS.createNodeJS();

        V8 runtime = nodeJS.getRuntime();

        runtime.registerJavaMethod((V8Object receiver, V8Array parameters) -> {
            Object o = parameters.get(0);
            Object d = receiver.get("pages");
            System.out.println(o.toString());
            return null;
        }, "process_in_java");

        File script = createTemporaryScriptFile(JIMP_SCRIPT, "jimpscript.js");

        String path = Sample.class.getClassLoader().getResource("test.pdf").getPath();

        nodeJS.exec(script);

        while(nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
        nodeJS.release();
    }*/

    public static void main(String[] args) throws IOException {
        final NodeJS nodeJS = NodeJS.createNodeJS();
        final V8Object pafParser = nodeJS.require(new File("C:/Users/bean/Workspaces/GitHub/pdf-reader/src/main/resources/pdfparser.js"));
        V8 runtime = nodeJS.getRuntime();
        V8Function callback = new V8Function(runtime, (receiver, parameters) -> {
            Object o = parameters.get(0);
            System.out.println(o.toString());
            return null;
        });

        /*runtime.registerJavaMethod((V8Object receiver, V8Array parameters) -> {
            Object o = parameters.get(0);
            Object d = receiver.get("pages");
            System.out.println(o.toString());
            return null;
        }, "process_in_java");*/

        pafParser.executeJSFunction("getJson", "C:/Users/bean/Workspaces/GitHub/pdf-reader/target/classes/test.pdf",callback);
        //executeJSFunction(pafParser,"getJson", "C:/Users/bean/Workspaces/GitHub/pdf-reader/target/classes/test.pdf", callback);

        while(nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
        callback.release();
        pafParser.release();
        nodeJS.release();
    }


    public static void executeJSFunction(V8Object object, String name, String path, Object params) {
        Object result = object.executeJSFunction(name,path, params);
        if (result instanceof Releasable) {
            ((Releasable) result).release();
        }
    }

    private static File createTemporaryScriptFile(final String script, final String name) throws IOException {
        File tempFile = File.createTempFile(name, ".js");
        PrintWriter writer = new PrintWriter(tempFile, "UTF-8");
        try {
            writer.print(script);
        } finally {
            writer.close();
        }
        return tempFile;
    }
}