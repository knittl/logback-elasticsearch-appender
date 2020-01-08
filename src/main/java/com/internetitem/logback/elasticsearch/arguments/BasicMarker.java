package com.internetitem.logback.elasticsearch.arguments;

import org.slf4j.Marker;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/* Copy of {@link org.slf4j.helpers.BasicMarker} from slf4j-api v1.7.12,
 * with a minor change to make the constructor public,
 * so that it can be extended in other packages.
 * <p>
 * slf4j-api, {@link org.slf4j.helpers.BasicMarker}, and the portions
 * of this class that have been copied from BasicMarker are provided under
 * the MIT License copied here:
 *
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
/**
 * A simple implementation of the {@link Marker} interface.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 */
@SuppressWarnings("serial")
public class BasicMarker implements Marker {
    public static final String MARKER_NAME_PREFIX = "LS_";

    private static final String OPEN = "[ ";
    private static final String CLOSE = " ]";
    private static final String SEP = ", ";

    private final String name;
    private final List<Marker> referenceList = new CopyOnWriteArrayList<>();

    public BasicMarker(String name) {
        this.name = Objects.requireNonNull(name, "A marker name cannot be null");
    }

    public String getName() {
        return name;
    }

    public void add(Marker reference) {
        Objects.requireNonNull(reference, "A null value cannot be added to a Marker as reference.");

        // no point in adding the reference multiple times
        if (this.contains(reference)) {
            return;
        }

        if (reference.contains(this)) { // avoid recursion
            // a potential reference should not contain its future "parent" as a reference
            return;
        }

        referenceList.add(reference);
    }

    @Override
    public boolean hasReferences() {
        return !referenceList.isEmpty();
    }

    @Override
    @Deprecated
    public boolean hasChildren() {
        return hasReferences();
    }

    @Override
    public Iterator<Marker> iterator() {
        return referenceList.iterator();
    }

    @Override
    public boolean remove(Marker referenceToRemove) {
        return referenceList.remove(referenceToRemove);
    }

    @Override
    public boolean contains(Marker other) {
        Objects.requireNonNull(other, "Other cannot be null");

        if (this.equals(other)) {
            return true;
        }

        return referenceList.contains(other);
    }

    /**
     * This method is mainly used with Expression Evaluators.
     */
    @Override
    public boolean contains(String name) {
        Objects.requireNonNull(name, "Other name cannot be null");

        if (this.name.equals(name)) {
            return true;
        }

        for (Marker ref : referenceList) {
            if (ref.contains(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Marker))
            return false;

        final Marker other = (Marker) obj;
        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        String self = toStringSelf();
        if (!this.hasReferences()) {
            return self;
        }

        Iterator<Marker> it = this.iterator();
        Marker reference;
        StringBuilder sb = new StringBuilder(this.getName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            reference = it.next();
            final String referenceName = reference.getName();
            sb.append(referenceName);
            if (it.hasNext()) {
                sb.append(SEP);
            }
        }
        sb.append(CLOSE);

        return sb.toString();
    }

    public String toStringSelf() {
        return getName();
    }
}