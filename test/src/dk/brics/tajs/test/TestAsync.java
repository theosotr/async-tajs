package dk.brics.tajs.test;

import dk.brics.tajs.Main;
import dk.brics.tajs.options.Options;
import org.junit.Before;
import org.junit.Test;


public class TestAsync {

    @Before
    public void init() {
        Main.reset();
        Options.get().enableTest();
        Options.get().enableContextSensitiveHeap();
        Options.get().enableParameterSensitivity();
        Options.get().enableAsyncEvents();
        Options.get().getSoundnessTesterOptions().setTest(false);
    }

    @Test
    public void async_00() {
        Misc.run("test-resources/src/async/test00.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_01() {
        Misc.run("test-resources/src/async/test01.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_02() {
        Misc.run("test-resources/src/async/test02.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_03() {
        Misc.run("test-resources/src/async/test03.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_04() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test04.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_05() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test05.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_06() {
        Misc.run("test-resources/src/async/test06.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_07() {
        Misc.run("test-resources/src/async/test07.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_08() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test08.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_09() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test09.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_10() {
        Misc.run("test-resources/src/async/test10.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_11() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test11.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_12() {
        Misc.run("test-resources/src/async/test12.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_13() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test13.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_14() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test14.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_15() {
        Misc.run("test-resources/src/async/test15.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_16() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test16.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_17() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test17.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_18() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test18.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_19() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test19.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_20() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test20.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_21() {
        Options.get().enableNodeJS();
        Misc.run("test-resources/src/async/test21.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_22() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test22.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_23() {
        Misc.run("test-resources/src/async/test23.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_24() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test24.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_25() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test25.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_26() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test26.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_27() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test27.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_28() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test28.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_29() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test29.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_30() {
        Options.get().enableCallbackGraphPrint();
        Options.get().enableQRSensitivity();
        Misc.run("test-resources/src/async/test30.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_31() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test31.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_32() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test32.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_33() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test33.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_34() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test34.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_35() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test35.js");
        Misc.checkSystemOutput();
    }
}
