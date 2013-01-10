package com.bowlong.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DFAFilter {
    /** ֱ�ӽ�ֹ�� */
    private HashMap keysMap = new HashMap();
    private int matchType = 1; // 1:��С����ƥ�� 2����󳤶�ƥ��

    public void addKeywords(List<String> keywords) {
        for (int i = 0; i < keywords.size(); i++) {
            String key = keywords.get(i).trim();
            HashMap nowhash = null;
            nowhash = keysMap;
            for (int j = 0; j < key.length(); j++) {
                char word = key.charAt(j);
                Object wordMap = nowhash.get(word);
                if (wordMap != null) {
                    nowhash = (HashMap) wordMap;
                } else {
                    HashMap<String, String> newWordHash = new HashMap<String, String>();
                    newWordHash.put("isEnd", "0");
                    nowhash.put(word, newWordHash);
                    nowhash = newWordHash;
                }
                if (j == key.length() - 1) {
                    nowhash.put("isEnd", "1");
                }
            }
        }
    }

    /**
     * ���ùؼ���
     */
    public void clearKeywords() {
        keysMap.clear();
    }

    /**
     * ���һ���ַ�����beginλ����ʼ�Ƿ���keyword���ϣ� ����з��ϵ�keywordֵ������ֵΪƥ��keyword�ĳ��ȣ����򷵻���
     * flag 1:��С����ƥ�� 2����󳤶�ƥ��
     */
    private int checkKeyWords(String txt, int begin, int flag) {
        HashMap nowhash = null;
        nowhash = keysMap;
        int maxMatchRes = 0;
        int res = 0;
        int l = txt.length();
        char word = 0;
        for (int i = begin; i < l; i++) {
            word = txt.charAt(i);
            Object wordMap = nowhash.get(word);
            if (wordMap != null) {
                res++;
                nowhash = (HashMap) wordMap;
                if (((String) nowhash.get("isEnd")).equals("1")) {
                    if (flag == 1) {
                        wordMap = null;
                        nowhash = null;
                        txt = null;
                        return res;
                    } else {
                        maxMatchRes = res;
                    }
                }
            } else {
                txt = null;
                nowhash = null;
                return maxMatchRes;
            }
        }
        txt = null;
        nowhash = null;
        return maxMatchRes;
    }

    /**
     * ����txt�йؼ��ֵ��б�
     */
    public Set<String> getTxtKeyWords(String txt) {
        Set set = new HashSet();
        int l = txt.length();
        for (int i = 0; i < l;) {
            int len = checkKeyWords(txt, i, matchType);
            if (len > 0) {
                set.add(txt.substring(i, i + len));
                i += len;
            } else {
                i++;
            }
        }
        txt = null;
        return set;
    }

    /**
     * ���ж�txt���Ƿ��йؼ���
     */
    public boolean isContentKeyWords(String txt) {
        for (int i = 0; i < txt.length(); i++) {
            int len = checkKeyWords(txt, i, 1);
            if (len > 0) {
                return true;
            }
        }
        txt = null;
        return false;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    public static void main(String[] args) {
        DFAFilter filter = new DFAFilter();
        List<String> keywords = new ArrayList<String>();
        keywords.add("�й���");
        keywords.add("�й�����");
        filter.addKeywords(keywords);
        String txt = "�й�����վ������";
        boolean boo = filter.isContentKeyWords(txt);
        System.out.println(boo);
        Set set = filter.getTxtKeyWords(txt);
        System.out.println(set);
    }
}