package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Component {
    public int id;
    public int parent;
//    public LinkedList<Integer> children;
    public Set<Integer> vertices;
    public ArrayList<Path> paths;

    public Component(int id, int parent) {
        this.id = id;
        this.parent = parent;
//        this.children = new LinkedList<>();
        this.vertices = new HashSet<>();
        paths = new ArrayList<>();
    }
}
