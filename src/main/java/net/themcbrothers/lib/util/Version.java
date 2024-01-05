// MIT License
//
// Copyright (c) 2017-2023 Aidan C. Brady
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
package net.themcbrothers.lib.util;

import net.neoforged.fml.ModContainer;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;

/**
 * Simple version handling.
 *
 * @param major       Major number for version
 * @param minor       Minor number for version
 * @param incremental Build number for version
 * @author AidanBrady
 */
public record Version(int major, int minor, int incremental) implements Comparable<Version> {
    /**
     * Builds a Version object from an Artifact Version
     */
    public Version(ArtifactVersion artifactVersion) {
        this(artifactVersion.getMajorVersion(), artifactVersion.getMinorVersion(), artifactVersion.getIncrementalVersion());
    }

    /**
     * Helper to make it so this is the only class with weird errors in IntelliJ (that don't actually exist), instead of having our main class also have "errors"
     */
    public Version(ModContainer container) {
        this(container.getModInfo().getVersion());
    }

    /**
     * Gets a version object from a string.
     *
     * @param s - string object
     * @return version if applicable, otherwise null
     */
    public static Version get(String s) {
        String[] split = s.replace('.', ':').split(":");
        if (split.length != 3) {
            return null;
        }

        int[] digits = new int[3];
        for (int i = 0; i < digits.length; i++) {
            try {
                digits[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return new Version(digits[0], digits[1], digits[2]);
    }

    @Override
    public int compareTo(@NotNull Version version) {
        if (version.major > major) {
            return -1;
        } else if (version.major == major) {
            if (version.minor > minor) {
                return -1;
            } else if (version.minor == minor) {
                return Integer.compare(incremental, version.incremental);
            }
        }
        return 1;
    }

    @Override
    public String toString() {
        return major + ":" + minor + ":" + incremental;
    }
}
