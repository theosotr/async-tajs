package dk.brics.tajs.test;

import dk.brics.tajs.Main;
import dk.brics.tajs.options.Options;
import org.junit.Before;
import org.junit.Test;


public class TestAsyncDisabledSensitivities {
    @Before
    public void init() {
        Main.reset();
        Options.get().enableTest();
        Options.get().enableContextSensitiveHeap();
        Options.get().enableAsyncEvents();
        Options.get().getSoundnessTesterOptions().setTest(false);
    }

    @Test
    public void async_disabled_01() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test04.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_02() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test05.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_03() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test08.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_04() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test09.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_05() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test11.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_06() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test13.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_07() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test16.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_08() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test17.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_09() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test18.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_10() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test19.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_11() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test20.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_12() {
        Options.get().disableQRSensitivity();
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test22.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_13() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test34.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_14() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test25.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_15() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test26.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_16() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test27.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_17() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test28.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_18() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test30.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_19() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test31.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_20() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test32.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_21() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test33.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_disabled_22() {
        Options.get().enableCallbackGraphPrint();
        Options.get().disableQRSensitivity();
        Misc.run("test-resources/src/async/test35.js");
        Misc.checkSystemOutput();
    }
}
