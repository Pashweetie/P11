import org.junit.*;

import static junit.framework.TestCase.assertEquals;


public class NodeTest {
    private Node first, second;

    @Before
    public void setUp(){
        first = new Node("expr", "5", null, null, null);
        second = new Node("expr", "6", null, null, null);
    }

    @Test
    public void evaluateSimple() {
        Node t1 = new Node("list", "plus", first, second, null);

        Item ans = t1.evaluate();
        assertEquals(11.0, ans.getNum());
    }
}