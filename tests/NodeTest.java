import org.junit.*;

import static junit.framework.TestCase.assertEquals;


public class NodeTest {
    private Node first, second;

    @Before
    public void setUp(){
        first = new Node("expr", "5", null, null, null);
        second = new Node("expr", "6", null, null, null);
    }

    /* Testing bif's with two arguments.
     * includes: "lt", "le", "eq", "ne", "and", "or",
     * "not", "plus", "minus", "times", "div", "ins"
     */
    @Test
    public void evaluatePlus() {
        Node t1 = new Node("list", "plus", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(11.0, ans.getNumber());
    }

    @Test
    public void evaluateMinus() {
        Node t1 = new Node("list", "minus", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(-1.0, ans.getNumber());
    }

    @Test
    public void evaluateTimes() {
        Node t1 = new Node("list", "times", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(30.0, ans.getNumber());
    }

    @Test
    public void evaluateDiv() {
        Node t1 = new Node("list", "div", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(5.0 / 6.0, ans.getNumber());
    }

    @Test
    public void evaluateLT() {
        Node t1 = new Node("list", "lt", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(1.0, ans.getNumber());
    }

    @Test
    public void evaluateLE() {
        Node t1 = new Node("list", "le", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(1.0, ans.getNumber());
    }

    @Test
    public void evaluateEQ() {
        Node t1 = new Node("list", "eq", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(0.0, ans.getNumber());
    }

    @Test
    public void evaluateNE() {
        Node t1 = new Node("list", "ne", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(1.0, ans.getNumber());
    }

    @Test
    public void evaluateAND() {
        Node t1 = new Node("list", "and", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(1.0, ans.getNumber());
    }

    @Test
    public void evaluateOR() {
        Node t1 = new Node("list", "or", first, second, null);

        Value ans = t1.evaluate();
        assertEquals(1.0, ans.getNumber());
    }
}