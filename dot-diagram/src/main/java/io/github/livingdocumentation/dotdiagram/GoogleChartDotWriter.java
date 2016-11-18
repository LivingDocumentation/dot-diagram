package io.github.livingdocumentation.dotdiagram;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class GoogleChartDotWriter extends AbstractDotWriter {

    private static final String GOOGLE_CHART_API = "http://chart.googleapis.com/chart";
    private final String path;

    /**
     * @param path
     *            The path to dot and png files, must end with a slash
     */
    public GoogleChartDotWriter(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getImageExtension() {
        return ".png";
    }

    @Override
    public void render(String filename) throws InterruptedException, IOException {
        String dot = read(path + filename + ".dot");
        HttpRequest httpRequest = HttpRequest.get(GOOGLE_CHART_API, true, "cht", "gv", "chl", dot);
        if (httpRequest.ok()) {
            try (InputStream is = httpRequest.stream()) {
                Files.copy(is, Paths.get(path + filename + getImageExtension()));
            }
        } else {
            throw new DotDiagramException("Errors calling Graphviz chart.googleapis.com");
        }
    }


    private static String read(String input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(input)))) {
            return buffer.lines()
                    .filter(l -> !l.trim().startsWith("//")) // ignore comment
                    .filter(l -> !l.trim().startsWith("#")) //ignore comment
                    .collect(Collectors.joining("\n"));
        }
    }

    public String toString() {
        return "GoogleChartDotWriter path=" + path + " imageExtension=" + getImageExtension();
    }
}
