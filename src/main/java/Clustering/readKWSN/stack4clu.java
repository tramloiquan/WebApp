package Clustering.readKWSN;
import java.util.NoSuchElementException;

public class stack4clu<Sensor> {
	 	private int n;          // size of the stack
	    private Node first;     // top of stack

	    // helper linked list class
	    private class Node {
	    private Sensor sensor;
	    private Node next;
	    }

	   /**
	     * Initializes an empty stack.
	     */
	    public stack4clu() {
	        first = null;
	        n = 0;
	    }

	    /**
	     * Returns true if this stack is empty.
	     *
	     * @return true if this stack is empty; false otherwise
	     */
	    public boolean isEmpty() {
	        return first == null;
	    }

	    /**
	     * Returns the number of items in this stack.
	     *
	     * @return the number of items in this stack
	     */
	    public int size() {
	        return n;
	    }

	    /**
	     * Adds the item to this stack.
	     *
	     * @param  item the item to add
	     */
	    public void push(Sensor sensor) {
	        Node oldfirst = first;
	        first = new Node();
	        first.sensor = sensor;
	        first.next = oldfirst;
	        n++;
	    }

	    /**
	     * Removes and returns the item most recently added to this stack.
	     *
	     * @return the item most recently added
	     * @throws NoSuchElementException if this stack is empty
	     */
	    public Sensor pop() {
	        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
	        Sensor item = first.sensor;     // save item to return
	        first = first.next;            // delete first node
	        n--;
	        return item;                   // return the saved item
	    }


	    /**
	     * Returns (but does not remove) the item most recently added to this stack.
	     *
	     * @return the item most recently added to this stack
	     * @throws NoSuchElementException if this stack is empty
	     */
	    public Sensor peek() {
	        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
	        return first.sensor;
	    }

	
}
