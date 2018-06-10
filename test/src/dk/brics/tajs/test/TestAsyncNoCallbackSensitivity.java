package dk.brics.tajs.test;

import dk.brics.tajs.Main;
import dk.brics.tajs.options.Options;
import org.junit.Before;
import org.junit.Test;


public class TestAsyncNoCallbackSensitivity {

    @Before
    public void init() {
        Main.reset();
        Options.get().enableTest();
        Options.get().enableAsyncEvents();
        Options.get().disableCallbackSensitivity();
        Options.get().getSoundnessTesterOptions().setTest(false);
    }

    @Test
    public void async_no_clb_01() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test04.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_02() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test05.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_03() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test07.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_04() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test08.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_05() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test09.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_06() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test10.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_07() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test11.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_08() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test13.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_09() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test14.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_10() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test15.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_11() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test16.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_12() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test17.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_13() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test18.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_14() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test19.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_15() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test20.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_16() {
        Options.get().enableNodeJS();
        Misc.run("test-resources/src/async/test21.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_17() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test22.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_18() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test24.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_19() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test25.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_20() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test26.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_21() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test27.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_22() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test28.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_23() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test29.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_24() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test30.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_25() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test31.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_26() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test32.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_27() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test33.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_clb_28() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test35.js");
        Misc.checkSystemOutput();
    }
}
