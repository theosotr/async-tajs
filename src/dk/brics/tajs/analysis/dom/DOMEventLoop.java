/*
 * Copyright 2009-2015 Aarhus University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.brics.tajs.analysis.dom;

import dk.brics.tajs.analysis.FunctionCalls;
import dk.brics.tajs.analysis.Solver;
import dk.brics.tajs.flowgraph.jsnodes.EventDispatcherNode;
import dk.brics.tajs.lattice.ObjectLabel;
import dk.brics.tajs.lattice.State;
import dk.brics.tajs.lattice.Value;
import dk.brics.tajs.options.Options;
import org.apache.log4j.Logger;

import java.util.Set;

public class DOMEventLoop {

    private static Logger log = Logger.getLogger(DOMEventLoop.class);

    private static DOMEventLoop instance;

    private final Value loadEvent;

    private final Value keyboardEvent;

    private final Value mouseEvent;

    private final Value ajaxEvent;

    private final Value anyEvent;

    private DOMEventLoop() {
        anyEvent = DOMEvents.createAnyEvent();

        loadEvent = DOMEvents.createAnyLoadEvent();

        if (Options.get().isSingleEventHandlerType()) {
            keyboardEvent = anyEvent;
        } else {
            keyboardEvent = DOMEvents.createAnyKeyboardEvent();
        }

        if (Options.get().isSingleEventHandlerType()) {
            mouseEvent = anyEvent;
        } else {
            mouseEvent = DOMEvents.createAnyMouseEvent();
        }

        if (Options.get().isSingleEventHandlerType()) {
            ajaxEvent = anyEvent;
        } else {
            ajaxEvent = DOMEvents.createAnyAjaxEvent();
        }
    }

    public static DOMEventLoop get() {

        if (instance == null) {
            instance = new DOMEventLoop();
        }
        return instance;
    }

    private static void triggerEventHandler(EventDispatcherNode currentNode, State currentState, DOMRegistry.MaySets eventhandlerKind, Value event, boolean requiresStateCloning, Solver.SolverInterface c) {
        Set<ObjectLabel> handlers = currentState.getExtras().getFromMaySet(eventhandlerKind.name());
        if (handlers.isEmpty()) {
            return;
        }
        State callState = requiresStateCloning ? currentState.clone() : currentState;
        for (ObjectLabel l : handlers) {
            log.debug("Triggering eventHandlers <" + eventhandlerKind + ">: " + l);
        }

        if (event != null) {
            callState.writeProperty(DOMWindow.WINDOW, "event", event);
        }

        c.setState(callState);
        FunctionCalls.callFunction(new FunctionCalls.EventHandlerCall(currentNode, Value.makeObject(handlers), event, callState), c);
        c.setState(currentState);
    }

    public void multipleNondeterministicEventLoops(EventDispatcherNode n, State state, Solver.SolverInterface c) {
        if (n.getType() == EventDispatcherNode.Type.LOAD) {
            triggerEventHandler(n, state, DOMRegistry.MaySets.LOAD_EVENT_HANDLER, loadEvent, false, c);
        }

        if (n.getType() == EventDispatcherNode.Type.UNLOAD) {
            triggerEventHandler(n, state, DOMRegistry.MaySets.UNLOAD_EVENT_HANDLERS, null, false, c);
        }

        if (n.getType() == EventDispatcherNode.Type.OTHER) {
            triggerEventHandler(n, state, DOMRegistry.MaySets.KEYBOARD_EVENT_HANDLER, keyboardEvent, true, c);
            triggerEventHandler(n, state, DOMRegistry.MaySets.MOUSE_EVENT_HANDLER, mouseEvent, true, c);
            triggerEventHandler(n, state, DOMRegistry.MaySets.AJAX_EVENT_HANDLER, ajaxEvent, true, c);
            triggerEventHandler(n, state, DOMRegistry.MaySets.UNKNOWN_EVENT_HANDLERS, anyEvent, true, c);
            triggerEventHandler(n, state, DOMRegistry.MaySets.TIMEOUT_EVENT_HANDLERS, null, true, c);
        }
    }
}
