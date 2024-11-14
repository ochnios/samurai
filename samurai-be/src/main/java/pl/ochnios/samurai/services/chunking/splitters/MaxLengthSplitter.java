package pl.ochnios.samurai.services.chunking.splitters;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentTransformer;

public class MaxLengthSplitter implements DocumentTransformer {

    private final int maxLength;

    public MaxLengthSplitter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public List<Document> apply(List<Document> documents) {
        return transform(documents);
    }

    @Override
    public List<Document> transform(List<Document> documents) {
        List<Document> result = new ArrayList<>();

        for (Document doc : documents) {
            if (doc.getContent().length() <= maxLength) {
                result.add(doc);
            } else {
                splitDocument(doc, maxLength, result);
            }
        }

        return result;
    }

    private void splitDocument(Document doc, int maxLength, List<Document> result) {
        String content = doc.getContent();

        while (content.length() > maxLength) {
            int splitIndex = findSplitIndex(content, maxLength);
            String firstPart = content.substring(0, splitIndex);
            content = content.substring(splitIndex).trim();

            result.add(new Document(firstPart));
        }

        if (!content.isEmpty()) {
            result.add(new Document(content));
        }
    }

    private int findSplitIndex(String content, int maxLength) {
        int idealIndex = Math.min(maxLength, content.length());

        // Try to find the last occurrence of newline, period, or space
        int lastNewline = content.lastIndexOf('\n', idealIndex);
        int lastPeriod = content.lastIndexOf('.', idealIndex);
        int lastSpace = content.lastIndexOf(' ', idealIndex);

        // Use the closest split point that's not too far back
        int threshold = (int) (maxLength * 0.85);

        if (lastNewline > threshold) return lastNewline + 1; // +1 to include the newline in first part
        if (lastPeriod > threshold) return lastPeriod + 1; // +1 to include the period in first part
        if (lastSpace > threshold) return lastSpace;

        return idealIndex;
    }
}
