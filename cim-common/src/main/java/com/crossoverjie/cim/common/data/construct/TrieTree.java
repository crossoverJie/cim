package com.crossoverjie.cim.common.data.construct;

import com.crossoverjie.cim.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Function: Trie tree prefix fuzzy matching
 *
 * @author crossoverJie
 *         Date: 2019/1/7 18:58
 * @since JDK 1.8
 */
public class TrieTree {

    /**
     * Supports both uppercase and lowercase
     */
    private static final int CHILDREN_LENGTH = 26 * 2;

    /**
     * Maximum string length stored
     */
    private static final int MAX_CHAR_LENGTH = 16;

    private static final char UPPERCASE_STAR = 'A';

    /**
     * Lowercase offset is -71
     */
    private static final char LOWERCASE_STAR = 'G';

    private Node root;

    public TrieTree() {
        root = new Node();
    }

    /**
     * Insert
     *
     * @param data
     */
    public void insert(String data) {
        this.insert(this.root, data);
    }

    private void insert(Node root, String data) {
        char[] chars = data.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            int index;
            if (Character.isUpperCase(aChar)) {
                index = aChar - UPPERCASE_STAR;
            } else {
                //Lowercase offset is -71
                index = aChar - LOWERCASE_STAR;
            }


            if (index >= 0 && index < CHILDREN_LENGTH) {
                if (root.children[index] == null) {
                    Node node = new Node();
                    root.children[index] = node;
                    root.children[index].data = chars[i];

                }

                //Set end flag for last character
                if (i + 1 == chars.length) {
                    root.children[index].isEnd = true;
                }

                //Move to next node
                root = root.children[index];
            }

        }
    }


    /**
     * Recursive depth traversal
     *
     * @param key
     * @return
     */
    public List<String> prefixSearch(String key) {
        List<String> value = new ArrayList<String>();
        if (StringUtil.isEmpty(key)) {
            return value;
        }

        char k = key.charAt(0);
        int index;
        if (Character.isUpperCase(k)) {
            index = k - UPPERCASE_STAR;
        } else {
            index = k - LOWERCASE_STAR;

        }
        if (root.children != null && root.children[index] != null) {
            return query(root.children[index], value,
                    key.substring(1), String.valueOf(k));
        }
        return value;
    }

    private List<String> query(Node child, List<String> value, String key, String result) {

        if (child.isEnd && key == null) {
            value.add(result);
        }
        if (StringUtil.isNotEmpty(key)) {
            char ca = key.charAt(0);

            int index;
            if (Character.isUpperCase(ca)) {
                index = ca - UPPERCASE_STAR;
            } else {
                index = ca - LOWERCASE_STAR;

            }

            if (child.children[index] != null) {
                query(child.children[index], value, key.substring(1).equals("") ? null : key.substring(1), result + ca);
            }
        } else {
            for (int i = 0; i < CHILDREN_LENGTH; i++) {
                if (child.children[i] == null) {
                    continue;
                }

                int j;
                if (Character.isUpperCase(child.children[i].data)) {
                    j = UPPERCASE_STAR + i;
                } else {
                    j = LOWERCASE_STAR + i;
                }

                char temp = (char) j;
                query(child.children[i], value, null, result + temp);
            }
        }

        return value;
    }


    /**
     * Get all entries
     *
     * @return
     */
    public List<String> all() {
        char[] chars = new char[MAX_CHAR_LENGTH];
        List<String> value = depth(this.root, new ArrayList<String>(), chars, 0);
        return value;
    }


    public List<String> depth(Node node, List<String> list, char[] chars, int index) {
        if (node.children == null || node.children.length == 0) {
            return list;
        }

        Node[] children = node.children;

        for (int i = 0; i < children.length; i++) {
            Node child = children[i];

            if (child == null) {
                continue;
            }

            if (child.isEnd) {
                chars[index] = child.data;

                char[] temp = new char[index + 1];
                for (int j = 0; j < chars.length; j++) {
                    if (chars[j] == 0) {
                        continue;
                    }

                    temp[j] = chars[j];
                }
                list.add(String.valueOf(temp));
                return list;
            } else {
                chars[index] = child.data;

                index++;

                depth(child, list, chars, index);

                index = 0;
            }
        }


        return list;
    }


    /**
     * Trie tree node
     */
    private class Node {
        /**
         * Whether this is the last character
         */
        public boolean isEnd = false;

        /**
         * Data storage is not needed for query-only nodes
         */
        public char data;

        public Node[] children = new Node[CHILDREN_LENGTH];

    }
}
