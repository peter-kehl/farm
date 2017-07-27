/**
 * Copyright (c) 2016-2017 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.tk.project;

import com.zerocracy.jstk.Farm;
import com.zerocracy.jstk.Project;
import com.zerocracy.tk.RsPage;
import com.zerocracy.tk.XeXsl;
import java.io.IOException;
import java.util.Properties;
import org.takes.Response;
import org.takes.facets.fork.RqRegex;
import org.takes.facets.fork.TkRegex;
import org.takes.rq.RqHref;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;

/**
 * Artifact page.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.12
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class TkArtifact implements TkRegex {

    /**
     * Properties.
     */
    private final Properties props;

    /**
     * Farm.
     */
    private final Farm farm;

    /**
     * Ctor.
     * @param pps Properties
     * @param frm Farm
     */
    public TkArtifact(final Properties pps, final Farm frm) {
        this.props = pps;
        this.farm = frm;
    }

    @Override
    public Response act(final RqRegex req) throws IOException {
        return new RsPage(
            this.props,
            "/xsl/artifact.xsl",
            req,
            () -> {
                final Project project = new RqProject(this.farm, req).value();
                final String artifact = new RqHref.Smart(
                    new RqHref.Base(req)
                ).single("a");
                return new XeChain(
                    new XeAppend("project", project.toString()),
                    new XeAppend("artifact", artifact),
                    new XeXsl(
                        project,
                        String.format(
                            "%s.xml", artifact.replaceAll("^.+/", "")
                        ),
                        String.format("%s.xsl", artifact)
                    )
                );
            }
        );
    }

}