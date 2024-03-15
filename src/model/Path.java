package model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class Path {
    public final static int NULL_VALUE = -1;
    public static int count = 0;
    public static int upperBound;
    public static int lowerBound = 4;
    public static Random randomGenerator = new Random();
    public int maxLength;
    public Set<Integer> set;
    public LinkedList<Integer> sequence;
    public boolean isAtDeadend;
    public final int id;

    Path()
    {
        set = new HashSet<>();
        sequence = new LinkedList<>();
        isAtDeadend = false;
        id = ++Path.count;
        maxLength = 50;
//        setRandomLength();
    }

    public void add(int id)
    {
        set.add(id);
        sequence.addFirst(id);
    }

    public void remove()
    {
        set.remove(sequence.removeFirst());
    }

    public boolean contains(int id)
    {
        return set.contains(id);
    }

    public boolean isEmpty()
    {
        return sequence.isEmpty();
    }

    public int peek()
    {
        return (sequence.isEmpty())? NULL_VALUE : sequence.peekFirst();
    }

    public int peekSecond()
    {
        return (sequence.size() > 1)? sequence.get(1) : NULL_VALUE;
    }

    public void setRandomLength()
    {
        maxLength = randomGenerator.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public boolean isFull()
    {
        return sequence.size()==maxLength;
    }

    public boolean isTooSmall()
    {
        return sequence.size()<3;
    }

    public LinkedList<Integer> getSequence()
    {
        return sequence;
    }
}
