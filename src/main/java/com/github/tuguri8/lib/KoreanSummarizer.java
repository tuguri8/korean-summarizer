package com.github.tuguri8.lib;

import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanSentenceSplitter;
import org.openkoreantext.processor.tokenizer.Sentence;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by tuguri8@gmail.com on 2019. 8. 25.
 * Github : https://github.com/tuguri8
 */

public class KoreanSummarizer {

    public KoreanSummarizer() {
    }

    // Get Top 5 Keywords from Text
    public List<String> getKeywords(String text) {
        List<String> splittedSentences = splitParagraph(text);
        List<List<String>> taggedSentenceList = splittedSentences.stream()
                                                                 .map(this::tagging)
                                                                 .collect(Collectors.toList());
        return getKeywordListFromNews(taggedSentenceList);
    }

    // Summarize Text to 3 sentences
    public String summarize(String text) {
        List<String> splittedSentences = splitParagraph(text);
        List<List<String>> taggedSentenceList = splittedSentences.stream()
                                                                 .map(this::tagging)
                                                                 .collect(Collectors.toList());
        List<String> keywordList = getKeywordListFromNews(taggedSentenceList);

        HashMap<Integer, Double> tfIdfMap = new HashMap<>();
        for (List<String> sentenceTags : taggedSentenceList) {
            Double resulTfIdf = taggedSentenceList.indexOf(sentenceTags) == 0 ? 0.6 : 0.0;
            for (String keyword : keywordList) {
                Long tf = getTf(sentenceTags, keyword);
                Double idf = getIdf(taggedSentenceList, keyword);
                Double tfIdf = tf * idf;
                resulTfIdf += tfIdf;
            }
            tfIdfMap.put(taggedSentenceList.indexOf(sentenceTags), resulTfIdf);
        }

        // Sorting MAP DESC
        Map<Integer, Double> sortedMap = tfIdfMap.entrySet().stream()
                                                 .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                 .collect(Collectors.toMap(Map.Entry::getKey,
                                                                           Map.Entry::getValue,
                                                                           (e1, e2) -> e1,
                                                                           LinkedHashMap::new));

        List<Integer> sortedIndexSentence = new ArrayList<>(sortedMap.keySet());
        return sortedIndexSentence.size() > 2 ? String.format("%s %s %s",
                                                              splittedSentences.get(sortedIndexSentence.get(0)),
                                                              splittedSentences.get(sortedIndexSentence.get(1)),
                                                              splittedSentences.get(sortedIndexSentence.get(2)))
            : splittedSentences.toString();
    }

    // Extract 5 keywords via TF-IDF
    private List<String> getKeywordListFromNews(List<List<String>> taggedSentenceList) {
        HashMap<String, Double> tfIdfMap = new HashMap<>();
        for (List<String> sentenceTags : taggedSentenceList) {
            for (String keyword : sentenceTags) {
                Long tf = getTf(sentenceTags, keyword);
                Double idf = getIdf(taggedSentenceList, keyword);
                Double tfIdf = tf * idf;
                if (tfIdfMap.containsKey(keyword)) {
                    if (tfIdfMap.get(keyword).compareTo(tfIdf) > 0) { continue; }
                }
                tfIdfMap.put(keyword, tf * idf);
            }
        }

        // Sorting MAP DESC
        Map<String, Double> sortedMap = tfIdfMap.entrySet().stream()
                                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                .collect(Collectors.toMap(Map.Entry::getKey,
                                                                          Map.Entry::getValue,
                                                                          (e1, e2) -> e1,
                                                                          LinkedHashMap::new));

        return sortedMap.keySet()
                        .stream()
                        .limit(5)
                        .collect(Collectors.toList());
    }

    // Calculate TF
    private Long getTf(List<String> sentence, String keyword) {
        return sentence.stream().filter(word -> word.equals(keyword)).count();
    }

    // Calculate IDF
    private Double getIdf(List<List<String>> tagList, String keyword) {
        int df = 1;
        df += tagList.stream().filter(sentence -> sentence.stream().anyMatch(word -> word.equals(keyword))).count();
        return Math.log10((double) tagList.size() / df);
    }

    // Extract Noun( > 2 words) from Sentence
    private List<String> tagging(String text) {
        Seq tokens = OpenKoreanTextProcessorJava.tokenize(text);
        List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
        return tokenList.stream()
                        .filter(token -> token.getPos().equals(KoreanPosJava.Noun) && token.getLength() > 1)
                        .map(KoreanTokenJava::getText)
                        .collect(Collectors.toList());
    }

    // Split Paragraph into Sentences Using OKT
    private List<String> splitParagraph(String newsText) {
        List<Sentence> splittedSentences = scala.collection.JavaConversions.seqAsJavaList(KoreanSentenceSplitter.split(newsText));
        return splittedSentences.stream()
                                .map(Sentence::text)
                                .collect(Collectors.toList());
    }
}
