package gui;

import pack.TableData;
import java.util.List;

public class TextPresenter {
    private static final int MAX_COL_WIDTH = 35; // Tronca testi a video troppo lunghi

    public static String format(TableData data) {
        if (data == null || data.isEmpty()) {
            return "Nessun dato trovato per: " + (data != null ? data.titolo() : "Query") + "\n";
        }
        
        StringBuilder sb = new StringBuilder();
        if (data.titolo() != null && !data.titolo().isEmpty()) {
            sb.append("=== ").append(data.titolo()).append(" ===\n");
        }
        
        int numCols = data.headers().size();
        int[] colWidths = new int[numCols];
        
        // Calcola larghezze dinamiche basate sul contenuto (tenendo conto del max width)
        for (int i = 0; i < numCols; i++) {
            colWidths[i] = Math.min(data.headers().get(i).length(), MAX_COL_WIDTH);
            for (List<String> row : data.rows()) {
                String cell = row.get(i);
                int len = cell != null ? cell.length() : 3;
                if (len > colWidths[i]) colWidths[i] = Math.min(len, MAX_COL_WIDTH);
            }
        }
        
        // Helper interno per stampare celle sicure e allineate
        java.util.function.BiConsumer<String, Integer> printCell = (testo, width) -> {
            if (testo == null) testo = "N/A";
            if (testo.length() > width) testo = testo.substring(0, width - 3) + "...";
            sb.append(String.format("%-" + width + "s", testo));
        };

        // Costruzione Intestazione
        for (int i = 0; i < numCols; i++) {
            printCell.accept(data.headers().get(i), colWidths[i]);
            if (i < numCols - 1) sb.append(" | ");
        }
        sb.append("\n");
        
        // Separatore
        int totalLen = 0;
        for (int w : colWidths) totalLen += w;
        totalLen += (numCols - 1) * 3;
        sb.append("-".repeat(totalLen)).append("\n");
        
        // Costruzione Righe
        for (List<String> row : data.rows()) {
            for (int i = 0; i < numCols; i++) {
                printCell.accept(row.get(i), colWidths[i]);
                if (i < numCols - 1) sb.append(" | ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String formatMulti(TableData... dataList) {
        StringBuilder sb = new StringBuilder();
        for (TableData d : dataList) {
            sb.append(format(d)).append("\n");
        }
        return sb.toString();
    }
}
