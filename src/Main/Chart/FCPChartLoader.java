// Language: java
// File: src/Main/Chart/FCPChartLoader.java
package Main.Chart;

import Main.Java.Notes.NoteEvent;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class FCPChartLoader {

    // Carrega um chart a partir do caminho do arquivo FCPXML e retorna uma lista de NoteEvent
    public static ArrayList<NoteEvent> load(String path) {
        ArrayList<NoteEvent> chart = new ArrayList<>(); // Lista que será retornada contendo os eventos de nota

        try {
            File file = new File(path); // Arquivo no sistema de arquivos apontando para o FCPXML

            // Cria o parser de XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file); // Faz o parse do arquivo

            doc.getDocumentElement().normalize(); // Normaliza o documento (boa prática antes de navegar pelo DOM)

            // Seleciona todos os elementos <marker> que representam pontos no tempo do vídeo/áudio
            NodeList markers = doc.getElementsByTagName("marker");

            // Itera sobre cada marker encontrado no XML
            for (int i = 0; i < markers.getLength(); i++) {
                Element marker = (Element) markers.item(i);

                String start = marker.getAttribute("start"); // Obtém o atributo 'start' que indica o tempo

                long timeMs = convertToMillis(start); // Converte o tempo do formato do FCPXML para milissegundos

                // Lane aleatória por enquanto (0 a 3) — aqui decide em qual pista a nota aparecerá
                int lane = i % 4;

                // Adiciona o evento de nota ao chart com o tempo em ms e a lane escolhida
                chart.add(new NoteEvent(timeMs));
            }

        } catch (Exception e) {
            // Em caso de erro ao ler/parsar, imprime stack trace — mantém comportamento semelhante ao original
            e.printStackTrace();
        }

        return chart; // Retorna a lista de eventos (pode estar vazia se ocorreu erro)
    }

    // Converte uma string do formato usado no FCPXML (ex: "123/60000s") para milissegundos
    private static long convertToMillis(String start) {

        start = start.replace("s", ""); // Remove o 's' ao final do valor quando presente

        String[] parts = start.split("/"); // Divide numerador e denominador (numerator/denominator)

        double numerator = Double.parseDouble(parts[0]); // Parte superior da fração
        double denominator = Double.parseDouble(parts[1]); // Parte inferior da fração

        double seconds = numerator / denominator; // Calcula os segundos a partir da fração

        return (long) (seconds * 1000); // Converte segundos para milissegundos e retorna
    }
}
