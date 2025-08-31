/*
 * MIT License
 *
 * Copyright (c) 2025 sucj
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package icu.suc.serverevents;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.ResourceLocation;

/**
 * A marker interface for objects that can listen to events.
 *
 * <p>Classes implementing {@code Listener} must provide the events they want to listen
 * to via the {@link #events()} method. Each event returned should have a type parameter
 * corresponding to an interface that the listener implements.</p>
 */
public interface Listener {
    /**
     * Returns all events that this listener wants to register to.
     *
     * <p>The listener is expected to implement the type parameter {@code T} of each event
     * returned. Registration is typically done via {@link ServerEvents#register(Listener)}.</p>
     *
     * @return an array of events that this listener should be registered to
     */
    Event<?>[] events();

    /**
     * Returns the phase or stage at which this listener should be executed.
     *
     * <p>By default, listeners are executed in {@link Event#DEFAULT_PHASE}. Implementing
     * classes can override this method to specify a custom phase.</p>
     *
     * @return the {@link ResourceLocation} representing the execution phase of this listener
     */
    default ResourceLocation phase() {
        return Event.DEFAULT_PHASE;
    }
}
