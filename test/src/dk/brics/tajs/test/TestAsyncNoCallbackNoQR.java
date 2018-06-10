package dk.brics.tajs.test;

import dk.brics.tajs.Main;
import dk.brics.tajs.options.Options;
import org.junit.Before;
import org.junit.Test;


public class TestAsyncNoCallbackNoQR {
    @Before
    public void init() {
        Main.reset();
        Options.get().enableTest();
        Options.get().enableContextSensitiveHeap();
        Options.get().enableAsyncEvents();
        Options.get().disableQRSensitivity();
        Options.get().disableCallbackSensitivity();
        Options.get().getSoundnessTesterOptions().setTest(false);
    }

    @Test
    public void async_no_qr_callback_01() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test04.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_02() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test05.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_03() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test08.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_04() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test09.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_05() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test11.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_06() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test13.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_07() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test16.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_08() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test17.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_09() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test18.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_10() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test19.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_11() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test20.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_12() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test22.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_13() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test34.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_14() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test25.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_15() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test26.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_16() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test27.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_17() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test28.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_18() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test30.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_19() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test31.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_20() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test32.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_21() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test33.js");
        Misc.checkSystemOutput();
    }

    @Test
    public void async_no_qr_callback_22() {
        Options.get().enableCallbackGraphPrint();
        Misc.run("test-resources/src/async/test35.js");
        Misc.checkSystemOutput();
    }
}
