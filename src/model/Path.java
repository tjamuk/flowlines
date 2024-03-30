package model;

import java.util.*;

public class Path {
    public final static int NULL_VALUE = -1;
    public final static int MAX_ADJACENT_TURNS = 2;
    public static int count = 0;
    public static int upperBound;
    public static int lowerBound = 4;
    public static Random randomGenerator = new Random();
    public int maxLength;
    public Set<Integer> set;
    public LinkedList<Integer> sequence;
    public LinkedList<Boolean> listOfTurns;
    public boolean isAtDeadend;
    public final int id;

    Path()
    {
        set = new HashSet<>();
        sequence = new LinkedList<>();
        listOfTurns = new LinkedList<>();
        isAtDeadend = false;
        id = ++Path.count;
        maxLength = 50;
//        setRandomLength();
    }

    public void add(int id, boolean isTurn)
    {
        set.add(id);
        sequence.addFirst(id);
        if (sequence.size() > 2)
        {
            listOfTurns.addFirst(isTurn);
        }
    }

    public int getTurnCount()
    {
        int counter = 0;
        for (boolean isTurn : listOfTurns)
        {
            if (isTurn) {
                counter++;
            }
        }
        return counter;
    }
    public void remove()
    {
        set.remove(sequence.removeFirst());
        if (!listOfTurns.isEmpty())
        {
            listOfTurns.removeFirst();
        }
    }

    public boolean areDirectionsValid()
    {
        if (listOfTurns.size() <= MAX_ADJACENT_TURNS)
        {
//            System.out.println("listOfTurns too small");
            return true;
        }

        Iterator<Boolean> iterator = listOfTurns.iterator();

        for (int i = 0; i <= MAX_ADJACENT_TURNS; i++)
        {
            if (!iterator.next())
            {
//                System.out.println(listOfTurns);
                return true;
            }
        }
//        System.out.println("Too many turns");
        return false;
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

    public int peekLast()
    {
        return (sequence.size() > 1)? sequence.getLast() : NULL_VALUE;
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
