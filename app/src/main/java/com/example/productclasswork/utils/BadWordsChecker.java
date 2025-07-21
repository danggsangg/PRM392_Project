package com.example.productclasswork.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BadWordsChecker {
    private static final String TAG = "BadWordsChecker";
    private Set<String> badWords = new HashSet<>();

    public BadWordsChecker(Context context) {
        Log.d(TAG, "Initializing BadWordsChecker...");
        loadBadWords(context, "badwords1.json");
        loadBadWords(context, "badwords2.json");
        Log.d(TAG, "Total bad words loaded: " + badWords.size());
        // In ra một số từ đầu tiên để kiểm tra
        int count = 0;
        for (String word : badWords) {
            if (count < 5) {
                Log.d(TAG, "Sample bad word: " + word);
                count++;
            } else {
                break;
            }
        }
    }

    private void loadBadWords(Context context, String fileName) {
        Log.d(TAG, "Loading bad words from: " + fileName);
        try {
            // Đọc file từ assets
            InputStream is = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            is.close();

            String jsonContent = jsonString.toString();
            Log.d(TAG, "JSON Content length: " + jsonContent.length());
            Log.d(TAG, "First 100 chars of JSON: " + jsonContent.substring(0, Math.min(100, jsonContent.length())));

            // Parse JSON object thay vì array
            JSONObject jsonObject = new JSONObject(jsonContent);
            Iterator<String> keys = jsonObject.keys();
            int wordsCount = 0;
            while (keys.hasNext()) {
                String word = keys.next().toLowerCase();
                badWords.add(word);
                wordsCount++;
                Log.d(TAG, "Added bad word: " + word);
            }
            Log.d(TAG, "Loaded " + wordsCount + " bad words from " + fileName);
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error loading bad words from " + fileName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) return false;
        
        Log.d(TAG, "Checking text for bad words: " + text);
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            Log.d(TAG, "Checking word: " + word);
            if (badWords.contains(word)) {
                Log.d(TAG, "Found bad word: " + word);
                return true;
            }
        }
        Log.d(TAG, "No bad words found");
        return false;
    }

    public String findFirstBadWord(String text) {
        if (text == null || text.isEmpty()) return null;
        
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            if (badWords.contains(word)) {
                Log.d(TAG, "Found first bad word: " + word);
                return word;
            }
        }
        return null;
    }
} 