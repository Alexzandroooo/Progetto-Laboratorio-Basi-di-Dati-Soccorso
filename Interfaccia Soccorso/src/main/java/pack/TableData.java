package pack;

import java.util.List;

public record TableData(String titolo, List<String> headers, List<List<String>> rows) {
    public boolean isEmpty() {
        return rows == null || rows.isEmpty();
    }
}