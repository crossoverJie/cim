package com.crossoverjie.cim.common.data.construct;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TrieTreeTest {
    @Test
    public void insert() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("abc");
        trieTree.insert("abcd");
    }


    @Test
    public void all() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("ABC");
        trieTree.insert("abC");
        List<String> all = trieTree.all();
        String result = "";
        for (String s : all) {
            result += s + ",";
            System.out.println(s);
        }

        Assert.assertTrue("ABC,abC,".equals(result));

    }
    @Test
    public void all2() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("abc");
        trieTree.insert("abC");
        List<String> all = trieTree.all();
        String result = "";
        for (String s : all) {
            result += s + ",";
            System.out.println(s);
        }

        //Assert.assertTrue("ABC,abC,".equals(result));

    }

    @Test
    public void prefixSea() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("java");
        trieTree.insert("jsf");
        trieTree.insert("jsp");
        trieTree.insert("javascript");
        trieTree.insert("php");

        String result ="";
        List<String> ab = trieTree.prefixSearch("jav");
        for (String s : ab) {
            result += s+",";
            System.out.println(s);
        }

        Assert.assertTrue(result.equals("java,javascript,"));

    }
    @Test
    public void prefixSea2() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("java");
        trieTree.insert("jsf");
        trieTree.insert("jsp");
        trieTree.insert("javascript");
        trieTree.insert("php");

        String result ="";
        List<String> ab = trieTree.prefixSearch("j");
        for (String s : ab) {
            result += s+",";
            System.out.println(s);
        }

        Assert.assertTrue(result.equals("java,javascript,jsf,jsp,"));

    }
    @Test
    public void prefixSea3() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("java");
        trieTree.insert("jsf");
        trieTree.insert("jsp");
        trieTree.insert("javascript");
        trieTree.insert("php");

        String result ="";
        List<String> ab = trieTree.prefixSearch("js");
        for (String s : ab) {
            result += s+",";
            System.out.println(s);
        }

        Assert.assertTrue(result.equals("jsf,jsp,"));

    }
    @Test
    public void prefixSea4() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("java");
        trieTree.insert("jsf");
        trieTree.insert("jsp");
        trieTree.insert("javascript");
        trieTree.insert("php");

        String result ="";
        List<String> ab = trieTree.prefixSearch("jav");
        for (String s : ab) {
            result += s+",";
            System.out.println(s);
        }

        Assert.assertTrue(result.equals("java,javascript,"));

    }
    @Test
    public void prefixSea5() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("java");
        trieTree.insert("jsf");
        trieTree.insert("jsp");
        trieTree.insert("javascript");
        trieTree.insert("php");

        String result ="";
        List<String> ab = trieTree.prefixSearch("js");
        for (String s : ab) {
            result += s+",";
            System.out.println(s);
        }

        Assert.assertTrue(result.equals("jsf,jsp,"));

    }

    @Test
    public void prefixSearch() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("abc");
        trieTree.insert("abd");
        trieTree.insert("ABe");

        List<String> ab = trieTree.prefixSearch("AB");
        for (String s : ab) {
            System.out.println(s);
        }

        System.out.println("========");

        //char[] chars = new char[3] ;
        //for (int i = 0; i < 3; i++) {
        //    int a = 97 + i ;
        //    chars[i] = (char) a ;
        //}
        //
        //String s = String.valueOf(chars);
        //System.out.println(s);
    }

    @Test
    public void prefixSearch2() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");

        List<String> ab = trieTree.prefixSearch("AC");
        for (String s : ab) {
            System.out.println(s);
        }
        Assert.assertTrue(ab.size() == 0);
    }

    @Test
    public void prefixSearch3() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");

        List<String> ab = trieTree.prefixSearch("CD");
        for (String s : ab) {
            System.out.println(s);
        }
        Assert.assertTrue(ab.size() == 1);
    }

    @Test
    public void prefixSearch4() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");

        List<String> ab = trieTree.prefixSearch("Cd");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals("Cde,"));
    }
    @Test
    public void prefixSearch44() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("a");
        trieTree.insert("b");
        trieTree.insert("c");
        trieTree.insert("d");
        trieTree.insert("e");
        trieTree.insert("f");
        trieTree.insert("g");
        trieTree.insert("h");

    }

    @Test
    public void prefixSearch5() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");
        trieTree.insert("CDfff");
        trieTree.insert("Cdfff");

        List<String> ab = trieTree.prefixSearch("Cd");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals("Cde,Cdfff,"));
    }

    @Test
    public void prefixSearch6() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");
        trieTree.insert("CDfff");
        trieTree.insert("Cdfff");

        List<String> ab = trieTree.prefixSearch("CD");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals("CDa,CDfff,"));
    }

    @Test
    public void prefixSearch7() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");
        trieTree.insert("CDfff");
        trieTree.insert("Cdfff");

        List<String> ab = trieTree.prefixSearch("");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals(""));
    }

    @Test
    public void prefixSearch8() throws Exception {
        TrieTree trieTree = new TrieTree();

        List<String> ab = trieTree.prefixSearch("");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals(""));
    }


    @Test
    public void prefixSearch9() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("Cde");
        trieTree.insert("CDa");
        trieTree.insert("ABe");
        trieTree.insert("CDfff");
        trieTree.insert("Cdfff");

        List<String> ab = trieTree.prefixSearch("CDFD");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals(""));
    }


    @Test
    public void prefixSearch10() throws Exception {
        TrieTree trieTree = new TrieTree();
        trieTree.insert("crossoverJie");
        trieTree.insert("zhangsan");

        List<String> ab = trieTree.prefixSearch("c");
        String result = "";
        for (String s : ab) {
            result += s + ",";
            System.out.println(s);
        }
        Assert.assertTrue(result.equals("crossoverJie,"));
    }

}