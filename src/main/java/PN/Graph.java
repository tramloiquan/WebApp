package PN;

import CPN.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Stack;
import java.util.Iterator;
import java.util.Map;

public class Graph {
  private Node init;
  private HashMap<String, Node> nodes = new HashMap<String, Node>();

  public Graph(Node init) {
    this.init = init;
    this.nodes.put(init.getCode(), init);
  }

  public void add(String code, Node node) {
    this.nodes.put(code, node);
  }

  public Result search() {
    Stack<Node> stack = new Stack<Node>();
    stack.push(init);
    int num = 0;
    int count = 0;
    while (!stack.isEmpty()) {
      Node t = stack.pop();
      t.markVisited();
      num++;
      if (t.isCongesting()) {
        count++;
        if (count > 0) {
          // System.out.println(num);
          return new Result(num, t);
        }
      } else {
        for (Arc a: t.getChildren()) {
          Node n = a.getNode();
          if (!n.getVisited()) {
            stack.push(n);
          }
        }
      }
    }
    return new Result(num, null);
  }

  public Result searchHeuristic(HashMap<String, Integer> table) {
    clearState();
    Stack<Node> stack = new Stack<Node>();
    stack.push(init);
    int num = 0;
    int count = 0;
    while (!stack.isEmpty()) {
      Node t = stack.pop();
      t.markVisited();
      num++;
      if (t.isCongesting()) {
        count++;
        if (count > 0) {
          // System.out.println(num);
          return new Result(num, t);
        }
      } else {
        int size = t.getChildren().size();
        int[] heap = new int[size];
        int[] temp = new int[size];
        for (int i = 0; i < size; i++) {
          temp[i] = i;
        }
        for (int i = 0; i < size; i++) {
          Integer value = table.get(t.getChildren().get(i).getNode().getMarkingCode());
          if (value == null) {
            heap[i] = Constant.MAX;
          } else {
            heap[i] = value.intValue();
          }
        }
        for (int i = 0; i < size-1; i++) {
          for (int j = i+1; j < size; j++) {
            if (heap[i] < heap[j]) {
              int th = heap[i];
              heap[i] = heap[j];
              heap[j] = th;
              int tt = temp[i];
              temp[i] = temp[j];
              temp[j] = tt;
            }
          }
        }
        for (int i = 0; i < size; i++) {
          Node n = t.getChildren().get(temp[i]).getNode();
          if (!n.getVisited()) {
            stack.push(n);
          }
        }
      }
    }
    return new Result(num, null);
  }

  public void clearState() {
    ArrayList<Node> listNode = new ArrayList<Node>(nodes.values());
    for (Node n: listNode) {
      n.unmarkVisited();
    }
  }

  public ArrayList<Node> minimalGetCongesionList() {
    ArrayList<Node> res = new ArrayList<Node>();
    ArrayList<Node> listNode = new ArrayList<Node>(nodes.values());
    for (Node n: listNode) {
      if (n.isCongesting()) {
        res.add(n);
      }
    }
    return res;
  }

  public HashMap<String, Integer> minimalBuildHeuristicTalbe() {
    HashMap<String, Integer> res = new HashMap<String, Integer>();
    ArrayList<Node> listNode = new ArrayList<Node>(nodes.values());
    for (Node n: listNode) {
      res.put(n.getMarkingCode(), new Integer(Constant.MAX));
    }
    ArrayList<Node> list = minimalGetCongesionList();
    for (Node n: list) {
      res.put(n.getMarkingCode(), new Integer(0));
      Stack<Node> stack = new Stack<Node>();
      stack.push(n);
      while (!stack.isEmpty()) {
        Node t = stack.pop();
        t.markVisited();
        int y = res.get(t.getMarkingCode()).intValue();
        for (Arc a: t.getParents()) {
          Node p = a.getNode();
          int yp = res.get(p.getMarkingCode()).intValue();
          if (y+1 < yp) {
            res.put(p.getMarkingCode(), new Integer(y+1));
          }
          if (!p.getVisited()) {
            stack.push(p);
          }
        }
      }
    }
    return res;
  }

  public Node getInit() {
    return this.init;
  }

  public Node find(String s) {
    return (Node)this.nodes.get(s);
  }

  public int getSize(){return nodes.size();}
}
